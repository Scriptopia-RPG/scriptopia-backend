package com.scriptopia.demo.controller;

import com.scriptopia.demo.config.JwtProperties;
import com.scriptopia.demo.dto.user.LoginRequest;
import com.scriptopia.demo.dto.user.LoginResponse;
import com.scriptopia.demo.dto.user.RegisterRequest;
import com.scriptopia.demo.dto.user.RefreshResponse;
import com.scriptopia.demo.service.LocalAccountService;
import com.scriptopia.demo.utils.JwtProvider;
import com.scriptopia.demo.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LocalAccountService localAccountService;
    private final JwtProvider jwt;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties props;

    private static final String RT_COOKIE = "RT";
    private static final boolean COOKIE_SECURE = true;
    private static final String COOKIE_SAMESITE = "None";



    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterRequest registerRequest
    ) {
        localAccountService.register(registerRequest);
        return ResponseEntity.status(201).build();

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        return ResponseEntity.ok(localAccountService.login(req, request, response));
    }

    // 쿠키 기반 리프레시
    @PostMapping("/token/refresh")
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

    @PostMapping("/logout")
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
