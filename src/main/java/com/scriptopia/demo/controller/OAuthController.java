package com.scriptopia.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scriptopia.demo.dto.localaccount.LoginResponse;
import com.scriptopia.demo.dto.oauth.OAuthLoginResponse;
import com.scriptopia.demo.dto.oauth.SocialSignupRequest;
import com.scriptopia.demo.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

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

    @PostMapping("/register")
    public ResponseEntity<OAuthLoginResponse> signup(
            @RequestBody SocialSignupRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        OAuthLoginResponse result = oAuthService.signup(req, request, response);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> getAuthorizationUrl(@RequestParam("provider") String provider) {
        return ResponseEntity.ok(oAuthService.buildAuthorizationUrl(provider));
    }
}
