package com.scriptopia.demo.dto.localaccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class changePasswordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}
