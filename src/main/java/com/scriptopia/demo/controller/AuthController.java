package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.auth.*;
import com.scriptopia.demo.service.LocalAccountService;
import com.scriptopia.demo.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LocalAccountService localAccountService;
    private final RefreshTokenService refreshTokenService;

    private static final String RT_COOKIE = "RT";
    private static final boolean COOKIE_SECURE = true;
    private static final String COOKIE_SAMESITE = "None";



    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = RT_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.logout(refreshToken);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, localAccountService.removeRefreshCookie().toString());
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        return ResponseEntity.ok(localAccountService.login(req, request, response));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        localAccountService.register(request);
        return ResponseEntity.ok("회원가입에 성공했습니다.");
    }

    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {

        localAccountService.verifyEmail(request);

        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }


    @PostMapping("/email/code/send")
    public ResponseEntity<String> sendCode(@RequestBody @Valid SendCodeRequest request) {
        localAccountService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
    }

    @PostMapping("/email/code/verify")
    public ResponseEntity<String> verifyCode(@RequestBody @Valid VerifyCodeRequest request) {
        localAccountService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");

    }


    @PostMapping("/password/reset/send")
    public ResponseEntity<?> sendResetMail(@Valid @RequestBody SendCodeRequest request){

        localAccountService.sendResetPasswordMail(request.getEmail());

        return ResponseEntity.ok("비밀번호 초기화 링크를 전송했습니다.");
    }


    @PatchMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        localAccountService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }


    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PatchMapping("/password/change")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                                 Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        localAccountService.changePassword(userId,request);

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }



}
