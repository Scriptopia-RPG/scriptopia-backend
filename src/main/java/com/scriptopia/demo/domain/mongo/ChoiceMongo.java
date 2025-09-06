package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.ChoiceResultType;
import com.scriptopia.demo.domain.Stat;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceMongo {
    private String detail;
    private Stat stats; // STRENGTH, AGILITY, INTELLIGENCE, LUCK
    private Integer probability;
    private ChoiceResultType resultType; // BATTLE, SHOP, CHOICE, NONE
}