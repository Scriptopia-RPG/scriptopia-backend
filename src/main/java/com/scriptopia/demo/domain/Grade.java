package com.scriptopia.demo.domain;

import lombok.Getter;

import java.security.SecureRandom;

@Getter
public enum Grade {
    COMMON(30, 137, 50),
    UNCOMMON(35, 177, 30),
    RARE(40, 216, 15),
    EPIC(45, 260, 4),
    LEGENDARY(50, 312, 1);

    private final int attackPower;
    private final int defensePower;
    private final int dropRate; // 새로 추가한 필드

    Grade(int attackPower, int defensePower, int dropRate) {
        this.attackPower = attackPower;
        this.defensePower = defensePower;
        this.dropRate = dropRate;
    }

    private static final SecureRandom random = new SecureRandom();

    /**
     * 아이템, 아이템 효과
     * Grade 중 하나를 랜덤으로 반환
     */
    public static Grade getRandomGradeByProbability() {
        int rand = random.nextInt(100) + 1; // 1~100
        int cumulative = 0;

        for (Grade grade : Grade.values()) {
            cumulative += grade.getDropRate();
            if (rand <= cumulative) {
                return grade;
            }
        }
        return LEGENDARY;
    }

    /**
     * 아이템 종류, 등급에 따른 기본 성능을 리턴
     * 기본 공격력에 +- 10%
     */
    public static int getRandomBaseStat(ItemType itemType, Grade grade) {
        if (itemType == ItemType.WEAPON){
            return getRandomBaseStatWeapon(grade);
        }
        return getRandomBaseStatArmor(grade);
    }

    
    // ±10% 랜덤 공격력
    public static int getRandomBaseStatWeapon(Grade grade) {
        int base = grade.getAttackPower();
        int delta = (int) (base * 0.1);
        return base - delta + random.nextInt(2 * delta + 1);
    }

    // ±10% 랜덤 방어력
    public static int getRandomBaseStatArmor(Grade grade) {
        int base = grade.getDefensePower();
        int delta = (int) (base * 0.1);
        return base - delta + random.nextInt(2 * delta + 1);
    }


}
