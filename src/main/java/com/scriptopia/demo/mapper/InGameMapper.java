package com.scriptopia.demo.mapper;


import com.scriptopia.demo.domain.mongo.*;
import com.scriptopia.demo.dto.gamesession.ingame.InGameChoiceResponse;
import com.scriptopia.demo.dto.gamesession.ingame.InGameInventoryResponse;
import com.scriptopia.demo.dto.gamesession.ingame.InGameNpcResponse;
import com.scriptopia.demo.dto.gamesession.ingame.InGamePlayerResponse;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.mongo.ItemDefMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InGameMapper {
    private final ItemDefMongoRepository itemDefMongoRepository;



    public InGamePlayerResponse mapPlayer(PlayerInfoMongo player) {
        if (player == null) return null;
        return InGamePlayerResponse.builder()
                .name(player.getName())
                .life(player.getLife())
                .level(player.getLevel())
                .healthPoint(player.getHealthPoint())
                .experiencePoint(player.getExperiencePoint())
                .trait(player.getTrait())
                .strength(player.getStrength())
                .agility(player.getAgility())
                .intelligence(player.getIntelligence())
                .luck(player.getLuck())
                .gold(player.getGold())
                .build();
    }

    public InGameNpcResponse mapNpc(NpcInfoMongo npc) {
        if (npc == null) return null;
        return InGameNpcResponse.builder()
                .name(npc.getName())
                .rank(npc.getRank())
                .trait(npc.getTrait())
                .strength(npc.getStrength())
                .agility(npc.getAgility())
                .intelligence(npc.getIntelligence())
                .luck(npc.getLuck())
                .npcWeaponName(npc.getNpcWeaponName())
                .npcWeaponDescription(npc.getNpcWeaponDescription())
                .build();
    }

    public List<InGameInventoryResponse> mapInventory(List<InventoryMongo> inventoryList) {
        if (inventoryList == null) return List.of();

        return inventoryList.stream()
                .map(inv -> {
                    ItemDefMongo itemDef = itemDefMongoRepository.findById(inv.getItemDefId())
                            .orElseThrow(() -> new CustomException(ErrorCode.E_404_ITEM_NOT_FOUND));

                    return InGameInventoryResponse.builder()
                            // 인벤토리(소유) 정보
                            .itemDefId(inv.getItemDefId())
                            .acquiredAt(inv.getAcquiredAt())
                            .equipped(inv.isEquipped())
                            .source(inv.getSource())

                            // 아이템 정의 정보
                            .name(itemDef.getName())
                            .description(itemDef.getDescription())
                            .itemPicSrc(itemDef.getItemPicSrc())
                            .category(itemDef.getCategory().name())
                            .baseStat(itemDef.getBaseStat())
                            .itemEffects(itemDef.getItemEffect().stream()
                                    .map(e -> InGameInventoryResponse.ItemEffect.builder()
                                            .itemEffectName(e.getItemEffectName())
                                            .itemEffectDescription(e.getItemEffectDescription())
                                            .grade(e.getEffectProbability().name())
                                            .build())
                                    .toList())
                            .strength(itemDef.getStrength())
                            .agility(itemDef.getAgility())
                            .intelligence(itemDef.getIntelligence())
                            .luck(itemDef.getLuck())
                            .mainStat(itemDef.getMainStat().name())
                            .grade(itemDef.getGrade().name())
                            .price(itemDef.getPrice().intValue())
                            .build();
                })
                .toList();
    }

    public List<InGameChoiceResponse.Choice> mapChoice(ChoiceInfoMongo choiceInfo) {
        if (choiceInfo == null || choiceInfo.getChoice() == null) return List.of();

        return choiceInfo.getChoice().stream()
                .limit(3) // 최대 3개만
                .map(ch -> InGameChoiceResponse.Choice.builder()
                        .detail(ch.getDetail())
                        .stats(ch.getStats() != null ? ch.getStats().name() : null) // null-safe
                        .probability(ch.getProbability())
                        .build())
                .toList();
    }



}
