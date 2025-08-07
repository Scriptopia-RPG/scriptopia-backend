package com.scriptopia.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ItemEffect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: ItemDefs
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemDef itemDefs;

    // FK: EffectGradeDef
    @ManyToOne(fetch = FetchType.LAZY)
    private EffectGradeDef effectGradeDef;

    // 예를 들어 효과 이름이나 수치 같은 필드가 있다면 여기에 추가
    private String effectName;
    private int effectValue;
}
