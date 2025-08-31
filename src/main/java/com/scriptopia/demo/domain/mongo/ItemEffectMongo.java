package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemEffectMongo {
    private String itemEffectName;
    private String itemEffectDescription;
    private Grade grade; // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    private Integer itemEffectWeight;
}
