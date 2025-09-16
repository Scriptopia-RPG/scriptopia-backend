package com.scriptopia.demo.domain.mongo;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BattleInfoMongo {
    private Long curTurnId;                  // 현재 턴
    private List<Long> playerHp;             // 플레이어 HP 로그
    private List<Long> enemyHp;              // NPC HP 로그
    private List<BattleStoryMongo> battleTurn;// 수치 기반 턴 기록
    private Boolean playerWin;               // 승패 여부
}