package com.scriptopia.demo.domain;

import lombok.Getter;

import java.security.SecureRandom;


@Getter
public enum ChoiceEventType {
    LIVING(50),
    NONLIVING(50);


    private final int ChoiceEventChance;


    private static final SecureRandom random = new SecureRandom();


    ChoiceEventType(final int ChoiceEventChance) {
        this.ChoiceEventChance = ChoiceEventChance;
    }

    public static ChoiceEventType getChoiceEventType() {
        int rand = random.nextInt(100) + 1;
        int cumulative = 0;

        for (ChoiceEventType Choicetype : ChoiceEventType.values()) {
            cumulative += Choicetype.getChoiceEventChance();
            if (cumulative >= rand) {
                return Choicetype;
            }
        }
        return NONLIVING;
    }
}