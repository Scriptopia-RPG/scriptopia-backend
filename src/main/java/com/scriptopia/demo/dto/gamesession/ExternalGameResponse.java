package com.scriptopia.demo.dto.gamesession;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.Stat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalGameResponse {
    private PlayerInfo playerInfo;
    private List<ItemDef> itemDef;
    private String worldView;
    private String backgroundStory;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerInfo {
        private String name;
        private Stat startStat;
        private String trait;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDef {
        private String name;
        private String description;
        private List<ItemEffect> itemEffect;
        private Stat mainStat;
        private Grade grade;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ItemEffect {
            private String itemEffectName;
            private String itemEffectDescription;
            private Grade grade;
        }
    }
}
