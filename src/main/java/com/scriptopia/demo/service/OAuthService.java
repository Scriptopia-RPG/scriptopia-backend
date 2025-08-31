package com.scriptopia.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.dto.localaccount.LoginResponse;
import com.scriptopia.demo.dto.oauth.LoginStatus;
import com.scriptopia.demo.dto.oauth.OAuthLoginResponse;
import com.scriptopia.demo.dto.oauth.OAuthUserInfo;
import com.scriptopia.demo.dto.oauth.SocialSignupRequest;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.SocialAccountRepository;
import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.utils.GoogleClient;
import com.scriptopia.demo.utils.JwtProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OAuthService {


    private static final String RT_COOKIE = "RT";
    private static final boolean COOKIE_SECURE = true;
    private static final String COOKIE_SAMESITE = "None";

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService; // RefreshToken 관리 서비스
    private final GoogleClient googleClient;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public OAuthLoginResponse login(String provider, String code,
                                    HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        Provider providerEnum = Provider.valueOf(provider.toUpperCase());

        OAuthUserInfo userInfo = fetchUserInfoFromProvider(provider, code);

        Optional<SocialAccount> accountOpt =
                socialAccountRepository.findBySocialIdAndProvider(userInfo.getId(), providerEnum);

        String json = objectMapper.writeValueAsString(userInfo);
        if (accountOpt.isEmpty()) {
            String signupToken = UUID.randomUUID().toString();
            // Redis TTL = 5분
            redisTemplate.opsForValue().set("signup:" + signupToken, json, 5, TimeUnit.MINUTES);

            return OAuthLoginResponse.builder()
                    .status(LoginStatus.SIGNUP_REQUIRED)
                    .signupToken(signupToken)
                    .build();
        }

        User user = accountOpt.get().getUser();
        user.setLastLoginAt(LocalDateTime.now());

        try {
            List<String> roles = List.of(user.getRole().toString());
            String access = jwtProvider.createAccessToken(user.getId(), roles);
            String refresh = jwtProvider.createRefreshToken(user.getId(), "SOCIAL-" + provider);

            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");
            refreshTokenService.saveLoginRefresh(user.getId(), refresh, "SOCIAL-" + provider, ip, ua);

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(refresh).toString());

            return OAuthLoginResponse.builder()
                    .status(LoginStatus.LOGIN_SUCCESS)
                    .accessToken(access)
                    .build();

        } catch (JwtException e) {
            throw new CustomException(ErrorCode.E_500_TOKEN_CREATION_FAILED);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.E_500_TOKEN_STORAGE_FAILED);
        }
    }

    @Transactional
    public OAuthLoginResponse signup(SocialSignupRequest req,
                                     HttpServletRequest request, HttpServletResponse response) {
        String key = "signup:" + req.getSignupToken();

        try {

            String json = (String) redisTemplate.opsForValue().get(key);
            if (json == null) {
                throw new CustomException(ErrorCode.E_400_INVALID_SOCIAL_LOGIN_CODE);
            }

            OAuthUserInfo userInfo = objectMapper.readValue(json, OAuthUserInfo.class);

            Provider provider = Provider.valueOf(userInfo.getProvider().toUpperCase());

            if (socialAccountRepository.existsBySocialIdAndProvider(userInfo.getId(), provider)) {
                throw new CustomException(ErrorCode.E_409_EMAIL_TAKEN);
            }

            if (userRepository.existsByNickname(req.getNickname())) {
                throw new CustomException(ErrorCode.E_409_NICKNAME_TAKEN);
            }


            User user = new User();
            user.setNickname(req.getNickname());
            user.setPia(0L);
            user.setCreatedAt(LocalDateTime.now());
            user.setLastLoginAt(LocalDateTime.now());
            user.setProfileImgUrl(null);
            user.setRole(Role.USER);
            user.setLoginType(LoginType.SOCIAL);
            userRepository.save(user);

            SocialAccount account = new SocialAccount();
            account.setUser(user);
            account.setSocialId(userInfo.getId());
            account.setEmail(userInfo.getEmail());
            account.setProvider(provider);
            socialAccountRepository.save(account);

            List<String> roles = List.of(user.getRole().toString());
            String deviceId = "SOCIAL-" + provider.name();
            String access = jwtProvider.createAccessToken(user.getId(), roles);
            String refresh = jwtProvider.createRefreshToken(user.getId(), deviceId);

            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");
            refreshTokenService.saveLoginRefresh(user.getId(), refresh, deviceId, ip, ua);

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(refresh).toString());

            redisTemplate.delete(key);

            return OAuthLoginResponse.builder()
                    .status(LoginStatus.LOGIN_SUCCESS)
                    .accessToken(access)
                    .build();

        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.E_500_DATABASE_ERROR);
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

    private OAuthUserInfo fetchUserInfoFromProvider(String provider, String code) {
        switch (provider.toLowerCase()) {
            case "google":
                return googleClient.getUserInfo(code);
            case "naver":
                // return naverClient.getUserInfo(code);
            case "kakao":
                // return kakaoClient.getUserInfo(code);
            default:
                throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
        }
    }
}
