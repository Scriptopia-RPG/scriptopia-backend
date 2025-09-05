package com.scriptopia.demo.domain;

import lombok.Getter;

import java.security.SecureRandom;

@Getter
public enum NpnGrade {
    GRADE_1(1, 67, 70, 74, 2, 16, 18, 20),
    GRADE_2(2, 100, 105, 110, 3, 23, 26, 29),
    GRADE_3(3, 133, 140, 147, 4, 32, 35, 39),
    GRADE_4(4, 144, 152, 160, 0, 34, 38, 42),
    GRADE_5(5, 179, 188, 197, 0, 42, 47, 52),
    GRADE_6(6, 194, 204, 214, 0, 46, 51, 56),
    GRADE_7(7, 236, 248, 260, 0, 56, 62, 68),
    GRADE_8(8, 255, 268, 281, 0, 60, 67, 74),
    GRADE_9(9, 304, 320, 336, 0, 72, 80, 88),
    GRADE_10(10, 327, 344, 361, 0, 77, 86, 95),
    GRADE_11(11, 672, 707, 742, 7, 91, 101, 111),
    GRADE_12(12, 915, 963, 1011, 9, 96, 107, 118);

    private final int gradeNumber;       // 1~12
    private final int minDefense;
    private final int defense;
    private final int maxDefense;
    private final int expectedHits;
    private final int minAttack;
    private final int attack;
    private final int maxAttack;

    private static final SecureRandom random = new SecureRandom();

    NpcGrade(int gradeNumber, int minDefense, int defense, int maxDefense,
             int expectedHits, int minAttack, int attack, int maxAttack) {
        this.gradeNumber = gradeNumber;
        this.minDefense = minDefense;
        this.defense = defense;
        this.maxDefense = maxDefense;
        this.expectedHits = expectedHits;
        this.minAttack = minAttack;
        this.attack = attack;
        this.maxAttack = maxAttack;
    }

    /**
     * 1~12 등급 중 랜덤 반환
     */
    public static NpcGrade getRandomGrade() {
        NpcGrade[] values = NpcGrade.values();
        return values[random.nextInt(values.length)];
    }
}
