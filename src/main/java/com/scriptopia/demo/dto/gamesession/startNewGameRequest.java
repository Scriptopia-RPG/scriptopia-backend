package com.scriptopia.demo.dto.gamesession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class startNewGameRequest {
    private String message;
    private String mongoId;

}
