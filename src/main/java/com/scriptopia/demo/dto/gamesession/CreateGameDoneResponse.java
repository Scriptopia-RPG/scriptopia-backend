package com.scriptopia.demo.dto.gamesession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameDoneResponse {
    private DoneInfo doneInfo;



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoneInfo {
        private String newLocation;
        private String reCap;
    }

}
