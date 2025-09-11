package com.scriptopia.demo.dto.sharedgame;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class MySharedGameResponse {
    private UUID shared_game_uuid;
    private String thumbnailUrl;
    private boolean recommand;
    private Long totalPlayed;
    private String title;
    private String worldView;
    private String backgroundStory;
    private LocalDateTime sharedAt;
    private List<TagDto> tags;

    @Data
    public static class TagDto {
        private String tagName;

        public TagDto(String tagName) {
            this.tagName = tagName;
        }
    }
}