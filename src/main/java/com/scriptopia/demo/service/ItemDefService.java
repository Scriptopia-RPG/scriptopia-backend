package com.scriptopia.demo.service;


import com.scriptopia.demo.domain.EffectGradeDef;
import com.scriptopia.demo.domain.ItemDef;
import com.scriptopia.demo.domain.ItemEffect;
import com.scriptopia.demo.domain.ItemGradeDef;
import com.scriptopia.demo.dto.items.ItemDefRequest;
import com.scriptopia.demo.dto.items.ItemEffectRequest;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import com.scriptopia.demo.repository.ItemDefRepository;
import com.scriptopia.demo.repository.ItemEffectRepository;
import com.scriptopia.demo.repository.ItemGradeDefRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemDefService {

    private final ItemDefRepository itemDefRepository;
    private final ItemEffectRepository itemEffectRepository;
    private final ItemGradeDefRepository itemGradeDefRepository;
    private final EffectGradeDefRepository effectGradeDefRepository;

    @Transactional
    public ItemDef createItem(ItemDefRequest dto) {
        // 1️⃣ ItemGradeDef 조회
        ItemGradeDef gradeDef = itemGradeDefRepository.findById(dto.getItemGradeDefId())
                .orElseThrow(() -> new IllegalArgumentException("ItemGradeDef not found"));

        // 2️⃣ ItemDef 생성
        ItemDef itemDef = new ItemDef();
        itemDef.setName(dto.getName());
        itemDef.setDescription(dto.getDescription());
        itemDef.setPicSrc(dto.getPicSrc());
        itemDef.setItemType(dto.getItemType());
        itemDef.setMainStat(dto.getMainStat());
        itemDef.setBaseStat(dto.getBaseStat());
        itemDef.setStrength(dto.getStrength());
        itemDef.setAgility(dto.getAgility());
        itemDef.setIntelligence(dto.getIntelligence());
        itemDef.setLuck(dto.getLuck());
        itemDef.setPrice(dto.getPrice());
        itemDef.setItemGradeDef(gradeDef);
        itemDef.setCreatedAt(LocalDateTime.now());

        itemDefRepository.save(itemDef);

        // 3️⃣ ItemEffect 생성
        if (dto.getEffects() != null) {
            for (ItemEffectRequest effectDto : dto.getEffects()) {
                EffectGradeDef effectGradeDef = effectGradeDefRepository.findById(effectDto.getGrade().ordinal() + 1L)
                        .orElseThrow(() -> new IllegalArgumentException("EffectGradeDef not found"));

                ItemEffect effect = new ItemEffect();
                effect.setItemDefs(itemDef);
                effect.setEffectGradeDef(effectGradeDef);
                effect.setEffectName(effectDto.getEffectName());
                effect.setEffectValue(effectDto.getEffectValue());

                itemEffectRepository.save(effect);
            }
        }

        return itemDef;
    }
}