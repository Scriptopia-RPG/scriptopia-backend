package com.scriptopia.demo.dto;


import com.scriptopia.demo.domain.ItemDef;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Setter
@Getter
public class Settlement {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    private ItemDef itemDef;


    private TradeStatus tradeStatus;
    private long price;


    private LocalDateTime settledAt;
    private LocalDateTime createdAt;

}
