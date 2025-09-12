package com.scriptopia.demo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "E_400_MISSING_EMAIL")
    @Email(message = "E_400_INVALID_EMAIL_FORMAT")
    private String email;

    @Size(min = 8, max = 20, message = "E_400_PASSWORD_SIZE")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).+$",
            message = "E_400_PASSWORD_COMPLEXITY"
    )
    private String password;

    @NotBlank(message = "E_400_MISSING_NICKNAME")
    private String nickname;
}
