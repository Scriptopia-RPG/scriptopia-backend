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
public class InGameDoneResponse {
    private String sceneType;
    private LocalDateTime startedAt;
    private LocalDateTime updatedAt;
    private String background;
    private String location;
    private int progress;
    private int stageSize;

    private InGamePlayerResponse playerInfo;
    private InGameNpcResponse npcInfo;
    private List<InGameInventoryResponse> inventory;

    // 🏆 보상 정보
    private RewardInfoResponse rewardInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardInfoResponse {
        private List<String> gainedItemNames;
        private int rewardStrength;
        private int rewardAgility;
        private int rewardIntelligence;
        private int rewardLuck;
        private int rewardLife;
        private int rewardGold;
    }
}
