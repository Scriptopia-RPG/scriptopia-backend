package com.scriptopia.demo.domain.mongo;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BattleTurnMongo {
    private Integer turnId;
    private String turnInfo;
}