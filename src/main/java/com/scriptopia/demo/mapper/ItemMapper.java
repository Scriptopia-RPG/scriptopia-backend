package com.scriptopia.demo.mapper;

import com.scriptopia.demo.domain.ItemDef;
import com.scriptopia.demo.domain.ItemEffect;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserItem;
import com.scriptopia.demo.dto.items.ItemDTO;
import com.scriptopia.demo.dto.items.ItemEffectDTO;
import com.scriptopia.demo.repository.UserItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {


    private final UserItemRepository userItemRepository;

    public List<ItemDTO> mapUser(User user) {
        List<ItemDTO> items = new ArrayList<>();

        List<UserItem> userItems = userItemRepository.findAllByUserId(user.getId());

        for (UserItem userItem : userItems) {
            ItemDef item = userItem.getItemDef();

            List<ItemEffectDTO> itemEffects = new ArrayList<>();

            for (ItemEffect effect : item.getItemEffects()) {
                itemEffects.add(
                        ItemEffectDTO.builder()
                                .effectProbability(effect.getEffectGradeDef().getEffectProbability())
                                .effectName(effect.getEffectName())
                                .description(effect.getEffectDescription())
                                .build()
                );

            }

            items.add(
                    ItemDTO.builder()
                            .name(item.getName())
                            .description(item.getDescription())
                            .picSrc(item.getPicSrc())
                            .itemType(item.getItemType())
                            .baseStat(item.getBaseStat())
                            .strength(item.getStrength())
                            .agility(item.getAgility())
                            .intelligence(item.getIntelligence())
                            .luck(item.getLuck())
                            .mainStat(item.getMainStat())
                            .grade(item.getItemGradeDef().getGrade())
                            .itemEffects(itemEffects)   // 리스트 주입
                            .remainingUses(userItem.getRemainingUses())
                            .price(item.getPrice())
                            .build()
            );
        }
        return items;
    }





}
