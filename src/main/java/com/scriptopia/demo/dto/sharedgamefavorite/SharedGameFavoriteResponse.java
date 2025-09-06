package com.scriptopia.demo.dto.sharedgamefavorite;

import lombok.Data;

@Data
public class SharedGameFavoriteResponse {
    private Long sharedGameId;
    private String thumbnailUrl;
    private boolean isLiked;
    private Long likeCount;
    private Long totalPlayCount;
    private String title;
    private String[] tags;
    private Long topScore;
}
