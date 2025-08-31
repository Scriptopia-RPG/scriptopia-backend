package com.scriptopia.demo.domain.mongo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemDefMongo {
    private Long itemDefId;
    private String itemPicSrc;
    private String name;
    private String description;
    private String category; // WEAPON, ARMOR, ARTIFACT, POTION
    private Integer baseStat;
    private List<ItemEffectMongo> itemEffect;
    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer luck;
    private String mainStat; // strength, agility, intelligence, luck
    private Integer weight;
    private String grade; // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    private Integer price;
}
