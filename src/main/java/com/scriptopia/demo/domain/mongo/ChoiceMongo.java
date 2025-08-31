package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.ChoiceResultType;
import com.scriptopia.demo.domain.MainStat;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChoiceMongo {
    private String detail;
    private MainStat stats; // strength, agility, intelligence, luck
    private Integer probability;
    private ChoiceResultType resultType; // battle, reward, shop, none
}