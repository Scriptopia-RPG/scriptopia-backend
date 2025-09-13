package com.scriptopia.demo.dto.items;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.Stat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    private String name;
    private String description;
    private String picSrc;
    private ItemType itemType; // WEAPON, ARMOR, ARTIFACT
    private Integer baseStat;
    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer luck;
    private Stat mainStat; // STRENGTH, AGILITY, INTELLIGENCE, LUCK
    private Grade grade;    // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    private List<ItemEffectDTO> itemEffect;
    private Integer remainingUses;
    private Long price;
}
