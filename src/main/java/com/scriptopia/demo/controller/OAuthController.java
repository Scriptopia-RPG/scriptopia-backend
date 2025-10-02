package com.scriptopia.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scriptopia.demo.dto.oauth.OAuthLoginResponse;
import com.scriptopia.demo.dto.oauth.SocialSignupRequest;
import com.scriptopia.demo.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@Tag(name = "소셜 인증 관련 API", description = "소셜 인증 관련 API 입니다.")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @Operation(summary = "Oauth 로그인 url 발급")
    @GetMapping("/authorize")
    public ResponseEntity<String> getAuthorizationUrl(@RequestParam("provider") String provider) {
        return ResponseEntity.ok(oAuthService.buildAuthorizationUrl(provider));
    }

    @Operation(summary = "소셜 로그인")
    @GetMapping("/{provider}")
    public ResponseEntity<OAuthLoginResponse> login(
            @PathVariable("provider") String provider,
            @RequestParam("code") String code,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws JsonProcessingException {
        OAuthLoginResponse result = oAuthService.login(provider, code, request, response);
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "소셜 회원가입")
    @PostMapping("/register")
    public ResponseEntity<OAuthLoginResponse> signup(
            @RequestBody SocialSignupRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        OAuthLoginResponse result = oAuthService.signup(req, request, response);
        return ResponseEntity.ok(result);
    }

}
