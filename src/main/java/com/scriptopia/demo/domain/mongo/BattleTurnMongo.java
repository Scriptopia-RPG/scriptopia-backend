package com.scriptopia.demo.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BattleTurnMongo {
    private Integer turnId;
    private String turnInfo;
}