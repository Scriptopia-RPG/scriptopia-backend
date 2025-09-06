package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.Grade;

import java.security.SecureRandom;

public class GameBalanceUtil {
    static SecureRandom secureRandom = new SecureRandom();


    /**
     * @param grade
     * @return (0: STR, 1: AGI, 2: INT, 3: LUCK)
     */
    public static int[] getRandomItemStatsByGrade(Grade grade) {
        int min = 0, max = 0;
        switch (grade) {
            case COMMON -> { min = 0; max = 3; }
            case UNCOMMON -> { min = 1; max = 4; }
            case RARE -> { min = 2; max = 5; }
            case EPIC -> { min = 4; max = 7; }
            case LEGENDARY -> { min = 5; max = 8; }
        }

        int totalPoints = secureRandom.nextInt(max - min + 1) + min;

        int[] stats = new int[4];

        for (int i = 0; i < totalPoints; i++) {
            stats[secureRandom.nextInt(4)]++;
        }

        return stats;
    }



}
