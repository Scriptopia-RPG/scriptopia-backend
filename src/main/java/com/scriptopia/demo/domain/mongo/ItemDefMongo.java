package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.Stat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDefMongo {
    private Long itemDefId;
    private String itemPicSrc;
    private String name;
    private String description;
    private ItemType category; // WEAPON, ARMOR, ARTIFACT, *POTION* 타입 때문에 애매함
    private Integer baseStat;
    private List<ItemEffectMongo> itemEffect;
    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer luck;
    private Stat mainStat; // strength, agility, intelligence, luck
    private Grade grade; // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    private Long price;
}
