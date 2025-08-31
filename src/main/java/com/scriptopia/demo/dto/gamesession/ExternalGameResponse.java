package com.scriptopia.demo.dto.gamesession;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.MainStat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalGameResponse {
    private PlayerInfo playerInfo;
    private List<InventoryItem> inventory;
    private List<ItemDef> itemDef;
    private String worldView;
    private String backgroundStory;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerInfo {
        private String name;
        private int life;
        private int level;
        private int experiencePoint;
        private int combatPoint;
        private int healthPoint;
        private String trait;
        private int strength;
        private int agility;
        private int intelligence;
        private int luck;
        private int gold;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InventoryItem {
        private int itemDefId;
        private String acquiredAt;
        private boolean equipped;
        private String source;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDef {
        private Long itemDefId;
        private String itemPicSrc;
        private String name;
        private String description;
        private ItemType category;
        private int baseStat;
        private List<ItemEffect> itemEffect;
        private int strength;
        private int agility;
        private int intelligence;
        private int luck;
        private MainStat mainStat;
        private int weight;
        private Grade grade;
        private int price;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ItemEffect {
            private String itemEffectName;
            private String itemEffectDescription;
            private Grade grade;
            private int itemEffectWeight;
        }
    }
}
