package com.scriptopia.demo.domain.mongo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryItemMongo {
    private Long itemDefId;
    private LocalDateTime acquiredAt;
    private Boolean equipped;
    private String source;
}
