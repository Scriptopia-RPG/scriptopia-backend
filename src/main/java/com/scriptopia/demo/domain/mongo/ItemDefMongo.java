package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.Stat;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDefMongo {
    @Id
    private String id;

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
