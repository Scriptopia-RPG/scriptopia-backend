package com.scriptopia.demo.dto.oauth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialSignupRequest {
    @NotBlank(message = "E_400_MISSING_NICKNAME")
    private String nickname;
    private String deviceId;
    private String signupToken;
}
