package com.scriptopia.demo.dto.sharedgame;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PublicSharedGameDetailResponse {
    private UUID sharedGameUuID;
    private String posterUrl;
    private String title;
    private String worldView;
    private String backgroundStory;
    private String creator;
    private Long playCount;
    private Long likeCount;
    private boolean isLiked;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime sharedAt;
    private List<TagDto> tags;
    private List<TopScoreDto> topScores;

    @Data
    public static class TagDto {
        private Long tagId;
        private String tagName;

        public TagDto(Long tagId, String tagName) {
            this.tagId = tagId;
            this.tagName = tagName;
        }
    }

    @Data
    public static class TopScoreDto {
        private String nickname;
        private String profileUrl;
        private Long score;
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private LocalDateTime createdAt;
    }
}
