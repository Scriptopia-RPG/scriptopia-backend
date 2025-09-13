package com.scriptopia.demo.dto.gamesession.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InGameBattleResponse {
    private String sceneType;
    private LocalDateTime startedAt;
    private LocalDateTime updatedAt;
    private String background;
    private String location;
    private int progress;
    private int stageSize;

    private InGamePlayerResponse playerInfo; // 외부
    private InGameNpcResponse npcInfo; // 외부
    private List<InGameInventoryResponse> inventory; // 외부


    private Long curTurnId; // 현재 턴
    private List<Integer> playerHp; // 플레이어 체력 로그
    private List<Integer> enemyHp;  // NPC 체력 로그
    private List<BattleStoryResponse> battleStory; // 턴별 전투 스토리
    private Boolean playerWin; // 승리 여부


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BattleStoryResponse {
        private String turnInfo; // 해당 턴 설명
    }
}
