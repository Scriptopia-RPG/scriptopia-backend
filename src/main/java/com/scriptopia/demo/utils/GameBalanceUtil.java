package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import com.scriptopia.demo.repository.ItemGradeDefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameBalanceUtil {

    private final ItemGradeDefRepository itemGradeDefRepository;
    private final EffectGradeDefRepository effectGradeDefRepository;
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
}
