package com.scriptopia.demo.dto.sharedgame;

import com.scriptopia.demo.domain.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SharedGameRequest {
    private Long userId;
    private String thumbnail_url;
    private String title;
    private String world_view;
    private String background_story;
}
