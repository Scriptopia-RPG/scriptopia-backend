package com.scriptopia.demo.domain.mongo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BattleInfoMongo {
    private Long curTurnId;
    private List<Long> playerHp;
    private List<Long> enemyHp;
    private List<BattleTurnMongo> battleTurn;
}