package com.scriptopia.demo.domain;


import lombok.Getter;

@Getter
public enum Chapter {
    CHAPTER1(new int[]{14,16,14,12,10,9,8,7,6,4,1,1}),
    CHAPTER2(new int[]{8,9,9,12,12,12,11,10,10,7,5,5}),
    CHAPTER3(new int[]{4,5,6,7,9,11,13,13,12,10,6,7});

    private final int[] npcProbabilities;

    Chapter(int[] npcProbabilities) {
        this.npcProbabilities = npcProbabilities;
    }

    public int[] getNpcProbabilities() {
        return npcProbabilities;
    }
}
