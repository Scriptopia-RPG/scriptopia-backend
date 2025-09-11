package com.scriptopia.demo.dto.gamesession;

import com.scriptopia.demo.domain.ChoiceEventType;
import com.scriptopia.demo.domain.Stat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameChoiceRequest {
    private String worldView;
    private String currentStory;
    private String location;
    private String currentChoice;
    private List<Stat> choiceStat;
    private ChoiceEventType eventType;
    private Integer npcRank;
    private PlayerInfo playerInfo;
    private List<ItemInfo> itemInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerInfo {
        private String name;
        private String trait;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemInfo {
        private String name;
        private String description;
    }
}
