package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.domain.mongo.ItemDefMongo;
import com.scriptopia.demo.domain.mongo.ItemEffectMongo;
import com.scriptopia.demo.dto.items.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.*;
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
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;
    private final ItemEffectRepository itemEffectRepository;


    @Transactional
    public ItemFastApiResponse createItemInit(ItemDefRequest request, InitItemData initItemData) {
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
                .itemEffects(initItemData.getEffectGrades())
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
     *
     * @param request
     * @return
     */
    @Transactional(readOnly = false)
    public String createItemInGame(ItemDefRequest request) {

        //아이템 초기 수치 생성
        InitItemData initItemData = new InitItemData(itemGradeDefRepository, effectGradeDefRepository);

        //fastAPI 통해서 아이템 생성
        ItemFastApiResponse response = createItemInit(request, initItemData);


        // 생성한 아이템 효과 MongoDB 매핑
        List<ItemEffectMongo> mongoEffects = new ArrayList<>();

        List<ItemFastApiResponse.ItemEffect> createdItemEffects = response.getItemEffects();
        List<EffectProbability> effectProbabilities = initItemData.getEffectGrades();

        for (int i = 0; i < createdItemEffects.size(); i++) {
            ItemFastApiResponse.ItemEffect createdEffect = createdItemEffects.get(i);
            EffectProbability effectProbability = i < effectProbabilities.size() ? effectProbabilities.get(i) : null;

            mongoEffects.add(ItemEffectMongo.builder()
                    .effectProbability(effectProbability != null ? (effectProbability) : EffectProbability.COMMON)
                    .itemEffectName(createdEffect.getItemEffectName())
                    .itemEffectDescription(createdEffect.getItemEffectDescription())
                    .build());
        }


        //아이템 정보 MongoDB 매핑
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


        //아이템 정보 RDBMS 매핑
        ItemDef itemDefRdb = new ItemDef();
        itemDefRdb.setName(response.getItemName());
        itemDefRdb.setDescription(response.getItemDescription());
        itemDefRdb.setItemGradeDef(itemGradeDefRepository.findByGrade(initItemData.getGrade()).get());
        itemDefRdb.setPicSrc("test link"); // Mongo 참조 대신 직접 값 지정
        itemDefRdb.setItemType(initItemData.getItemType());
        itemDefRdb.setBaseStat(initItemData.getBaseStat());
        itemDefRdb.setStrength(initItemData.getStats()[0]);
        itemDefRdb.setAgility(initItemData.getStats()[1]);
        itemDefRdb.setIntelligence(initItemData.getStats()[2]);
        itemDefRdb.setLuck(initItemData.getStats()[3]);
        itemDefRdb.setMainStat(initItemData.getMainStat());
        itemDefRdb.setPrice(initItemData.getItemPrice());
        itemDefRdb.setCreatedAt(LocalDateTime.now());



        List<ItemEffect> rdbEffects = new ArrayList<>();

        //아이템 효과 정보 RDBMS 매핑
        for (int i = 0; i < effectProbabilities.size(); i++) {
            ItemEffect effect = new ItemEffect();
            effect.setItemDef(itemDefRdb);
            effect.setEffectName(createdItemEffects.get(i).getItemEffectName());
            effect.setEffectDescription(createdItemEffects.get(i).getItemEffectDescription());
            effect.setEffectGradeDef(effectGradeDefRepository.findByEffectProbability(effectProbabilities.get(i)).get());
            rdbEffects.add(effect);
        }

        itemDefRdb.setItemEffects(rdbEffects);

        itemDefRepository.save(itemDefRdb);

        return itemDefMongo.getId();
    }

    @Transactional
    public ItemDTO createItemInWeb(String userId, ItemDefRequest request) {

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(
                () -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND)
        );

        InitItemData initItemData = new InitItemData(itemGradeDefRepository, effectGradeDefRepository);

        //fastAPI 통해서 아이템 생성
        ItemFastApiResponse response = createItemInit(request, initItemData);

        List<EffectProbability> effectProbabilities = initItemData.getEffectGrades();
        List<ItemFastApiResponse.ItemEffect> createdItemEffects = response.getItemEffects();

        //아이템 정보 RDBMS 매핑
        ItemDef itemDefRdb = new ItemDef();
        itemDefRdb.setName(response.getItemName());
        itemDefRdb.setDescription(response.getItemDescription());
        itemDefRdb.setItemGradeDef(itemGradeDefRepository.findByGrade(initItemData.getGrade()).get());
        itemDefRdb.setPicSrc("test link"); // Mongo 참조 대신 직접 값 지정
        itemDefRdb.setItemType(initItemData.getItemType());
        itemDefRdb.setBaseStat(initItemData.getBaseStat());
        itemDefRdb.setStrength(initItemData.getStats()[0]);
        itemDefRdb.setAgility(initItemData.getStats()[1]);
        itemDefRdb.setIntelligence(initItemData.getStats()[2]);
        itemDefRdb.setLuck(initItemData.getStats()[3]);
        itemDefRdb.setMainStat(initItemData.getMainStat());
        itemDefRdb.setPrice(initItemData.getItemPrice());
        itemDefRdb.setCreatedAt(LocalDateTime.now());

        List<ItemEffect> rdbEffects = new ArrayList<>();

        //아이템 효과 정보 RDBMS 매핑
        for (int i = 0; i < effectProbabilities.size(); i++) {
            ItemEffect effect = new ItemEffect();
            effect.setItemDef(itemDefRdb);
            effect.setEffectName(createdItemEffects.get(i).getItemEffectName());
            effect.setEffectDescription(createdItemEffects.get(i).getItemEffectDescription());
            effect.setEffectGradeDef(effectGradeDefRepository.findByEffectProbability(effectProbabilities.get(i)).get());
            ItemEffect savedItemEffect = itemEffectRepository.save(effect);
            rdbEffects.add(savedItemEffect);
        }
        itemDefRepository.save(itemDefRdb);

        UserItem userItem = new UserItem();
        userItem.setItemDef(itemDefRdb);
        userItem.setUser(user);
        userItem.setRemainingUses(initItemData.getRemainingUses());
        userItem.setTradeStatus(TradeStatus.OWNED);

        userItemRepository.save(userItem);


        List<ItemEffectDTO> effects = rdbEffects.stream()
                .map(e -> ItemEffectDTO.builder()
                        .effectProbability(e.getEffectGradeDef().getEffectProbability()) // enum/객체 그대로 매핑
                        .effectName(e.getEffectName())
                        .description(e.getEffectDescription())
                        .build())
                .toList();

        return ItemDTO.builder()
                .name(response.getItemName())
                .description(response.getItemDescription())
                .picSrc("test link")
                .itemType(initItemData.getItemType())
                .baseStat(initItemData.getBaseStat())
                .strength(initItemData.getStats()[0])
                .agility(initItemData.getStats()[1])
                .intelligence(initItemData.getStats()[2])
                .luck(initItemData.getStats()[3])
                .mainStat(initItemData.getMainStat())
                .grade(initItemData.getGrade())
                .itemEffects(effects)   // 리스트 주입
                .remainingUses(initItemData.getRemainingUses())
                .price(initItemData.getItemPrice())
                .build();

    }


}