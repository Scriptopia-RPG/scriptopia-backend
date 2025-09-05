package com.scriptopia.demo.domain;

import java.security.SecureRandom;

public enum ChoiceResultType {
    BATTLE(40),
    CHOICE(30),
    SHOP(10),
    NONE(50);

    private int nextEventType;

    private static final SecureRandom random = new SecureRandom();

    ChoiceResultType(int nextEventType) {
        this.nextEventType = nextEventType;
    }


    public ChoiceResultType nextResultType() {
        int rand = random.nextInt(nextEventType);
        int cumulative = 0;

        for(ChoiceResultType type : values()) {
            if(rand == type.nextEventType) {
                return type;
            }
        }
        return NONE;
    }
}
