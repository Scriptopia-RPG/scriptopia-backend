package com.scriptopia.demo.dto.history;

import com.scriptopia.demo.domain.History;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryPageResponse {
    private Long id;
    private String title;
    private Long score;
    private String thumbnail_url;
    private LocalDateTime created_at;

    public static HistoryPageResponse from(History h) {
        HistoryPageResponse dto = new HistoryPageResponse();
        dto.setId(h.getId());
        dto.setTitle(h.getTitle());
        dto.setScore(h.getScore());
        dto.setThumbnail_url(h.getThumbnailUrl());
        dto.setCreated_at(h.getCreatedAt());

        return dto;
    }
}
