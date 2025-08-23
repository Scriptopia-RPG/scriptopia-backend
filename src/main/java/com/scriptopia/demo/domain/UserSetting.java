package com.scriptopia.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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

    private FontType fontType;

    private int fontSize;

    private int lineHeight;

    private LocalDateTime updatedAt;
}
