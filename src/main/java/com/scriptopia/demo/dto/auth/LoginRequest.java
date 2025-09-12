package com.scriptopia.demo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "E_400_MISSING_EMAIL")
    @Email(message = "E_400_INVALID_EMAIL_FORMAT")
    private String email;

    @NotBlank(message = "E_400_MISSING_PASSWORD")
    private String password;

    @NotBlank(message = "디바이스 식별값이 필요합니다.")
    private String deviceId;

}
