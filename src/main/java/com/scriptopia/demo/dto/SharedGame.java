package com.scriptopia.demo.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class SharedGame {
    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String thumbnailUrl;
    private Long recommand;
    private Long totalPlayed;
    private String title;
    private String worldView;
    private String backgroundStory;
    private LocalDateTime sharedAt;
}
