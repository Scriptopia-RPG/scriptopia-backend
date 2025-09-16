package com.scriptopia.demo.domain;

import lombok.Getter;

import java.security.SecureRandom;


@Getter
public enum ChoiceResultType {
    BATTLE(20),
    CHOICE(40),
    DONE(45),
    SHOP(5);

    private final int nextEventType;

    private static final SecureRandom random = new SecureRandom();

    ChoiceResultType(int nextEventType) {
        this.nextEventType = nextEventType;
    }


    public static ChoiceResultType nextResultType(ChoiceEventType nextEventType) {
        int rand = 0;
        if( nextEventType == ChoiceEventType.LIVING){
            rand = random.nextInt(100) + 1; // 1 ~ 100
        }else{
            rand = random.nextInt(60) + 41; // 41 ~ 100
        }

        int cumulative = 0;

        for(ChoiceResultType type : values()) {
            cumulative += type.getNextEventType();
            if(rand <= cumulative ) {
                return type;
            }
        }
        return DONE;
    }
}
