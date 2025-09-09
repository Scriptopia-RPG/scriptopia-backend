package com.scriptopia.demo.dto.gamesession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameBattleRequest {

    private int turnCount;
    private String worldView;
    private String location;

    private String playerName;
    private String playerTrait;
    private int playerDmg;
    private Item playerWeapon;
    private Item playerArmor;
    private Item playerArtifact;

    private String npcName;
    private String npcTrait;
    private int npcDmg;
    private String npcWeapon;
    private String npcWeaponDescription;

    private int battleResult;
    private List<List<Integer>> hpLog;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String name;
        private String description;
        private List<ItemEffect> effects;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ItemEffect {
            private String name;
            private String description;
        }
    }
}
