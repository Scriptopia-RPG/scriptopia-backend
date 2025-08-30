package com.scriptopia.demo.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PiaItemPurchaseLog {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private PiaItem piaItem;

    private LocalDateTime purchaseDate;
    private Long price;

    @PrePersist
    public void prePersist() {
        this.purchaseDate = LocalDateTime.now();
    }

}
