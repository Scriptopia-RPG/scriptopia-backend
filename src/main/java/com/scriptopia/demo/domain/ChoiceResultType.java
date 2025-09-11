package com.scriptopia.demo.domain;

import lombok.Getter;

import java.security.SecureRandom;

@Getter
public enum ChoiceResultType {
    BATTLE(40),
    CHOICE(30),
    SHOP(10),
    NONE(30);

    private final int nextEventType;

    private static final SecureRandom random = new SecureRandom();

    ChoiceResultType(int nextEventType) {
        this.nextEventType = nextEventType;
    }


    public static ChoiceResultType nextResultType() {
        int rand = random.nextInt(100) + 1;
        int cumulative = 0;

        for(ChoiceResultType type : values()) {
            cumulative += type.getNextEventType();
            if(rand <= cumulative ) {
                return type;
            }
        }
        return NONE;
    }
}
