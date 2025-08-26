package com.scriptopia.demo.dto.localaccount;

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


    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자리여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).+$",
            message = "비밀번호는 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String oldPassword;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자리여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).+$",
            message = "비밀번호는 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String newPassword;
}
