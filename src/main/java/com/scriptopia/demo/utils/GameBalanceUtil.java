package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.Stat;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameBalanceUtil {

    static SecureRandom secureRandom = new SecureRandom();
    static final int PLAYER_BASE_STAT = 5;

    /**
     * NPC 기본 스탯 테이블
     * rank → [STR, AGI, INT, LUK]
     */
    private static final int[][] NPC_BASE_STATS = {
            {3, 3, 3, 3},   // rank 0 (dummy, 사용 안 함)
            {5, 4, 3, 2},   // rank 1 : C 하급
            {7, 6, 4, 3},   // rank 2 : C 일반
            {9, 7, 5, 4},   // rank 3 : C 상급
            {12, 9, 6, 5},  // rank 4 : U 중급
            {15, 12, 8, 6}, // rank 5 : U 중급+
            {18, 14, 10, 7},// rank 6 : R 정예 시작
            {22, 17, 12, 9},// rank 7 : R 강한 정예
            {28, 20, 15, 11},// rank 8 : E 보스
            {35, 25, 18, 14},// rank 9 : E 보스급+
            {50, 35, 25, 20},// rank 10 : E+ 대륙 위협
            {70, 50, 35, 25},// rank 11 : L 국가급
            {100, 70, 50, 40}// rank 12 : L+ 초월급
    };



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


    /**
     * @param itemGradePrice
     * @param effectGradeList
     * @return Long
     */
    public static Long getItemPriceByGrade(Long itemGradePrice, List<Long> effectGradeList) {
        return getRandomItemPriceByGrade(itemGradePrice) + getRandomItemPriceByGrade(effectGradeList);
    }


    public static Long getRandomItemPriceByGrade(Long itemGradePrice) {
        int priceRate = secureRandom.nextInt(21) - 10;

        Long itemPrice = 0L;
        itemPrice += (long) Math.floor(itemGradePrice * (1 + priceRate / 100.0));
        return itemPrice;
    }

    public static Long getRandomItemPriceByGrade(List<Long> effectGradeList) {
        int priceRate = secureRandom.nextInt(21) - 10;

        Long effectPrice = 0L;
        for (Long grade : effectGradeList) {
            effectPrice += (long) Math.floor(grade * (1 + priceRate / 100.0));
        }

        return effectPrice;
    }

    /**
     * 플레이어 기본 스탯 초기화
     * @param playerStat (플레이어가 선택한 주 스탯)
     * @return (0: STR, 1: AGI, 2: INT, 3: LUK)
     */
    public static int[] getInitialPlayerStats(Stat playerStat) {
        int mainStat = secureRandom.nextInt(3);   // 0 ~ 2
        int subStat = secureRandom.nextInt(3) - 2; // -2 ~ 0

        int[] stats = new int[4];
        stats[0] = (playerStat.equals(Stat.STRENGTH))     ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        stats[1] = (playerStat.equals(Stat.AGILITY))      ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        stats[2] = (playerStat.equals(Stat.INTELLIGENCE)) ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        stats[3] = (playerStat.equals(Stat.LUCK))         ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;

        return stats;
    }


    /**
     * NPC 스탯 생성 (랭크 기반)
     * @param rank (1 ~ 12)
     * @return (0: STR, 1: AGI, 2: INT, 3: LUK)
     */
    public static int[] getNpcStatsByRank(int rank) {
        if (rank < 1 || rank >= NPC_BASE_STATS.length) {
            throw new CustomException(ErrorCode.E_400_INVALID_NPC_RANK);
        }

        int[] base = NPC_BASE_STATS[rank];
        int[] npcStats = new int[4];

        // 약간의 랜덤 편차 추가 (±10%)
        for (int i = 0; i < 4; i++) {
            int variance = (int) Math.round(base[i] * (secureRandom.nextDouble() * 0.2 - 0.1));
            npcStats[i] = base[i] + variance;
        }

        return npcStats;
    }

    public static boolean getChoiceProbability(int statValue) {
        double baseRate = 40.0;      // 기본 확률 40%
        double minRate = 30.0;       // 최소 30%
        double maxRate = 80.0;       // 최대 80%
        double statBonus = 0.8 * statValue; // 스탯 1당 +0.8%

        double finalRate = baseRate + statBonus;
        finalRate = Math.max(minRate, Math.min(finalRate, maxRate));

        return secureRandom.nextDouble() * 100 < finalRate;
    }


}
