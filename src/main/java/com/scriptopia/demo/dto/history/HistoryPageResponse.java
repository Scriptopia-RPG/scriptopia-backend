package com.scriptopia.demo.dto.history;

import com.scriptopia.demo.domain.History;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryPageResponse {
    private UUID uuid;
    private String thumbnailUrl;
    private String title;
    private String worldView;
    private String backgroundStory;
    private String epilogue1Title;
    private String epilogue1Content;
    private String epilogue2Title;
    private String epilogue2Content;
    private String epilogue3Title;
    private String epilogue3Content;
    private Long score;
    private LocalDateTime created_at;
    private boolean isShared;

    public static HistoryPageResponse from(History h) {
        return HistoryPageResponse.builder()
                .uuid(h.getUuid())
                .thumbnailUrl(h.getThumbnailUrl())
                .title(h.getTitle())
                .worldView(h.getWorldView())
                .backgroundStory(h.getBackgroundStory())
                .epilogue1Title(h.getEpilogue1Title())
                .epilogue1Content(h.getEpilogue1Content())
                .epilogue2Title(h.getEpilogue2Title())
                .epilogue2Content(h.getEpilogue2Content())
                .epilogue3Title(h.getEpilogue3Title())
                .epilogue3Content(h.getEpilogue3Content())
                .score(h.getScore())
                .created_at(h.getCreatedAt())
                .isShared(h.getIsShared())
                .build();
    }
}
