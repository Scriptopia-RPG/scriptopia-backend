package com.scriptopia.demo.domain.mongo;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BattleInfoMongo {
    private Long curTurnId;
    private List<Long> playerHp;
    private List<Long> enemyHp;
    private List<BattleTurnMongo> battleTurn;
}