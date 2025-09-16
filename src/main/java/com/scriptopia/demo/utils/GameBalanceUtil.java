package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.EffectProbability;
import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.RewardType;
import com.scriptopia.demo.domain.Stat;
import com.scriptopia.demo.domain.mongo.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

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

    // NPC 등급별 예상 HP와 공격력 (HealthPoint, CombatPoint)
    public static final int[][] NPC_BATTLE_STATS = {
            {}, // index 0 dummy
            {70, 18},   // rank 1: {HealthPoint, CombatPoint}
            {105, 26},  // rank 2
            {140, 35},  // rank 3
            {152, 38},  // rank 4
            {188, 47},  // rank 5
            {204, 51},  // rank 6
            {248, 62},  // rank 7
            {268, 67},  // rank 8
            {320, 80},  // rank 9
            {344, 86},  // rank 10
            {707, 101}, // rank 11
            {963, 107}  // rank 12
    };



    private static final NavigableMap<Integer, Double> STAT_MULTIPLIERS = new TreeMap<>() {{
        put(5, 1.1);
        put(10, 1.2);
        put(15, 1.3);
        put(20, 1.4);
        put(25, 1.5);
        put(30, 1.6);
        put(35, 1.7);
        put(40, 1.8);
        put(45, 1.9);
        put(Integer.MAX_VALUE, 2.0); // 46 이상
    }};




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

    public static Integer getChoiceProbability(Stat stat, PlayerInfoMongo playerInfo) {
        int baseProbability = 40; // 기본 선택지 확률 40%
        double multiplier = 0.9 + (0.2 * secureRandom.nextDouble()); // 0.9 ~ 1.1
        baseProbability = (int) (baseProbability * multiplier);

        double statBonus = 0;

        switch (stat) {
            case STRENGTH -> statBonus = playerInfo.getStrength() * 0.8;
            case AGILITY -> statBonus = playerInfo.getAgility() * 0.8;
            case INTELLIGENCE -> statBonus = playerInfo.getIntelligence() * 0.8;
            case LUCK -> statBonus = playerInfo.getLuck() * 0.8;
        }

        double probability = baseProbability + statBonus;

        // 최소 30%, 최대 80% 제한
        probability = Math.max(30, Math.min(80, probability));

        return (int) Math.round(probability);
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

    public static int getPlayerWeaponDmg(PlayerInfoMongo player, ItemDefMongo weapon) {
        int baseCombatPoint = 22; // 맨손일 때

        if (weapon == null) {
            return baseCombatPoint;
        }

        // 무기 메인 스탯 결정
        Stat mainStat = weapon.getMainStat();
        int playerStatValue = switch (mainStat) {
            case STRENGTH -> player.getStrength();
            case AGILITY -> player.getAgility();
            case INTELLIGENCE -> player.getIntelligence();
            case LUCK -> player.getLuck();
        };

        double multiplier = STAT_MULTIPLIERS.ceilingEntry(playerStatValue).getValue();

        // 최종 전투력 계산
        int weaponStat = weapon.getBaseStat();
        int combatPoint = (int) (weaponStat * multiplier);

        return combatPoint;
    }


    /**
     * @param player
     * @param weapon
     * @return PlayerCombatPoint
     */
    public static int getBattlePlayerCombatPoint(
            PlayerInfoMongo player,
            ItemDefMongo weapon,
            EffectGradeDefRepository effectGradeDefRepository
    ) {
        int combatPoint = getPlayerWeaponDmg(player, weapon);

        if (weapon == null) {
            return combatPoint;
        }

        double totalMultiplier = 1.0;

        for (ItemEffectMongo effect : weapon.getItemEffect()) {
            EffectProbability prob = effect.getEffectProbability();
            var defOpt = effectGradeDefRepository.findByEffectProbability(prob);
            if (defOpt.isPresent()) {
                totalMultiplier += defOpt.get().getWeight(); // 여기서는 문제 없음
            }
        }

        // 최종 계산
        combatPoint = (int) Math.round(combatPoint * totalMultiplier);

        return combatPoint;
    }



    public static int simulateBattle(int playerCombatPoint, int npcCombatPoint){
        int maxNum = playerCombatPoint + npcCombatPoint;
        int num = secureRandom.nextInt(maxNum) + 1;

        return playerCombatPoint >= num ? 1 : 0 ;
    }

    public static List<List<Integer>> getBattleLog(
            int playerWin,
            int playerDmg,
            int playerHp,
            int npcDmg,
            int npcRank
    ) {
        List<List<Integer>> hpLog = new ArrayList<>();

        int npcHp = getNpcHealthPoint(npcRank);
        int winnerDmg = (playerWin == 1) ? playerDmg : npcDmg;
        int loserDmg = (playerWin == 1) ? npcDmg : playerDmg;

        int winnerCurrentHp = (playerWin == 1) ? playerHp : npcHp;
        int loserCurrentHp = (playerWin == 1) ? npcHp : playerHp;

        int winnerIndex = (playerWin == 1) ? 0 : 1;
        int loserIndex = (playerWin == 1) ? 1 : 0;

        List<Integer> originHp = new ArrayList<>(Arrays.asList(0, 0));

        originHp.set(loserIndex, loserCurrentHp);
        originHp.set(winnerIndex, winnerCurrentHp);

        hpLog.add(new ArrayList<>(originHp)); // 초기 로그 추가
        List<Integer> currentHp = originHp;


        while (loserCurrentHp > 0) {


            // 패배자에게만 데미지 적용
            int damage = (int) Math.round(winnerDmg * (0.9 + 0.4 * secureRandom.nextDouble()));
            loserCurrentHp -= damage;
            if (loserCurrentHp < 0) loserCurrentHp = 0;

            // winnerIndex, loserIndex 기준으로 정확히 넣기
            currentHp.set(winnerIndex, winnerCurrentHp);
            currentHp.set(loserIndex, loserCurrentHp);

            hpLog.add(new ArrayList<>(currentHp));
        }

        // 승리자 최소 HP (패배자가 0일 때)
        int winnerMaxHp = (int) (winnerCurrentHp * (0.1 + secureRandom.nextDouble() * 0.4));

        for (int i = 1 ; i < hpLog.size(); i++) {
            List<Integer> turnHp = hpLog.get(i);
            int damage = (int) Math.round(loserDmg * (0.9 + 0.4 * secureRandom.nextDouble()));
            winnerCurrentHp = Math.max(winnerCurrentHp - damage, winnerMaxHp);
            turnHp.set(winnerIndex,winnerCurrentHp);
        }


        return hpLog;
    }

    public static int getNpcCombatPoint(int npcRank) {
        int base = NPC_BATTLE_STATS[npcRank][1];

        double variance = 0.9 + secureRandom.nextDouble() * 0.2; // 0.9 ~ 1.1
        return (int) Math.round(base * variance);
    }

    public static int getNpcHealthPoint(int npcRank) {
        int base = NPC_BATTLE_STATS[npcRank][0];

        double variance = 0.9 + secureRandom.nextDouble() * 0.2; // 0.9 ~ 1.1
        return (int) Math.round(base * variance);
    }


    public static boolean isPass(int choiceProbability) {
        int randomCount = secureRandom.nextInt(100) + 1;

        return (choiceProbability >= randomCount);
    }

    public static RewardInfoMongo getReward(RewardType rewardType, boolean isPass) {
        RewardInfoMongo.RewardInfoMongoBuilder builder = RewardInfoMongo.builder();

        // 기본값: 성공이면 생명 +1, 실패면 생명 -1
        builder.rewardLife(isPass ? 1 : -1);

        switch (rewardType) {
            case GOLD:
                int gold = secureRandom.nextInt(50) + 1; // 1~50 골드
                builder.rewardGold(isPass ? gold : -gold);
                break;

            case STAT:
                int statValue = secureRandom.nextInt(3) + 1; // 1~3 사이 스탯 변화
                int statType = secureRandom.nextInt(4); // 0~3 → 힘, 민첩, 지능, 운 중 하나
                switch (statType) {
                    case 0 -> builder.rewardStrength(isPass ? statValue : -statValue);
                    case 1 -> builder.rewardAgility(isPass ? statValue : -statValue);
                    case 2 -> builder.rewardIntelligence(isPass ? statValue : -statValue);
                    case 3 -> builder.rewardLuck(isPass ? statValue : -statValue);
                }
                break;
            case ITEM:
            case NONE:
            default:
                // 아무 보상 없음
                break;
        }

        return builder.build();
    }

    public static PlayerInfoMongo updateReward(PlayerInfoMongo playerInfo, RewardInfoMongo rewardInfo) {
        if (rewardInfo != null) {
            if (rewardInfo.getRewardStrength() != null)
                playerInfo.setStrength(playerInfo.getStrength() + rewardInfo.getRewardStrength());

            if (rewardInfo.getRewardAgility() != null)
                playerInfo.setAgility(playerInfo.getAgility() + rewardInfo.getRewardAgility());

            if (rewardInfo.getRewardIntelligence() != null)
                playerInfo.setIntelligence(playerInfo.getIntelligence() + rewardInfo.getRewardIntelligence());

            if (rewardInfo.getRewardLuck() != null)
                playerInfo.setLuck(playerInfo.getLuck() + rewardInfo.getRewardLuck());

            if (rewardInfo.getRewardLife() != null)
                playerInfo.setLife(playerInfo.getLife() + rewardInfo.getRewardLife());

            if (rewardInfo.getRewardGold() != null)
                playerInfo.setGold(playerInfo.getGold() + rewardInfo.getRewardGold());

            if (rewardInfo.getRewardTrait() != null)
                playerInfo.setTrait(rewardInfo.getRewardTrait());
        }

        return playerInfo;
    }


    public static List<InventoryMongo> updateRewardItem(List<InventoryMongo> inventory, RewardInfoMongo rewardInfo) {
        if (rewardInfo == null || rewardInfo.getGainedItemDefId() == null) {
            return inventory;
        }

        for (String id : rewardInfo.getGainedItemDefId()) {
            InventoryMongo newItem = InventoryMongo.builder()
                    .itemDefId(id)
                    .equipped(false)
                    .source("Scenario")
                    .build();
            inventory.add(newItem);
        }

        return inventory;
    }

    public static List<String> updateInGameItem(List<String> inGameItem, RewardInfoMongo rewardInfo) {
        if (rewardInfo == null || rewardInfo.getGainedItemDefId() == null) {
            return inGameItem;
        }

        for (String id : rewardInfo.getGainedItemDefId()) {
            inGameItem.add(id);
        }

        return inGameItem;
    }
}