package com.scriptopia.demo.dto.items;

import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.Stat;
import lombok.Data;

import java.util.List;

@Data
public class ItemDefRequest {

    private String worldView;
    private String name;
    private String description;

    // 🔹 아이템 효과 리스트
    private List<ItemEffectRequest> effects;

}