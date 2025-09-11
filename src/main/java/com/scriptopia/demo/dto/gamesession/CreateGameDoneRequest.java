package com.scriptopia.demo.dto.gamesession;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameDoneRequest {

    private String worldView;
    private String location;

    private String previousStory;
    private String selectedChoice;

    private String resultContent;
    private String playerName;
    private String playerVictory;

}