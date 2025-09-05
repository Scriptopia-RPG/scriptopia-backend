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
}
