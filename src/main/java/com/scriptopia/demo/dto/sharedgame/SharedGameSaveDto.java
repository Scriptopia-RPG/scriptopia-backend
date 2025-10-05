package com.scriptopia.demo.dto.sharedgame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedGameSaveDto {
    private String sharedGameUuid;
    private String thumbnailUrl;
    private Long recommand;
    private Long playCount;
    private String title;
    private String worldView;
    private String backgroundStory;
    private LocalDateTime sharedAt;
}
