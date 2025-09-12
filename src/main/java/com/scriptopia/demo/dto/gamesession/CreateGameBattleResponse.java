package com.scriptopia.demo.dto.gamesession;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameBattleResponse {
    private BattleInfoDto battleInfo;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BattleInfoDto {
        private List<String> turnInfo; // 턴별 전투 로그
        private String reCap;          // 전투 요약
    }
}
