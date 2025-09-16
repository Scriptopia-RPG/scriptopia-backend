package com.scriptopia.demo.dto.gamesession;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameRequest {
    private String background;
    private String characterName;
    private String characterDescription;
}
