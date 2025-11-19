package com.scriptopia.demo.controller;


import com.scriptopia.demo.config.JwtProperties;
import com.scriptopia.demo.dto.auth.RefreshResponse;
import com.scriptopia.demo.dto.token.RefreshRequest;
import com.scriptopia.demo.service.LocalAccountService;
import com.scriptopia.demo.service.RefreshTokenService;
import com.scriptopia.demo.utils.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/token")
@Tag(name = "리프레쉬 토큰 관련 API", description = "리프레쉬 토큰 관련 API 입니다.")
@RequiredArgsConstructor
public class refreshController {

    private final LocalAccountService localAccountService;
    private final JwtProvider jwt;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties props;

    private static final String RT_COOKIE = "RT";
    private static final boolean COOKIE_SECURE = false;
    private static final String COOKIE_SAMESITE = "Lax";

    @Operation(summary = "리프레시 토큰 재발급")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @CookieValue(name = RT_COOKIE, required = false) String refreshToken,
            @RequestBody RefreshRequest request
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        Long userId = jwt.getUserId(refreshToken);
        List<String> roles = localAccountService.getRoles(userId);

        var pair = refreshTokenService.rotate(refreshToken, request.getDeviceId(), roles);
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
