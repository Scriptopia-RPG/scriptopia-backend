package com.scriptopia.demo.domain;

import lombok.Getter;

import java.security.SecureRandom;

@Getter
public enum NpcGrade {
    GRADE1(1,  70, 18),
    GRADE2(2, 105,26),
    GRADE3(3,  140,35),
    GRADE4(4,152,38),
    GRADE5(5, 188,47),
    GRADE6(6, 204,51),
    GRADE7(7, 248,62),
    GRADE8(8, 268,67),
    GRADE9(9, 320,80),
    GRADE10(10, 344,86),
    GRADE11(11, 707, 101),
    GRADE12(12, 963,107);

    private final int gradeNumber;
    private final int defense;
    private final int attack;

    private static final SecureRandom random = new SecureRandom();

    NpcGrade(int gradeNumber, int defense,
             int attack) {
        this.gradeNumber = gradeNumber;
        this.defense = defense;
        this.attack = attack;
    }

    // ±10% 랜덤 방어력
    public int getRandomDefense() {
        int delta = (int)(defense * 0.1);
        return defense - delta + random.nextInt(2 * delta + 1);
    }

    // ±10% 랜덤 공격력
    public int getRandomAttack() {
        int delta = (int)(attack * 0.1);
        return attack - delta + random.nextInt(2 * delta + 1);
    }


}
