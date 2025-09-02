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
    private Stat stats; // strength, agility, intelligence, luck
    private Integer probability;
    private ChoiceResultType resultType; // battle, reward, shop, none
}