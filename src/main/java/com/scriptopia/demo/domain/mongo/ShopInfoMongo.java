package com.scriptopia.demo.domain.mongo;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopInfoMongo {
    private List<Long> itemDefId;
}
