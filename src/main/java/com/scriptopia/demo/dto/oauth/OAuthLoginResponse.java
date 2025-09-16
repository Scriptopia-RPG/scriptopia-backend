package com.scriptopia.demo.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthLoginResponse {
    private LoginStatus status;
    private String accessToken;
    private String signupToken;
}

