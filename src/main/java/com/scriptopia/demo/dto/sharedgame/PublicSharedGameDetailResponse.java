package com.scriptopia.demo.dto.sharedgame;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PublicSharedGameDetailResponse {
    private UUID sharedGameUUID;
    private String nickname;
    private String thumbnailUrl;
    private Long totalPlayed;
    private String title;
    private String worldView;
    private String backgroundStory;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime sharedAt;
    private List<TagDto> tags;
    private List<TopScoreDto> topScores;

    @Data
    public static class TagDto {
        private String tagName;

        public TagDto(String tagName) {
            this.tagName = tagName;
        }
    }

    @Data
    public static class TopScoreDto {
        private String nickname;
        private Long score;
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private LocalDateTime createdAt;
    }
}
