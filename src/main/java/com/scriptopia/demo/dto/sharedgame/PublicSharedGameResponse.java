package com.scriptopia.demo.dto.sharedgame;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PublicSharedGameResponse {
    private Long sharedGameId;
    private String thumbnailUrl;
    private boolean isLiked;
    private Long likeCount;
    private Long totalPlayCount;
    private String title;
    private Long topScore;
    private LocalDateTime sharedAt;

    private List<TagDto> tags;

}