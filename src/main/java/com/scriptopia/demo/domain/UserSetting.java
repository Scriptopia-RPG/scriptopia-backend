package com.scriptopia.demo.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.awt.*;
import java.time.LocalDateTime;

@Entity
@Data
public class UserSetting {

    @Id@GeneratedValue
    private long id;

    @OneToOne
    private User user;

    private Theme theme;

    @Enumerated(EnumType.STRING)
    private FontType fontType;

    private int fontSize;

    private int lineHeight;

    private int wordSpacing;

    private LocalDateTime updatedAt;
}
