package com.scriptopia.demo.dto.sharedgame;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PublicSharedGameResponse {
    private UUID sharedGameId;
    private String thumbnailUrl;
    private boolean isLiked;
    private Long likeCount;
    private Long totalPlayCount;
    private String title;
    private Long topScore;
    private LocalDateTime sharedAt;

    private List<TagDto> tags;

}