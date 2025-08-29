package com.scriptopia.demo.controller;

import com.scriptopia.demo.config.JwtProperties;
import com.scriptopia.demo.dto.localaccount.*;
import com.scriptopia.demo.service.LocalAccountService;
import com.scriptopia.demo.utils.JwtProvider;
import com.scriptopia.demo.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final LocalAccountService localAccountService;
    private final JwtProvider jwt;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties props;

    private static final String RT_COOKIE = "RT";
    private static final boolean COOKIE_SECURE = true;
    private static final String COOKIE_SAMESITE = "None";

    @PostMapping("/public/auth/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        return ResponseEntity.ok(localAccountService.login(req, request, response));
    }

    @PostMapping("/user/auth/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = RT_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.logout(refreshToken);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, localAccountService.removeRefreshCookie().toString());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/public/auth/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterRequest registerRequest
    ) {
        localAccountService.register(registerRequest);
        return ResponseEntity.ok("회원가입에 성공했습니다.");
    }

    @PostMapping("/public/auth/send-code")
    public ResponseEntity<String> sendCode(@RequestBody @Valid SendCodeRequest sendCodeRequest) {
        localAccountService.sendVerificationCode(sendCodeRequest.getEmail());
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
    }

    @PostMapping("/public/auth/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam String email,
                                             @RequestParam String code) {
        localAccountService.verifyCode(email, code);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");

    }



    @PatchMapping("/user/auth/password/change")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request,
                                                 Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        localAccountService.changePassword(userId,request);

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }


    // 쿠키 기반 리프레시
    @PostMapping("/user/auth/token/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @CookieValue(name = RT_COOKIE, required = false) String refreshToken,
            @RequestParam(required = false) String deviceId
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        Long userId = jwt.getUserId(refreshToken);
        List<String> roles = localAccountService.getRoles(userId);

        var pair = refreshTokenService.rotate(refreshToken, deviceId, roles);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(pair.refreshToken()).toString())
                .body(new RefreshResponse(pair.accessToken(), props.accessExpSeconds()));
    }





    private ResponseCookie refreshCookie(String value) {
        return ResponseCookie.from(RT_COOKIE, value)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAMESITE)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();
    }


}
