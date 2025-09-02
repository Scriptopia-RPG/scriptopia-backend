package com.scriptopia.demo.domain;

public enum Grade {
    COMMON(30, 137),
    UNCOMMON(35, 177),
    RARE(40, 216),
    EPIC(45, 260),
    LEGENDARY(50, 312);

    private final int attackPower;   // 무기 공격력
    private final int defensePower;  // 방어구 방어력

    Grade(int attackPower, int defensePower) {
        this.attackPower = attackPower;
        this.defensePower = defensePower;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefensePower() {
        return defensePower;
    }

}
