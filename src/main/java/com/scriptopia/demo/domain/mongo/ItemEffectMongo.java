package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.EffectProbability;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemEffectMongo {
    private String itemEffectName;
    private String itemEffectDescription;
    private EffectProbability effectProbability; // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
}