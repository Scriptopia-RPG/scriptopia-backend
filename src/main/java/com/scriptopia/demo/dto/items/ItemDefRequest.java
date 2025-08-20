package com.scriptopia.demo.dto.items;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.MainStat;
import lombok.Data;

import java.util.List;

@Data
public class ItemDefRequest {

    private String name;
    private String description;
    private String picSrc;

    private ItemType itemType;
    private MainStat mainStat;

    private Integer baseStat;
    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer luck;

    private Long itemGradeDefId;
    private Long price;

    // 🔹 아이템 효과 리스트
    private List<ItemEffectRequest> effects;

}