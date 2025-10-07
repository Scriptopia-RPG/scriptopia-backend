package com.scriptopia.demo.dto.sharedgame;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PublicSharedGameResponse {
    private UUID sharedGameUuid;
    private String thumbnailUrl;
    private String title;
    private Long playCount;

    private List<TagDto> tags;

}