package com.scriptopia.demo.domain.mongo;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RewardInfoMongo {

    @Builder.Default
    private List<String> gainedItemDefId = new ArrayList<>();

    @Builder.Default
    private List<String> lostItemsDefId = new ArrayList<>();

    @Builder.Default
    private Integer rewardStrength = 0;

    @Builder.Default
    private Integer rewardAgility = 0;

    @Builder.Default
    private Integer rewardIntelligence = 0;

    @Builder.Default
    private Integer rewardLuck = 0;

    @Builder.Default
    private Integer rewardLife = 0;

    private String rewardTrait;

    @Builder.Default
    private Integer rewardGold = 0;
}
