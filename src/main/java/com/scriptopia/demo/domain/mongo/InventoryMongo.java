package com.scriptopia.demo.domain.mongo;

import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMongo {
    @Id
    private String id;

    private String itemDefId;
    private LocalDateTime acquiredAt;
    private Boolean equipped;
    private String source;

    public boolean isEquipped() {
        return this.equipped;
    }
}
