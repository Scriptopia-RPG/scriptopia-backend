package com.scriptopia.demo.domain;

import java.security.SecureRandom;

public enum ItemType {
    WEAPON(40),
    ARMOR(40),
    ARTIFACT(20),
    POTION(0);


    private final int dropRate;

    private static final SecureRandom random = new SecureRandom();

    ItemType(int dropRate) {
        this.dropRate = dropRate;
    }

    public static ItemType getRandomItemType() {
        int rand = random.nextInt(100) + 1;
        int cumulative = 0;

        for (ItemType itemType : ItemType.values()) {
            cumulative += itemType.dropRate;
            if ( rand <= cumulative) {
                return itemType;
            }
        }
        return null;
    }

}