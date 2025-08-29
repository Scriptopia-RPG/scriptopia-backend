package com.scriptopia.demo.dto.localaccount;

import com.scriptopia.demo.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Long expiresIn;
    private Role role;
}
