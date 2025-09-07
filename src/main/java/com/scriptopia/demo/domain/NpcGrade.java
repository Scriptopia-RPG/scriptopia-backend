package com.scriptopia.demo.domain;

import lombok.Getter;

import java.security.SecureRandom;
import java.util.List;

@Getter
public enum NpcGrade {
    GRADE1(1,  70, 18, List.of(14, 8, 4)),
    GRADE2(2, 105, 26, List.of(16, 9, 5)),
    GRADE3(3, 140, 35, List.of(14, 9, 6)),
    GRADE4(4, 152, 38, List.of(12, 12, 7)),
    GRADE5(5, 188, 47, List.of(10, 12, 9)),
    GRADE6(6, 204, 51, List.of(9, 12, 11)),
    GRADE7(7, 248, 62, List.of(8, 11, 13)),
    GRADE8(8, 268, 67, List.of(7, 10, 13)),
    GRADE9(9, 320, 80, List.of(6, 10, 12)),
    GRADE10(10, 344, 86, List.of(3, 5, 7)),
    GRADE11(11, 707, 101, List.of(1, 1, 2)),
    GRADE12(12, 963, 107, List.of(0, 1, 1));


    private final int gradeNumber;
    private final int defense;
    private final int attack;
    private final List<Integer> chapter;


    private static final SecureRandom random = new SecureRandom();

    NpcGrade(int gradeNumber, int defense,
             int attack, List<Integer> chapter) {
        this.gradeNumber = gradeNumber;
        this.defense = defense;
        this.attack = attack;
        this.chapter = chapter;
    }

    public static NpcGrade getByGradeNumber(int gradeNumber) {
        for (NpcGrade grade : NpcGrade.values()) {
            if (grade.getGradeNumber() == gradeNumber) {
                return grade;
            }
        }
        return null;
    }

    public static Integer getNpcNumberByRandom(int currentChapter) {
        int rand = random.nextInt(100) + 1; // 1~100
        int cumulative = 0;

        for (NpcGrade grade : NpcGrade.values()) {
            int weight = grade.getChapter().get(currentChapter - 1);

            cumulative += weight;
            if (rand <= cumulative) {
                return grade.getGradeNumber();
            }
        }

        return NpcGrade.GRADE12.getGradeNumber();
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
