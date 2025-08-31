package com.scriptopia.demo.dto.sharedgame;

import lombok.Data;

@Data
public class PublicSharedGameResponse {
    private Long sharedGameId;
    private String thumbnailUrl;
    private boolean isLiked;
    private Long likeCount;
    private Long totalPlayCount;
    private String title;
    private Float topScore;
}
