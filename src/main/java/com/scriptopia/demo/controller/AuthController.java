package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.CommonResponse;
import com.scriptopia.demo.dto.auth.*;
import com.scriptopia.demo.service.LocalAccountService;
import com.scriptopia.demo.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "로컬 인증 API", description = "로컬 인증 관련 API 입니다.")
@RequiredArgsConstructor
public class AuthController {
    private final LocalAccountService localAccountService;
    private final RefreshTokenService refreshTokenService;
    private static final String RT_COOKIE = "RT";
    private static final boolean COOKIE_SECURE = true;
    private static final String COOKIE_SAMESITE = "None";


    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(
            @CookieValue(name = RT_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.logout(refreshToken);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, localAccountService.removeRefreshCookie().toString());
        return ResponseEntity.ok(new CommonResponse("로그아웃 되었습니다."));
    }

    @Operation(summary = "로컬 로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        return ResponseEntity.ok(localAccountService.login(req, request, response));
    }

    @Operation(summary = "로컬 계정 회원가입")
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(

            @RequestBody @Valid RegisterRequest req,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(localAccountService.register(req, request, response));
    }

    @Operation(summary = "이메일 중복 검증")
    @PostMapping("/email/verify")
    public ResponseEntity<CommonResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {

        localAccountService.verifyEmail(request);

        return ResponseEntity.ok(new CommonResponse("사용 가능한 이메일입니다."));
    }

    @Operation(summary = "이메일 인증 코드 전송")
    @PostMapping("/email/code/send")
    public ResponseEntity<CommonResponse> sendCode(@RequestBody @Valid SendCodeRequest request) {
        localAccountService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(new CommonResponse("인증 코드가 이메일로 발송되었습니다."));
    }

    @Operation(summary = "이메일 인증 코드 확인")
    @PostMapping("/email/code/verify")
    public ResponseEntity<CommonResponse> verifyCode(@RequestBody @Valid VerifyCodeRequest request) {
        localAccountService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(new CommonResponse("이메일 인증이 완료되었습니다."));
    }

    @Operation(summary = "비밀번호 초기화 링크 발송")
    @PostMapping("/password/reset/send")
    public ResponseEntity<CommonResponse> sendResetMail(@Valid @RequestBody SendCodeRequest request){

        localAccountService.sendResetPasswordMail(request.getEmail());

        return ResponseEntity.ok(new CommonResponse("비밀번호 초기화 링크를 전송했습니다."));
    }

    @Operation(summary = "비밀번호 초기화")
    @PatchMapping("/password/reset")
    public ResponseEntity<CommonResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        localAccountService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(new CommonResponse("비밀번호가 성공적으로 변경되었습니다."));
    }

    @Operation(summary = "비밀번호 재설정")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PatchMapping("/password/change")
    public ResponseEntity<CommonResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                                 Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        localAccountService.changePassword(userId,request);

        return ResponseEntity.ok(new CommonResponse("비밀번호가 성공적으로 변경되었습니다."));
    }



}
