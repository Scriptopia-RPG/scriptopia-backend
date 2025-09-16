package com.scriptopia.demo.service;

import com.scriptopia.demo.config.JwtProperties;
import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.dto.auth.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.LocalAccountRepository;
import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.repository.UserSettingRepository;
import com.scriptopia.demo.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.thymeleaf.util.StringUtils.length;

@Service
@Transactional
@RequiredArgsConstructor
public class LocalAccountService {

    private final StringRedisTemplate redisTemplate;
    private final LocalAccountRepository localAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtProvider jwt;
    private final RefreshTokenService refreshService;
    private final JwtProperties prop;
    private final MailService mailService;

    private static final String RT_COOKIE = "RT";
    private static final boolean COOKIE_SECURE = true;
    private static final String COOKIE_SAMESITE = "None";


    private static final Pattern WS = Pattern.compile("[\\s\\p{Z}\\u200B\\u200C\\u200D\\uFEFF]");

    private static final long TOKEN_EXPIRATION = 30;
    private final UserSettingRepository userSettingRepository;


    @Transactional
    public void resetPassword(String token,String newPassword) {

        String key = "reset:token:" + token;
        String email = redisTemplate.opsForValue().get(key);
        if (email == null) {
            throw new CustomException(ErrorCode.E_401);
        }

        LocalAccount localAccount = localAccountRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        localAccount.setPassword(passwordEncoder.encode(newPassword));
        localAccountRepository.save(localAccount);

        redisTemplate.delete(key);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        String email = request.getEmail();

        if (localAccountRepository.existsByEmail(email)){
            throw new CustomException(ErrorCode.E_409_EMAIL_TAKEN);
        }

    }

    @Transactional
    public void sendVerificationCode(String email) {
        if (localAccountRepository.existsByEmail(email)){
            throw new CustomException(ErrorCode.E_409_EMAIL_TAKEN);
        }
        String code = String.format("%06d", (int)(Math.random() * 999999));
        mailService.saveCode(email, code);
        mailService.sendVerificationCode(email, code);
    }

    @Transactional
    public void sendResetPasswordMail(String email) {

        if (!localAccountRepository.existsByEmail(email)){
            throw new CustomException(ErrorCode.E_404_USER_NOT_FOUND);
        }

        String resetToken = createResetToken(email);
        mailService.sendResetLink(email, resetToken);
    }

    @Transactional
    public void verifyCode(String email, String inputCode) {

        if (length(inputCode) != 6){
            throw new CustomException(ErrorCode.E_400_INVALID_CODE);
        }

        String savedCode = redisTemplate.opsForValue().get("email:verify:" + email);

        if (savedCode != null && savedCode.equals(inputCode)) {
            // 인증 완료 후 30분 유지
            redisTemplate.opsForValue().set("email:verified:" + email, "true", TOKEN_EXPIRATION, TimeUnit.MINUTES);
            redisTemplate.delete("email:verify:" + email); // 코드 제거
        }
        else{
            throw new CustomException(ErrorCode.E_401_CODE_MISMATCH);
        }

    }



    @Transactional
    public void register(RegisterRequest request) {
        String email = request.getEmail();

        //중복 검증
        if (localAccountRepository.existsByEmail(email)){
            throw new CustomException(ErrorCode.E_409_EMAIL_TAKEN);
        }

        String verified = redisTemplate.opsForValue().get("email:verified:" + email);

        //이메일 인증 검증
        if (verified == null || !verified.equals("true")) {
            throw new CustomException(ErrorCode.E_412_EMAIL_NOT_VERIFIED);
        }

        // 공백 검증
        if (WS.matcher(request.getPassword()).find()) {
            throw new CustomException(ErrorCode.E_400_PASSWORD_WHITESPACE);
        }

        isAvailable(email, request.getNickname());

        //user 객체 생성
        User user = new User();
        user.setNickname(request.getNickname());
        user.setPia(0L);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(null);
        user.setProfileImgUrl(null);
        user.setRole(Role.USER);
        user.setLoginType(LoginType.LOCAL);
        userRepository.save(user);

        //localAccount 객체 생성
        LocalAccount localAccount = new LocalAccount();
        localAccount.setUser(user);
        localAccount.setEmail(email);
        localAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        localAccount.setUpdatedAt(LocalDateTime.now());
        localAccount.setStatus(UserStatus.UNVERIFIED);
        localAccountRepository.save(localAccount);

        //환경 설정 초기 값
        UserSetting userSetting = new UserSetting();
        userSetting.setUser(user);
        userSetting.setTheme(Theme.DARK);
        userSetting.setFontType(FontType.PretendardVariable);
        userSetting.setFontSize(16);
        userSetting.setLineHeight(1);
        userSetting.setWordSpacing(1);
        userSetting.setUpdatedAt(LocalDateTime.now());
        userSettingRepository.save(userSetting);

    }

    @Transactional
    public LoginResponse login(LoginRequest req, HttpServletRequest request, HttpServletResponse response) {

        LocalAccount localAccount = localAccountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.E_401_INVALID_CREDENTIALS));


        if (!passwordEncoder.matches(req.getPassword(), localAccount.getPassword())) {
            throw new CustomException(ErrorCode.E_401_INVALID_CREDENTIALS);
        }


        User user = localAccount.getUser();
        user.setLastLoginAt(LocalDateTime.now());

        List<String> roles = List.of(user.getRole().toString());
        String access  = jwt.createAccessToken(user.getId(), roles);
        String refresh = jwt.createRefreshToken(user.getId(), req.getDeviceId());

        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        refreshService.saveLoginRefresh(user.getId(), refresh, req.getDeviceId(), ip, ua);

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(refresh).toString());


        return new LoginResponse(access, prop.accessExpSeconds(), user.getRole());
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {

        //현재, 변경 비밀번호 불일치
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new CustomException(ErrorCode.E_400_PASSWORD_CONFIRM_MISMATCH);
        }

        // 공백 검증
        if (WS.matcher(request.getNewPassword()).find()) {
            throw new CustomException(ErrorCode.E_400_PASSWORD_WHITESPACE);
        }

        LocalAccount localAccount = localAccountRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));


        //현재 암호 불일치
        if (!passwordEncoder.matches(request.getOldPassword(), localAccount.getPassword())) {
            throw new CustomException(ErrorCode.E_401_CURRENT_PASSWORD_MISMATCH);
        }

        //이전과 동일한 암호
        if (passwordEncoder.matches(request.getNewPassword(), localAccount.getPassword())) {
            throw new CustomException(ErrorCode.E_409_PASSWORD_SAME_AS_OLD);
        }

        localAccount.setPassword(passwordEncoder.encode(request.getNewPassword()));

    }

    public String createResetToken(String email) {
        String token = UUID.randomUUID().toString();

        redisTemplate.opsForValue()
                .set("reset:token:" + token, email, TOKEN_EXPIRATION, TimeUnit.MINUTES);

        return token;
    }



    public List<String> getRoles(Long userId) {
        return List.of(Role.USER.toString());
    }


    private void isAvailable(String email, String nickname) {
        if (localAccountRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.E_409_EMAIL_TAKEN);
        }

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.E_409_NICKNAME_TAKEN);
        }

    }

    public ResponseCookie refreshCookie(String value) {
        return ResponseCookie.from(RT_COOKIE, value)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAMESITE)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();
    }

    public ResponseCookie removeRefreshCookie() {
        return ResponseCookie.from(RT_COOKIE, "")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAMESITE)
                .path("/")
                .maxAge(0)
                .build();
    }
}
