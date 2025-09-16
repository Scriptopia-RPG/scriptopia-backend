package com.scriptopia.demo.domain;


import com.scriptopia.demo.domain.mongo.GameSessionMongo;
import com.scriptopia.demo.domain.mongo.RewardInfoMongo;

import java.security.SecureRandom;

public enum RewardType {
    GOLD(60),
    STAT(10),
    ITEM(10),
    NONE(20);

    private final int dropRate;
    private static final SecureRandom random = new SecureRandom();

    RewardType(int dropRate) {
        this.dropRate = dropRate;
    }

    public int getDropRate() {
        return dropRate;
    }

    // 랜덤 보상 추출
    public static RewardType getRandomRewardType() {
        int rand = random.nextInt(100) + 1; // 1~100
        int cumulative = 0;

        for (RewardType reward : RewardType.values()) {
            cumulative += reward.getDropRate();
            if (rand <= cumulative) {
                return reward;
            }
        }
        return null; // 혹은 기본값
    }


    public static String getRewardSummary(RewardInfoMongo rewardInfo) {
        if (rewardInfo == null) {
            return "보상이 없습니다.";
        }

        StringBuilder sb = new StringBuilder();

        // GOLD
        if (rewardInfo.getRewardGold() != null && rewardInfo.getRewardGold() != 0) {
            if (rewardInfo.getRewardGold() > 0) {
                sb.append(rewardInfo.getRewardGold()).append(" 골드를 획득하였습니다.\n");
            } else {
                sb.append(Math.abs(rewardInfo.getRewardGold())).append(" 골드를 잃었습니다.\n");
            }
        }

        // STAT
        if (rewardInfo.getRewardStrength() != null && rewardInfo.getRewardStrength() != 0) {
            sb.append("힘 ").append(formatChange(rewardInfo.getRewardStrength())).append("\n");
        }
        if (rewardInfo.getRewardAgility() != null && rewardInfo.getRewardAgility() != 0) {
            sb.append("민첩 ").append(formatChange(rewardInfo.getRewardAgility())).append("\n");
        }
        if (rewardInfo.getRewardIntelligence() != null && rewardInfo.getRewardIntelligence() != 0) {
            sb.append("지능 ").append(formatChange(rewardInfo.getRewardIntelligence())).append("\n");
        }
        if (rewardInfo.getRewardLuck() != null && rewardInfo.getRewardLuck() != 0) {
            sb.append("운 ").append(formatChange(rewardInfo.getRewardLuck())).append("\n");
        }
        if (rewardInfo.getRewardLife() != null && rewardInfo.getRewardLife() != 0) {
            sb.append("생명 ").append(formatChange(rewardInfo.getRewardLife())).append("\n");
        }

        // TRAIT
        if (rewardInfo.getRewardTrait() != null) {
            sb.append("특성 획득: ").append(rewardInfo.getRewardTrait()).append("\n");
        }

        // ITEM
        if (rewardInfo.getGainedItemDefId() != null && !rewardInfo.getGainedItemDefId().isEmpty()) {
            sb.append("아이템 획득: ").append(rewardInfo.getGainedItemDefId()).append("\n");
        }
        if (rewardInfo.getLostItemsDefId() != null && !rewardInfo.getLostItemsDefId().isEmpty()) {
            sb.append("아이템 잃음: ").append(rewardInfo.getLostItemsDefId()).append("\n");
        }

        return sb.length() == 0 ? "보상이 없습니다." : sb.toString().trim();
    }

    private static String formatChange(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

}
