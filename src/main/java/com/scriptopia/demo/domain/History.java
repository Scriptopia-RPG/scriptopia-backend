package com.scriptopia.demo.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class History {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String thumbnailUrl;
    private String title;
    private String worldView;
    private String backgroundStory;
    private String worldPrompt;
    private String epilogue1Title;
    private String epilogue1Content;
    private String epilogue2Title;
    private String epilogue2Content;
    private String epilogue3Title;
    private String epilogue3Content;

    private Long score;
    private LocalDateTime createdAt;
    private Boolean isShared;
}
