package com.scriptopia.demo.domain;


import java.security.SecureRandom;

public enum RewardType {
    GOLD(40),
    LIFE(30),
    ITEM(20),
    STAT(10);

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
}
