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
        private Integer rank;
        private String trait;
        private Integer strength;
        private Integer agility;
        private Integer intelligence;
        private Integer luck;
        private String npcWeaponName;
        private String npcWeaponDescription;

}
