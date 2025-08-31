package com.scriptopia.demo.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialSignupRequest {
    private String nickname;
    private String deviceId;
    private String signupToken;
}
