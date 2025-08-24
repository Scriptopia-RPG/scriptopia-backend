package com.scriptopia.demo.dto.history;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryResponse {
    private Long id;
    private Long userId;
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
