package com.scriptopia.demo.dto.oauth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUserInfo {
    private String id;
    private String email;
    private String name;
    private String profileImage;
    private String provider;

}
