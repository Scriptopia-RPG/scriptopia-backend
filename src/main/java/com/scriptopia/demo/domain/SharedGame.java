package com.scriptopia.demo.domain;

import com.scriptopia.demo.dto.sharedgame.SharedGameRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class SharedGame {
    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID uuid;

    private String thumbnailUrl;
    private Long recommend = 0L;
    private Long totalPlayed = 0L;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String worldView;

    @Column(columnDefinition = "TEXT")
    private String backgroundStory;
    private LocalDateTime sharedAt;

    @PrePersist
    public void generateUuid() {
        if(uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public static SharedGame from(User user, History h) {
        SharedGame game = new SharedGame();
        game.user = user;
        game.thumbnailUrl = h.getThumbnailUrl();
        game.title = h.getTitle();
        game.worldView = h.getWorldView();
        game.backgroundStory = h.getBackgroundStory();
        game.sharedAt = LocalDateTime.now();

        return game;
    }
}
