package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemCategory;
import com.scriptopia.demo.domain.MainStat;
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
    private ItemCategory category; // WEAPON, ARMOR, ARTIFACT, POTION
    private Integer baseStat;
    private List<ItemEffectMongo> itemEffect;
    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer luck;
    private MainStat mainStat; // strength, agility, intelligence, luck
    private Integer weight;
    private Grade grade; // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    private Integer price;
}
