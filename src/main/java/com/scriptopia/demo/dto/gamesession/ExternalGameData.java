package com.scriptopia.demo.dto.gamesession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalGameData {

    private PlayerInfo player_info;
    private List<InventoryItem> inventory;
    private List<ItemDef> item_def;
    private String world_view;
    private String background_story;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerInfo {
        private String name;
        private int life;
        private int level;
        private int experience_point;
        private int combat_point;
        private int health_point;
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
        private int item_def_id;
        private String acquired_at;
        private boolean equipped;
        private String source;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDef {
        private int item_def_id;
        private String item_pic_src;
        private String name;
        private String description;
        private String category;
        private int base_stat;
        private List<ItemEffect> item_effect;
        private int strength;
        private int agility;
        private int intelligence;
        private int luck;
        private String main_stat;
        private int weight;
        private String grade;
        private int price;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ItemEffect {
            private String item_effect_name;
            private String item_effect_description;
            private String grade;
            private int item_effect_weight;
        }
    }
}
