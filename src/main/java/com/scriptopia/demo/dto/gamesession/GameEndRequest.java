package com.scriptopia.demo.dto.gamesession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEndRequest {
    private String worldView;
    private String location;
    private String previousStory;
    private String playerName;
    private int gameEnd;
}
