package com.scriptopia.demo.dto.gamesession.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InGameInventoryResponse {
        // 소유자 정보
        private String itemDefId;
        private LocalDateTime acquiredAt;
        private boolean equipped;
        private String source;

        // 아이템 정의 정보
        private String name;
        private String description;
        private String itemPicSrc;
        private String category;
        private int baseStat;
        private List<ItemEffect> itemEffects;
        private int strength;
        private int agility;
        private int intelligence;
        private int luck;
        private String mainStat;
        private String grade;
        private int price;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ItemEffect {
                private String itemEffectName;
                private String itemEffectDescription;
                private String grade;
        }
}
