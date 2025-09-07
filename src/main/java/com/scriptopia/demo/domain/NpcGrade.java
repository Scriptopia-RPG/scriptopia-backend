package com.scriptopia.demo.domain;

import lombok.Getter;

import java.security.SecureRandom;

@Getter
public enum NpcGrade {
    GRADE1(1,  70, 18, 14, 8, 4),
    GRADE2(2, 105,26, 16, 9, 5),
    GRADE3(3,  140,35, 14, 9, 6),
    GRADE4(4,152,38, 12, 12, 7),
    GRADE5(5, 188,47, 10, 12, 9),
    GRADE6(6, 204,51, 9, 12 ,11),
    GRADE7(7, 248,62, 8, 11, 13),
    GRADE8(8, 268,67, 7, 10, 13),
    GRADE9(9, 320,80, 6, 10 , 12),
    GRADE10(10, 344,86, 3, 5, 7),
    GRADE11(11, 707, 101, 1, 1, 2),
    GRADE12(12, 963,107, 0, 1, 1);

    private final int gradeNumber;
    private final int defense;
    private final int attack;
    private final int chapter1;
    private final int chapter2;
    private final int chapter3;


    private static final SecureRandom random = new SecureRandom();

    NpcGrade(int gradeNumber, int defense,
             int attack, int chapter1, int chapter2, int chapter3) {
        this.gradeNumber = gradeNumber;
        this.defense = defense;
        this.attack = attack;
        this.chapter1 = chapter1;
        this.chapter2 = chapter2;
        this.chapter3 = chapter3;
    }

    public static NpcGrade getByGradeNumber(int gradeNumber) {
        for (NpcGrade grade : NpcGrade.values()) {
            if (grade.getGradeNumber() == gradeNumber) {
                return grade;
            }
        }
        return null;
    }

    public static Integer getNpcNumberByRandom (int currentChapter) {
        int rand = random.nextInt(100) + 1; // 1~100
        int cumulative = 0;

        for (NpcGrade grade : NpcGrade..values()) {
            if (rand <= cumulative) {
                return grade;
            }
        }
        return 12;
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
