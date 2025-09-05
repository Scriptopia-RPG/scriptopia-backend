package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.*;
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
    public ItemDefResponse createItem(ItemDefRequest request) {
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
                .category(ItemType.getRandomItemType())
                .baseStat()
                .mainStat(Stat.getRandomMainStat())
                .grade(Grade.RARE)
                .itemEffect(List.of(Grade.COMMON, Grade.UNCOMMON))
                .strength(10)
                .agility(5)
                .intelligence(3)
                .luck(2)
                .price(1000L)
                .playerTrait("용맹함")
                .previousStory("고대의 유산에서 발견됨")
                .build();


    }


}