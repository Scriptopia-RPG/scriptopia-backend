package com.scriptopia.demo.dto.sharedgamefavorite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scriptopia.demo.dto.sharedgame.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharedGameFavoriteResponse {
    private String sharedGameUuid;
    private String thumbnailUrl;

    @JsonProperty("isLiked")
    private boolean isLiked;

    private Long likeCount;
    private Long totalPlayCount;
    private String title;
    private List<TagDto> tags;
    private Long topScore;
}
