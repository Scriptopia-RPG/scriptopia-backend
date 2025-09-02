package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.Grade;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemEffectMongo {
    private String itemEffectName;
    private String itemEffectDescription;
    private Grade grade; // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
}
