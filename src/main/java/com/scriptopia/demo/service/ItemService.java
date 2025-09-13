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
import com.scriptopia.demo.utils.InitItemData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemDefRepository itemDefRepository;
    private final ItemGradeDefRepository itemGradeDefRepository;
    private final EffectGradeDefRepository effectGradeDefRepository;
    private final ItemDefMongoRepository itemDefMongoRepository;
    private final FastApiService fastApiService;



    @Transactional
    public ItemFastApiResponse createItemInit(ItemDefRequest request, InitItemData initItemData){
        /**
         * 1. 카테고리
         * 2. 등급
         * 3. 메인 스탯
         * 4. 베이스 스탯 (공격력, 체력)
         * 5. 아이템 이펙트( 최대 등급 3개)
         * 6. 추가 스탯
         */
        ItemFastApiRequest fastRequest = ItemFastApiRequest.builder()
                .worldView(request.getWorldView())
                .location(request.getLocation())
                .category(initItemData.getItemType())
                .baseStat(initItemData.getBaseStat())
                .mainStat(initItemData.getMainStat())
                .grade(initItemData.getGrade())
                .itemEffect(initItemData.getEffectGrades())
                .strength(initItemData.getStats()[0])
                .agility(initItemData.getStats()[1])
                .intelligence(initItemData.getStats()[2])
                .luck(initItemData.getStats()[3])
                .price(initItemData.getItemPrice())
                .playerTrait(request.getPlayerTrait())
                .previousStory(request.getPreviousStory())
                .build();


        ItemFastApiResponse response = fastApiService.item(fastRequest);

        if (response == null) {
            throw new CustomException(ErrorCode.E_500_EXTERNAL_API_ERROR);
        }
        return response;
    }



    /**
     * mongoDB, RDB에 저장 후 mongoDB의 item_Def_id를 리턴
     * @param request
     * @return
     */
    @Transactional(readOnly = false)
    public String createMongoItem(ItemDefRequest request) {

        //아이템 초기 수치 생성
        InitItemData initItemData = new InitItemData(itemGradeDefRepository, effectGradeDefRepository);

        //fastAPI 통해서 아이템 생성
        ItemFastApiResponse response = createItemInit(request, initItemData);


        List<ItemEffectMongo> mongoEffects = new ArrayList<>();
        List<ItemFastApiResponse.ItemEffect> apiEffects = response.getItemEffect();

        List<EffectProbability> effectGrades = initItemData.getEffectGrades();



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
                .category(initItemData.getItemType())
                .baseStat(initItemData.getBaseStat())
                .itemEffect(mongoEffects)
                .strength(initItemData.getStats()[0])
                .agility(initItemData.getStats()[1])
                .intelligence(initItemData.getStats()[2])
                .luck(initItemData.getStats()[3])
                .mainStat(initItemData.getMainStat())
                .grade(initItemData.getGrade())
                .price(initItemData.getItemPrice())
                .build();


        itemDefMongoRepository.save(itemDefMongo);

        ItemDef itemDefRdb = new ItemDef();
        itemDefRdb.setName(itemDefMongo.getName());
        itemDefRdb.setDescription(itemDefMongo.getDescription());
        itemDefRdb.setItemGradeDef(itemGradeDefRepository.findByGrade(initItemData.getGrade()).get());
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

        return itemDefMongo.getId();
    }



}