package com.scriptopia.demo.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class User {
    @Id @GeneratedValue
    private Long id;

    private String nickname;
    private Long pia;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    private String profileImgUrl;
    private Role role;

}
