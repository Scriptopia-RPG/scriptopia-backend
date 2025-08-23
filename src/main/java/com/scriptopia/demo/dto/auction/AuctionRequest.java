package com.scriptopia.demo.dto.auction;


import com.scriptopia.demo.domain.TradeStatus;
import lombok.Data;

@Data
public class AuctionRequest {
    private String itemDefId; // 단수형으로 바꿔주세요
    private Long price;
}