package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import com.scriptopia.demo.repository.ItemGradeDefRepository;
import lombok.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class InitGameData {

    private final ItemGradeDefRepository itemGradeDefRepository;
    private final EffectGradeDefRepository effectGradeDefRepository;

    static SecureRandom secureRandom = new SecureRandom();
    static final int PLAYER_BASE_STAT = 5;
    //Game Session
    private List<Integer> stages;

    //PlayerInfo
    private Integer life;
    private Integer level;
    private Integer healthPoint;
    private Integer experiencePoint;
    private Integer playerStr;
    private Integer playerAgi;
    private Integer playerInt;
    private Integer playerLuk;
    private Long gold;

    //ItemDef
    private ItemType category;
    private Integer baseStat;
    private Integer itemStr;
    private Integer itemAgi;
    private Integer itemInt;
    private Integer itemLuk;
    private Long itemPrice;

    public InitGameData(Stat playerStat, Grade grade, ItemGradeDefRepository itemRepo,
                        EffectGradeDefRepository effectRepo) {


        this.itemGradeDefRepository = itemRepo;
        this.effectGradeDefRepository = effectRepo;

        this.stages = initStage();

        this.life = 5;
        this.level = 1;
        this.experiencePoint = 0;
        this.healthPoint = 80;

        int mainStat = secureRandom.nextInt(3);
        int subStat = secureRandom.nextInt(3) - 2;



        this.playerStr = (playerStat.equals(Stat.STRENGTH)) ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        this.playerAgi = (playerStat.equals(Stat.AGILITY)) ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        this.playerInt = (playerStat.equals(Stat.INTELLIGENCE)) ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        this.playerLuk = (playerStat.equals(Stat.LUCK)) ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        this.gold = secureRandom.nextLong(50) + 50;

        this.category = ItemType.WEAPON;

        int attackRate = secureRandom.nextInt(21) - 10;
        this.baseStat = (int) Math.floor(Grade.COMMON.getAttackPower() * (1 + attackRate / 100.0));





        // 배열 생성 (0: STR, 1: AGI, 2: INT, 3: LUCK)
        int[] stats = GameBalanceUtil.getRandomItemStatsByGrade(grade);
        this.itemStr = stats[0];
        this.itemAgi = stats[1];
        this.itemInt = stats[2];
        this.itemLuk = stats[3];


        List<Long> itemEffectList = new ArrayList<>();
        itemEffectList.add(effectGradeDefRepository.findPriceByEffectProbability(EffectProbability.COMMON).get());
        Long gradePrice = itemGradeDefRepository.findPriceByGrade(grade);

        this.itemPrice = GameBalanceUtil.getItemPriceByGrade(gradePrice , itemEffectList);

    }

    private List<Integer> initStage(){
        List<Integer> arr = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> result = new ArrayList<>();

        int leadingZeros = 2 + secureRandom.nextInt(3); // 2~4
        for (int j = 0; j < leadingZeros; j++) {
            result.add(0);
        }

        for (int i = 0; i < arr.size(); i++) {
            result.add(arr.get(i));
            if (i < arr.size() - 1) {
                int zeros = 2 + secureRandom.nextInt(3);
                for (int j = 0; j < zeros; j++) {
                    result.add(0);
                }
            }
        }
        return result;
    }

}
