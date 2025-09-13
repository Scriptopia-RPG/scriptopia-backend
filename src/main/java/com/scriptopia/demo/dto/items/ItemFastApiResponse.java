package com.scriptopia.demo.dto.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemFastApiResponse {

    private String itemName;
    private String itemDescription;
    private List<ItemEffect> itemEffects;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemEffect {
        private String itemEffectName;
        private String itemEffectDescription;
    }
}