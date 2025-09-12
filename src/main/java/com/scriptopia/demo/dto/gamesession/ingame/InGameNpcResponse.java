package com.scriptopia.demo.dto.gamesession.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InGameNpcResponse {
        private String name;
        private int rank;
        private String trait;
        private int strength;
        private int agility;
        private int intelligence;
        private int luck;
        private String npcWeaponName;
        private String npcWeaponDescription;

}
