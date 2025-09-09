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

    private Player player;
    private Npc npc;

    private List<List<Integer>> hpLog;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Player {
        private String name;
        private String trait;
        private int dmg;
        private String weapon;
        private String armor;
        private String artifact;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Npc {
        private String name;
        private String trait;
        private int dmg;
        private String weapon;
    }
}
