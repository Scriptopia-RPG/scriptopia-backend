package com.scriptopia.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private Long id;


    private String nickname;

    private Long pia;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    private String profileImgUrl;


    @Enumerated(EnumType.STRING)
    private Role role;

}
