package com.scriptopia.demo.domain;

import lombok.Getter;

import java.security.SecureRandom;

@Getter
public enum EffectProbability {

    COMMON(80, 16, 4, 0, 0, 0),
    UNCOMMON(75, 7, 12, 5, 0, 0),
    RARE(65, 5, 5, 17, 7, 0),
    EPIC(60, 4, 4, 4, 20, 8),
    LEGENDARY(55, 1, 2, 7, 23, 12);

    private final int nullProb;
    private final int commonProb;
    private final int uncommonProb;
    private final int rareProb;
    private final int epicProb;
    private final int legendaryProb;

    private static final SecureRandom random = new SecureRandom();



    EffectProbability(int n, int c, int u, int r, int e, int l) {
        this.nullProb = n;
        this.commonProb = c;
        this.uncommonProb = u;
        this.rareProb = r;
        this.epicProb = e;
        this.legendaryProb = l;
    }

    /**
     * @param weaponGrade
     * @return
     */
    public static EffectProbability getRandomEffectGradeByWeaponGrade(Grade weaponGrade) {
        EffectProbability prob = null;

        switch (weaponGrade) {
            case COMMON -> prob = EffectProbability.COMMON;
            case UNCOMMON -> prob = EffectProbability.UNCOMMON;
            case RARE -> prob = EffectProbability.RARE;
            case EPIC -> prob = EffectProbability.EPIC;
            case LEGENDARY -> prob = EffectProbability.LEGENDARY;
        }

        int[] probabilities = {
                prob.getNullProb(),
                prob.getCommonProb(),
                prob.getUncommonProb(),
                prob.getRareProb(),
                prob.getEpicProb(),
                prob.getLegendaryProb()
        };

        EffectProbability[] grades = {null, EffectProbability.COMMON, EffectProbability.UNCOMMON, EffectProbability.RARE, EffectProbability.EPIC, EffectProbability.LEGENDARY};

        int rand = random.nextInt(100) + 1; // 1~100
        int cumulative = 0;

        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (rand <= cumulative) {
                return grades[i];
            }
        }

        return null;
    }

}
