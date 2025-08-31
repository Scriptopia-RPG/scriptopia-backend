package com.scriptopia.demo.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BattleInfoMongo {
    private Long curTurnId;
    private List<Long> playerHp;
    private List<Long> enemyHp;
    private List<BattleTurnMongo> battleTurn;
}