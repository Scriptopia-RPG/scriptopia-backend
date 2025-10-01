package com.scriptopia.demo.dto.gamesession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEndRequest {
    private String worldView;
    private String location;
    private String previousStory;
    private String playerName;
    private String gameEnd;
}
