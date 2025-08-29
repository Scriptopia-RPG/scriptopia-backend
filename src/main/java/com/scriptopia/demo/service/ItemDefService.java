package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.EffectGradeDef;
import com.scriptopia.demo.domain.ItemDef;
import com.scriptopia.demo.domain.ItemEffect;
import com.scriptopia.demo.domain.ItemGradeDef;
import com.scriptopia.demo.dto.develop.ItemDefResponse;
import com.scriptopia.demo.dto.develop.ItemEffectResponse;
import com.scriptopia.demo.dto.items.*;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import com.scriptopia.demo.repository.ItemDefRepository;
import com.scriptopia.demo.repository.ItemGradeDefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemDefService {

    private final ItemDefRepository itemDefRepository;
    private final ItemGradeDefRepository itemGradeDefRepository;
    private final EffectGradeDefRepository effectGradeDefRepository;

    @Transactional
    public ItemDefResponse createItem(ItemDefRequest dto) {
        // ItemGradeDef 조회
        ItemGradeDef gradeDef = itemGradeDefRepository.findById(dto.getItemGradeDefId())
                .orElseThrow(() -> new IllegalArgumentException("ItemGradeDef not found"));

        // ItemDef 생성
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

        // ItemEffect 생성
        if (dto.getEffects() != null) {
            for (ItemEffectRequest effectDto : dto.getEffects()) {
                EffectGradeDef effectGradeDef = effectGradeDefRepository.findById(effectDto.getGrade().ordinal() + 1L)
                        .orElseThrow(() -> new IllegalArgumentException("EffectGradeDef not found"));

                ItemEffect effect = new ItemEffect();
                effect.setItemDef(itemDef);
                effect.setEffectGradeDef(effectGradeDef);
                effect.setEffectName(effectDto.getEffectName());
                effect.setEffect_description(effectDto.getEffectDescription());

                itemDef.getItemEffects().add(effect);
            }
        }

        // ItemDef 저장 (cascade로 ItemEffect도 같이 저장)
        itemDefRepository.save(itemDef);

        // DTO 변환 후 반환
        return toResponse(itemDef);
    }

    // ================== DTO 변환 ==================
    private ItemDefResponse toResponse(ItemDef itemDef) {
        ItemDefResponse response = new ItemDefResponse();
        response.setId(itemDef.getId());
        response.setName(itemDef.getName());
        response.setDescription(itemDef.getDescription());
        response.setPicSrc(itemDef.getPicSrc());
        response.setItemType(itemDef.getItemType().name());
        response.setMainStat(itemDef.getMainStat().name());
        response.setBaseStat(itemDef.getBaseStat());
        response.setStrength(itemDef.getStrength());
        response.setAgility(itemDef.getAgility());
        response.setIntelligence(itemDef.getIntelligence());
        response.setLuck(itemDef.getLuck());
        response.setPrice(itemDef.getPrice());
        response.setCreatedAt(itemDef.getCreatedAt());

        List<ItemEffectResponse> effects = itemDef.getItemEffects().stream()
                .map(effect -> {
                    ItemEffectResponse eResp = new ItemEffectResponse();
                    eResp.setEffectName(effect.getEffectName());
                    eResp.setEffectDescription(effect.getEffect_description());
                    eResp.setGrade(effect.getEffectGradeDef().getGrade().name());
                    return eResp;
                })
                .collect(Collectors.toList());

        response.setEffects(effects);

        return response;
    }
}