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
     * 메인 스탯의 경우 사용
     * 무작위로 하나의 스탯을 반환
     */
    public static Stat getRandomMainStat() {
        Stat[] values = Stat.values();
        return values[random.nextInt(values.length)];
    }

}
