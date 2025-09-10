package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.domain.mongo.ItemDefMongo;
import com.scriptopia.demo.domain.mongo.ItemEffectMongo;
import com.scriptopia.demo.dto.items.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import com.scriptopia.demo.repository.ItemDefRepository;
import com.scriptopia.demo.repository.ItemGradeDefRepository;
import com.scriptopia.demo.repository.mongo.ItemDefMongoRepository;
import com.scriptopia.demo.utils.GameBalanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemDefService {

    private final ItemDefRepository itemDefRepository;
    private final ItemGradeDefRepository itemGradeDefRepository;
    private final EffectGradeDefRepository effectGradeDefRepository;
    private final ItemDefMongoRepository itemDefMongoRepository;
    private final FastApiService fastApiService;


    @Transactional(readOnly = false)
    public ItemFastApiResponse createItem(ItemDefRequest request) {
        /**
         * 1. 카테고리
         * 2. 등급
         * 3. 메인 스탯
         * 4. 베이스 스탯 (공격력, 체력)
         * 5. 아이템 이펙트( 최대 등급 3개)
         * 6. 추가 스탯
         */
        ItemType itemCategory = ItemType.getRandomItemType();
        Grade itemGrade = Grade.getRandomGradeByProbability();
        int baseStat = Grade.getRandomBaseStat(itemCategory, itemGrade);
        Stat mainStat = Stat.getRandomMainStat();
        int[] additionalStats = GameBalanceUtil.getRandomItemStatsByGrade(itemGrade); // strength, agility, intelligence, luck


        List<EffectProbability> effectGrades = new ArrayList<>();
        List<Long> effectGradesList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            EffectProbability effectGrade = EffectProbability.getRandomEffectGradeByWeaponGrade(itemGrade);
            if (effectGrade != null) {
                Long effectPrice = effectGradeDefRepository.findPriceByEffectProbability(effectGrade)
                        .orElseThrow(() -> new IllegalStateException("EffectGradeDef not found: " + effectGrade));

                effectGradesList.add(effectPrice);
                effectGrades.add(effectGrade);
                // effectGradesList.add(effectGradeDefRepository.findPriceByEffectProbability(effectGrade).get());
            }
        }

        System.out.println(effectGrades);
        Long gradeGradePrice = itemGradeDefRepository.findPriceByGrade(itemGrade);
        Long itemPrice = GameBalanceUtil.getItemPriceByGrade(gradeGradePrice, effectGradesList);





        ItemFastApiRequest fastRequest = ItemFastApiRequest.builder()
                .worldView(request.getWorldView())
                .location(request.getLocation())
                .category(itemCategory)
                .baseStat(baseStat)
                .mainStat(mainStat)
                .grade(itemGrade)
                .itemEffect(effectGrades)
                .strength(additionalStats[0])
                .agility(additionalStats[1])
                .intelligence(additionalStats[2])
                .luck(additionalStats[3])
                .price(itemPrice)
                .playerTrait(request.getPlayerTrait())
                .previousStory(request.getPreviousStory())
                .build();


        ItemFastApiResponse response = fastApiService.item(fastRequest);

        if (response == null) {
            throw new CustomException(ErrorCode.E_500_EXTERNAL_API_ERROR);
        }


        List<ItemEffectMongo> mongoEffects = new ArrayList<>();
        List<ItemFastApiResponse.ItemEffect> apiEffects = response.getItemEffect();

        for (int i = 0; i < apiEffects.size(); i++) {
            ItemFastApiResponse.ItemEffect apiEffect = apiEffects.get(i);
            EffectProbability effectGrade = i < effectGrades.size() ? effectGrades.get(i) : null;

            mongoEffects.add(ItemEffectMongo.builder()
                    .effectProbability(effectGrade != null ? (effectGrade) : EffectProbability.COMMON)
                    .itemEffectName(apiEffect.getItemEffectName())
                    .itemEffectDescription(apiEffect.getItemEffectDescription())
                    .build());
        }

        ItemDefMongo itemDefMongo = ItemDefMongo.builder()
                .itemPicSrc("test link")
                .name(response.getItemName())
                .description(response.getItemDescription())
                .category(itemCategory)
                .baseStat(baseStat)
                .itemEffect(mongoEffects)
                .strength(additionalStats[0])
                .agility(additionalStats[1])
                .intelligence(additionalStats[2])
                .luck(additionalStats[3])
                .mainStat(mainStat)
                .grade(itemGrade)
                .price(itemPrice)
                .build();

        itemDefMongoRepository.save(itemDefMongo);

        ItemDef itemDefRdb = new ItemDef();
        itemDefRdb.setName(itemDefMongo.getName());
        itemDefRdb.setDescription(itemDefMongo.getDescription());
        itemDefRdb.setItemGradeDef(itemGradeDefRepository.findByGrade(itemGrade).get());
        itemDefRdb.setPicSrc(itemDefMongo.getItemPicSrc());
        itemDefRdb.setItemType(itemDefMongo.getCategory());
        itemDefRdb.setBaseStat(itemDefMongo.getBaseStat());
        itemDefRdb.setStrength(itemDefMongo.getStrength());
        itemDefRdb.setAgility(itemDefMongo.getAgility());
        itemDefRdb.setIntelligence(itemDefMongo.getIntelligence());
        itemDefRdb.setLuck(itemDefMongo.getLuck());
        itemDefRdb.setMainStat(itemDefMongo.getMainStat());
        itemDefRdb.setPrice(itemDefMongo.getPrice());
        itemDefRdb.setCreatedAt(LocalDateTime.now());

        List<ItemEffect> rdbEffects = new ArrayList<>();
        for (ItemEffectMongo effectMongo : itemDefMongo.getItemEffect()) {
            ItemEffect effect = new ItemEffect();
            effect.setItemDef(itemDefRdb);
            effect.setEffectName(effectMongo.getItemEffectName());
            effect.setEffectDescription(effectMongo.getItemEffectDescription());
            effect.setEffectGradeDef(effectGradeDefRepository.findByEffectProbability(effectMongo.getEffectProbability()).get());
            rdbEffects.add(effect);
        }
        itemDefRdb.setItemEffects(rdbEffects);

        itemDefRepository.save(itemDefRdb);

        return response;

    }



}