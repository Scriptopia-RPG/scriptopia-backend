package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.Grade;

import java.security.SecureRandom;

public class GameBalanceUtil {
    static SecureRandom secureRandom = new SecureRandom();

    public static int[] initItemStat(Grade grade) {
        int[] stats = new int[4]; // str,agi,int,luk
        int itemBaseStat = 0;

        switch (grade) {
            case COMMON -> itemBaseStat = secureRandom.nextInt(4);
            case UNCOMMON -> itemBaseStat = secureRandom.nextInt(4) + 1;
            case RARE -> itemBaseStat = secureRandom.nextInt(4) + 2;
            case EPIC -> itemBaseStat = secureRandom.nextInt(4) + 4;
            case LEGENDARY -> itemBaseStat = secureRandom.nextInt(4) + 5;
        }

        for(int i = 0; i < itemBaseStat; i++){
            stats[secureRandom.nextInt(4)] += 1;
        }
        return stats;
    }

    public static int[] getRandomItemStatsByGrade(Grade grade) {
        int min = 0, max = 0;
        switch (grade) {
            case COMMON -> { min = 0; max = 3; }
            case UNCOMMON -> { min = 1; max = 4; }
            case RARE -> { min = 2; max = 5; }
            case EPIC -> { min = 4; max = 7; }
            case LEGENDARY -> { min = 5; max = 8; }
        }

        // 총합 랜덤 결정
        int totalPoints = secureRandom.nextInt(max - min + 1) + min;

        // 배열 생성 (0: STR, 1: AGI, 2: INT, 3: LUCK)
        int[] stats = new int[4];

        // 랜덤 분배
        for (int i = 0; i < totalPoints; i++) {
            stats[secureRandom.nextInt(4)]++;
        }

        return stats;
    }



}
