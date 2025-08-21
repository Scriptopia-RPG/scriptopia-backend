package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.user.LoginRequest;
import com.scriptopia.demo.dto.user.RefreshRequest;
import com.scriptopia.demo.dto.user.TokenResponse;
import com.scriptopia.demo.service.LocalAccountService;
import com.scriptopia.demo.utils.JwtProvider;
import com.scriptopia.demo.utils.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LocalAccountService localAccountService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwt;
    private final RefreshTokenService refreshSvc;

    private static final String RT_COOKIE = "RT";
    private static final boolean COOKIE_SECURE = true;   // HTTPS면 true
    private static final String COOKIE_SAMESITE = "None"; // 동일 도메인이면 "Lax"

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        var user = localAccountService.loadByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("invalid credentials");
        }

        List<String> roles = user.getRoles();
        String access  = jwt.createAccessToken(user.getId(), roles);
        String refresh = jwt.createRefreshToken(user.getId(), req.getDeviceId());

        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        refreshSvc.saveLoginRefresh(user.getId(), refresh, req.getDeviceId(), ip, ua);

        // 쿠키에 RT 넣기
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(refresh).toString());

        return ResponseEntity.ok(new TokenResponse(access, null));
    }

    // 쿠키 기반 리프레시(권장)
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(name = RT_COOKIE, required = false) String refreshToken,
            @RequestParam(required = false) String deviceId
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        Long userId = jwt.getUserId(refreshToken);
        List<String> roles = localAccountService.getRoles(userId);

        var pair = refreshSvc.rotate(refreshToken, deviceId, roles);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(pair.refreshToken()).toString())
                .body(new TokenResponse(pair.accessToken(), null));
    }

    // 바디 기반 리프레시(옵션: 쿠키 미사용 시)
    @PostMapping("/refresh/body")
    public ResponseEntity<TokenResponse> refreshBody(@RequestBody @Valid RefreshRequest req) {
        Long userId = jwt.getUserId(req.getRefreshToken());
        List<String> roles = localAccountService.getRoles(userId);
        var pair = refreshSvc.rotate(req.getRefreshToken(), req.getDeviceId(), roles);
        return ResponseEntity.ok(new TokenResponse(pair.accessToken(), pair.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = RT_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshSvc.logout(refreshToken);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, removeRefreshCookie().toString());
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie refreshCookie(String value) {
        // RT 만료기간에 맞춰 maxAge를 조정하고 싶으면 JwtProvider.getExpiry(...)로 계산 가능
        return ResponseCookie.from(RT_COOKIE, value)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAMESITE)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();
    }

    private ResponseCookie removeRefreshCookie() {
        return ResponseCookie.from(RT_COOKIE, "")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAMESITE)
                .path("/")
                .maxAge(0)
                .build();
    }
}
