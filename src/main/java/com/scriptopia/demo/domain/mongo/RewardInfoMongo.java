package com.scriptopia.demo.domain.mongo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RewardInfoMongo {
    private List<Long> gainedItemDefId;
    private List<Long> lostItemsDefId;
    private Integer rewardStrength;
    private Integer rewardAgility;
    private Integer rewardIntelligence;
    private Integer rewardLuck;
    private Integer rewardLife;
    private String rewardTrait;
    private Integer rewardGold;
}
