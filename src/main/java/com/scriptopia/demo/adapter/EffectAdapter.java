package com.scriptopia.demo.adapter;

import com.scriptopia.demo.domain.EffectProbability;
import com.scriptopia.demo.domain.Grade;

public class EffectAdapter {

    public static Grade toGrade(EffectProbability effectProb) {
        if (effectProb == null) return null;

        return switch (effectProb) {
            case COMMON -> Grade.COMMON;
            case UNCOMMON -> Grade.UNCOMMON;
            case RARE -> Grade.RARE;
            case EPIC -> Grade.EPIC;
            case LEGENDARY -> Grade.LEGENDARY;
        };
    }
}
