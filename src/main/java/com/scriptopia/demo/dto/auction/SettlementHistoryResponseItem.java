package com.scriptopia.demo.dto.auction;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementHistoryResponseItem {
    private Long settlementId;
    private String itemName;
    private String itemType;
    private String itemGrade;
    private Long price;
    private String tradeType; // BUY / SELL
    private LocalDateTime settledAt;
}
