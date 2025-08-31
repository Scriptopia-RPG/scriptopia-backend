package com.scriptopia.demo.domain.mongo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChoiceMongo {
    private String detail;
    private String stats; // strength, agility, intelligence, luck
    private Integer probability;
    private String resultType; // battle, reward, shop, none
}