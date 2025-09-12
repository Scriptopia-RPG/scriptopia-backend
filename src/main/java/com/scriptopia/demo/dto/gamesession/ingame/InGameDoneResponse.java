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

    private InGamePlayerResponse playerInfo; // 외부
    private InGameNpcResponse npcInfo; // 외부
    private List<InGameInventoryResponse> inventory; // 외부
}
