package com.scriptopia.demo.dto.gamesession.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InGamePlayerResponse {
    private String name;
    private int life;
    private int level;
    private int healthPoint;
    private int experiencePoint;
    private String trait;
    private int strength;
    private int agility;
    private int intelligence;
    private int luck;
    private long gold;
}


