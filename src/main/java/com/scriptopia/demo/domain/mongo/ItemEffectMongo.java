package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.Grade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemEffectMongo {
    private String itemEffectName;
    private String itemEffectDescription;
    private Grade grade; // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    private Integer itemEffectWeight;
}
