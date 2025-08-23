package com.scriptopia.demo.service;

import com.scriptopia.demo.config.JwtProperties;
import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.dto.user.LoginRequest;
import com.scriptopia.demo.dto.user.LoginResponse;
import com.scriptopia.demo.dto.user.RegisterRequest;
import com.scriptopia.demo.repository.LocalAccountRepository;
import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.utils.JwtProvider;
import com.scriptopia.demo.utils.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
public class LocalAccountService {

    private final LocalAccountRepository localAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtProvider jwt;
    private final RefreshTokenService refreshService;
    private final JwtProperties prop;

    private static final String RT_COOKIE = "RT";
    private static final boolean COOKIE_SECURE = true;
    private static final String COOKIE_SAMESITE = "None";

    @Transactional
    public void register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        validateParams(normalizedEmail, request.getPassword(), request.getNickname());
        isAvailable(normalizedEmail, request.getNickname());

        //user 객체 생성
        User user = new User();
        user.setNickname(request.getNickname());
        user.setPia(0L);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(null);
        user.setProfileImgUrl(null);
        user.setRole(Role.USER);
        userRepository.save(user);

        //localAccount 객체 생성
        LocalAccount localAccount = new LocalAccount();
        localAccount.setUser(user);
        localAccount.setEmail(normalizedEmail);
        localAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        localAccount.setUpdatedAt(LocalDateTime.now());
        localAccountRepository.save(localAccount);

        //환경 설정 초기 값
        UserSetting userSetting = new UserSetting();
        userSetting.setTheme(Theme.DARK);
        userSetting.setFontType(FontType.PretendardVariable);
        userSetting.setFontSize(16);
        userSetting.setLineHeight(1);
        userSetting.setUpdatedAt(LocalDateTime.now());

    }

    @Transactional
    public LoginResponse login(LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        LocalAccount localAccount = localAccountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("아이디 혹은 비밀번호를 잘못 입력했습니다."));

        if (!passwordEncoder.matches(req.getPassword(), localAccount.getPassword())) {
            throw new IllegalArgumentException("아이디 혹은 비밀번호를 잘못 입력했습니다.");
        }


        User user = localAccount.getUser();
        user.setLastLoginAt(LocalDateTime.now());

        List<String> roles = List.of(Role.USER.toString());
        String access  = jwt.createAccessToken(user.getId(), roles);
        String refresh = jwt.createRefreshToken(user.getId(), req.getDeviceId());

        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        refreshService.saveLoginRefresh(user.getId(), refresh, req.getDeviceId(), ip, ua);

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(refresh).toString());


        return new LoginResponse(access, prop.accessExpSeconds(), user.getRole());
    }



    public List<String> getRoles(Long userId) {
        return List.of(Role.USER.toString());
    }

    //대 소문자 구별
    private static String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static void validateParams(String email, String rawPassword, String nickname) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임 입력해주세요.");
        }
    }

    private void isAvailable(String email, String nickname) {
        if (localAccountRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException(nickname);
        }

    }

    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String email) {
            super("이미 존재하는 이메일입니다.: " + email);
        }
    }

    public static class DuplicateNicknameException extends RuntimeException {
        public DuplicateNicknameException(String nickname) {
            super("이미 존재하는 닉네임입니다.: " + nickname);
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
