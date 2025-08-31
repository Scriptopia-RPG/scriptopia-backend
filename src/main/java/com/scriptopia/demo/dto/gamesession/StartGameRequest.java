package com.scriptopia.demo.dto.gamesession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartGameRequest {
    private String background;
    private String characterName;
    private String characterDescription;
    private String itemId; // uuid를 받을 것 (임시임)
}