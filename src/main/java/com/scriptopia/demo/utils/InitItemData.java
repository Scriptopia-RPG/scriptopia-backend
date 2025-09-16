package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.EffectProbability;
import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.Stat;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import com.scriptopia.demo.repository.ItemGradeDefRepository;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InitItemData {
    private ItemType itemType;
    private Grade grade;
    private Integer baseStat;
    private Stat mainStat;
    private int[] stats;
    private List<EffectProbability> effectGrades;
    private List<Long> effectPrices;
    private Long gradePrice;
    private Long itemPrice;
    private Integer remainingUses;

    public InitItemData(ItemGradeDefRepository itemGradeDefRepository,
                        EffectGradeDefRepository effectGradeDefRepository) {

        // 아이템 타입, 등급, 기본 스탯, 메인 스탯
        this.itemType = ItemType.getRandomItemType();
        this.grade = Grade.getRandomGradeByProbability();
        this.baseStat = Grade.getRandomBaseStat(itemType, grade);
        this.mainStat = Stat.getRandomMainStat();

        // 추가 스탯
        this.stats = GameBalanceUtil.getRandomItemStatsByGrade(grade);

        // 이펙트 스탯 및 가격
        this.effectGrades = new ArrayList<>();
        this.effectPrices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            EffectProbability effectGrade = EffectProbability.getRandomEffectGradeByWeaponGrade(grade);
            if (effectGrade != null) {
                Long effectPrice = effectGradeDefRepository.findPriceByEffectProbability(effectGrade)
                        .orElseThrow(() -> new IllegalStateException("EffectGradeDef not found: " + effectGrade));
                this.effectGrades.add(effectGrade);
                this.effectPrices.add(effectPrice);
            }
        }

        // 등급 가격, 최종 아이템 가격
        this.gradePrice = itemGradeDefRepository.findPriceByGrade(grade);
        this.itemPrice = GameBalanceUtil.getItemPriceByGrade(gradePrice, effectPrices);
        this.remainingUses = 5;
    }
}
