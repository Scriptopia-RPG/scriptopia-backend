package com.scriptopia.demo.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "사용자 아이디", example = "userA@example.com")
    private String email;

    @NotBlank(message = "E_400_MISSING_PASSWORD")
    @Schema(description = "비밀번호", example = "userA!234")
    private String password;

    @NotBlank(message = "디바이스 식별값이 필요합니다.")
    @Schema(description = "디바이스 아이디", example = "1234")
    private String deviceId;

}
