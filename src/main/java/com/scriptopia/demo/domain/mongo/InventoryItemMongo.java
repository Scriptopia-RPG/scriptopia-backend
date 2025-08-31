package com.scriptopia.demo.domain.mongo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemMongo {
    private Long itemDefId;
    private LocalDateTime acquiredAt;
    private Boolean equipped;
    private String source;
}
