package com.scriptopia.demo.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {


    @NotBlank(message = "E_400_MISSING_PASSWORD")
    @Size(min = 8, max = 20, message = "E_400_PASSWORD_SIZE")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).+$",
            message = "E_400_PASSWORD_COMPLEXITY"
    )
    private String oldPassword;

    @NotBlank(message = "E_400_MISSING_PASSWORD\"")
    @Size(min = 8, max = 20, message = "E_400_PASSWORD_SIZE")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).+$",
            message = "E_400_PASSWORD_COMPLEXITY"
    )
    private String newPassword;

    private String confirmPassword;
}
