package com.scriptopia.demo.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfoMongo {
    private String name;
    private Integer life;
    private Integer level;
    private Integer experiencePoint;
    private Integer combatPoint;
    private Integer healthPoint;
    private String trait;
    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer luck;
    private Integer gold;
}
