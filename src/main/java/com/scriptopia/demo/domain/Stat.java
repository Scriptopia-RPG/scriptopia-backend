package com.scriptopia.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.SecureRandom;

public enum Stat {
    INTELLIGENCE,
    STRENGTH,
    AGILITY,
    LUCK;



    private static final SecureRandom random = new SecureRandom();

    /**
     * 무작위로 하나의 스탯을 반환
     */
    public static Stat getRandomStat() {
        Stat[] values = Stat.values();
        return values[random.nextInt(values.length)];
    }

}
