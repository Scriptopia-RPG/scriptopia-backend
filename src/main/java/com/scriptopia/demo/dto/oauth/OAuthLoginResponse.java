package com.scriptopia.demo.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthLoginResponse {
    private LoginStatus status;   // LOGIN_SUCCESS or SIGNUP_REQUIRED
    private String accessToken;
    private String refreshToken;

    // 회원가입 필요할 때만 내려줌
    private String socialId;
    private String email;
    private String provider;
}

