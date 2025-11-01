package com.scriptopia.demo.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusResponse {
    private String nickname;
    private String profileImage;
    private Integer ticket;

}
