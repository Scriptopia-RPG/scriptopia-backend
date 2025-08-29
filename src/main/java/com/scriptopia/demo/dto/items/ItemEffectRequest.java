package com.scriptopia.demo.dto.items;

import com.scriptopia.demo.domain.Grade;
import lombok.Data;

@Data
public class ItemEffectRequest {
    private String effectName;
    private String effectDescription;
    private Grade grade;
    private Integer effectValue;
}