package com.scriptopia.demo.config;

import com.scriptopia.demo.domain.EffectGradeDef;
import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.Stat;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import com.scriptopia.demo.repository.ItemGradeDefRepository;
import com.scriptopia.demo.utils.GameBalanceUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

@Data
public class InitGameData {

    static SecureRandom secureRandom = new SecureRandom();
    private ItemGradeDefRepository itemGradeDefRepository;
    private EffectGradeDefRepository effectGradeDefRepository;

    static final int PLAYER_BASE_STAT = 5;

    //PlayerInfo
    private int life;
    private int healthPoint;
    private int playerStr;
    private int playerAgi;
    private int playerInt;
    private int playerLuk;
    private int gold;

    //ItemDef
    private ItemType category;
    private int baseStat;
    private int itemEffectWeight;
    private int itemStr;
    private int itemAgi;
    private int itemInt;
    private int itemLuk;
    private int itemPrice;

    public InitGameData(Stat playerStat, Grade grade) {
        this.life = 5;
        this.healthPoint = 80;

        int mainStat = secureRandom.nextInt(3);
        int subStat = secureRandom.nextInt(3) - 2;

        this.playerStr = (playerStat.equals(Stat.STRENGTH)) ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        this.playerAgi = (playerStat.equals(Stat.AGILITY)) ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        this.playerInt = (playerStat.equals(Stat.INTELLIGENCE)) ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        this.playerLuk = (playerStat.equals(Stat.LUCK)) ? PLAYER_BASE_STAT + mainStat : PLAYER_BASE_STAT + subStat;
        this.gold = secureRandom.nextInt(50) + 50;

        this.category = ItemType.WEAPON;

        int[] stats = GameBalanceUtil.initItemStat(grade);
        this.itemStr = stats[0];
        this.itemAgi = stats[1];
        this.itemInt = stats[2];
        this.itemLuk = stats[3];

        int rate = secureRandom.nextInt(21) - 10;
        int gradePrice = itemGradeDefRepository.findPriceByGrade(grade);
        int effectPrice = (int) Math.floor(effectGradeDefRepository.findPriceByGrade(grade) * (1 + rate / 100.0));

        this.itemPrice = gradePrice + effectPrice;

    }


}
