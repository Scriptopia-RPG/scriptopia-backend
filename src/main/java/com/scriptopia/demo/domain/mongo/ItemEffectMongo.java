package com.scriptopia.demo.domain.mongo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemEffectMongo {
    private String itemEffectName;
    private String itemEffectDescription;
    private String grade; // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    private Integer itemEffectWeight;
}
