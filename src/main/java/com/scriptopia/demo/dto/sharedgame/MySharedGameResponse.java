package com.scriptopia.demo.dto.sharedgame;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MySharedGameResponse {
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