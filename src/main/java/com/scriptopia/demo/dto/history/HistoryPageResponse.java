package com.scriptopia.demo.dto.history;

import com.scriptopia.demo.domain.History;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HistoryPageResponse {
    private UUID uuid;
    private String title;
    private Long score;
    private String thumbnail_url;
    private LocalDateTime created_at;

    public static HistoryPageResponse from(History h) {
        HistoryPageResponse dto = new HistoryPageResponse();
        dto.setUuid(h.getUuid());
        dto.setTitle(h.getTitle());
        dto.setScore(h.getScore());
        dto.setThumbnail_url(h.getThumbnailUrl());
        dto.setCreated_at(h.getCreatedAt());

        return dto;
    }
}
