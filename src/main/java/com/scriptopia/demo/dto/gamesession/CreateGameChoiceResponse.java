package com.scriptopia.demo.dto.gamesession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameChoiceResponse {

    private ChoiceInfo choiceInfo;
    private NpcInfo npcInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChoiceInfo {
        private String story;
        private String title;
        private List<ChoiceOption> choice;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChoiceOption {
        private String detail;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NpcInfo {
        private String name;
        private Integer rank;
        private String trait;
        private String npcWeaponName;
        private String npcWeaponDescription;
    }
}
