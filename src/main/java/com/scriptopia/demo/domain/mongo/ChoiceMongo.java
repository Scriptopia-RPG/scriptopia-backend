package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.ChoiceResultType;
import com.scriptopia.demo.domain.Stat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceMongo {
    private String detail;
    private Stat stats; // strength, agility, intelligence, luck
    private Integer probability;
    private ChoiceResultType resultType; // battle, reward, shop, none
}