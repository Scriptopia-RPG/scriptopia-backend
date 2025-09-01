package com.scriptopia.demo.domain;

import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
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

    @Column(nullable = false, unique = true)
    private String nickname;

    private Long pia;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    private String profileImgUrl;


    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    // 거래 관련 도메인 메소드
    public void addPia(Long amount) {
        if (amount <= 0) throw new CustomException(ErrorCode.E_400_INVALID_AMOUNT);
        this.pia += amount;
    }

    public void subtractPia(Long amount) {
        if (amount <= 0) throw new CustomException(ErrorCode.E_400_INVALID_AMOUNT);
        if (this.pia < amount) throw new CustomException(ErrorCode.E_400_INSUFFICIENT_PIA);
        this.pia -= amount;
    }

}
