package com.scriptopia.demo.domain.mongo;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RewardInfoMongo {
    private List<String> gainedItemDefId;
    private List<String> lostItemsDefId;
    private Integer rewardStrength;
    private Integer rewardAgility;
    private Integer rewardIntelligence;
    private Integer rewardLuck;
    private Integer rewardLife;
    private String rewardTrait;
    private Integer rewardGold;
}
