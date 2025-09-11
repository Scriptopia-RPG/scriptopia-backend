package com.scriptopia.demo.domain;


import com.scriptopia.demo.dto.history.HistoryRequest;
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
public class History {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String worldView;

    @Column(columnDefinition = "TEXT")
    private String backgroundStory;

    @Column(columnDefinition = "TEXT")
    private String worldPrompt;

    @Column(columnDefinition = "TEXT")
    private String epilogue1Title;

    @Column(columnDefinition = "TEXT")
    private String epilogue1Content;

    @Column(columnDefinition = "TEXT")
    private String epilogue2Title;

    @Column(columnDefinition = "TEXT")
    private String epilogue2Content;

    @Column(columnDefinition = "TEXT")
    private String epilogue3Title;

    @Column(columnDefinition = "TEXT")
    private String epilogue3Content;

    private Long score;
    private LocalDateTime createdAt;
    private Boolean isShared;

    @PrePersist
    public void prePersist() {
        if(uuid == null) uuid = UUID.randomUUID();
        if(createdAt == null) createdAt = LocalDateTime.now();
    }

    public History(User id, HistoryRequest req) {
        this.user = id;
        this.thumbnailUrl = req.getThumbnailUrl();
        this.title = req.getTitle();
        this.worldView = req.getWorldView();
        this.backgroundStory = req.getBackgroundStory();
        this.worldPrompt = req.getWorldPrompt();
        this.epilogue1Title = req.getEpilogue1Title();
        this.epilogue1Content = req.getEpilogue1Content();
        this.epilogue2Title = req.getEpilogue2Title();
        this.epilogue2Content = req.getEpilogue2Content();
        this.epilogue3Title = req.getEpilogue3Title();
        this.epilogue3Content = req.getEpilogue3Content();
        this.score = req.getScore();
        this.createdAt = LocalDateTime.now();
        this.isShared = false;
    }
}
