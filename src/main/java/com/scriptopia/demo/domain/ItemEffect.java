package com.scriptopia.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ItemEffect {

    @Id
    @GeneratedValue
    private Long id;

    // FK: ItemDefs
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemDef itemDef;

    // FK: EffectGradeDef
    @ManyToOne(fetch = FetchType.LAZY)
    private EffectGradeDef effectGradeDef;

    // 예를 들어 효과 이름
    private String effectName;
    
    // 아이템 효과 설명
    private String effect_description;
}
