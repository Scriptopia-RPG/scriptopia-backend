package com.scriptopia.demo.dto.items;

import com.scriptopia.demo.domain.EffectProbability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEffectDTO {
    private EffectProbability effectProbability;
    private String effectName;
    private String description;
}
