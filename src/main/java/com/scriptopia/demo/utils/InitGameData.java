package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemDef;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.Stat;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import com.scriptopia.demo.repository.ItemDefRepository;
import com.scriptopia.demo.repository.ItemGradeDefRepository;
import lombok.Data;

import java.security.SecureRandom;

@Data
public class InitGameData {

    static SecureRandom secureRandom = new SecureRandom();
    private ItemGradeDefRepository itemGradeDefRepository;
    private EffectGradeDefRepository effectGradeDefRepository;

    static final int PLAYER_BASE_STAT = 5;

    //PlayerInfo
    private Integer life;
    private Integer healthPoint;
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

    public InitGameData(Stat playerStat, Grade grade) {
        this.life = 5;
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


        int[] stats = GameBalanceUtil.initItemStat(grade);
        this.itemStr = stats[0];
        this.itemAgi = stats[1];
        this.itemInt = stats[2];
        this.itemLuk = stats[3];

        int priceRate = secureRandom.nextInt(21) - 10;
        Long gradePrice = (long) itemGradeDefRepository.findPriceByGrade(grade);
        Long effectPrice = (long) Math.floor(effectGradeDefRepository.findPriceByGrade(grade) * (1 + priceRate / 100.0));

        this.itemPrice = gradePrice + effectPrice;

    }

}
