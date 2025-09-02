package com.scriptopia.demo.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "item_def")
public class NpcInfoMongo {
    private String name;
    private Integer rank;
    private String trait;
    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer luck;
    private String NpcWeaponName;
    private String NpcWeaponDescription;
}
