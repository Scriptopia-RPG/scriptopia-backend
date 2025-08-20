package com.scriptopia.demo.dto.auction;


import com.scriptopia.demo.domain.TradeStatus;
import lombok.Data;

@Data
public class AuctionRequestDto {
    private String itemDefsId;
    private TradeStatus tradeStatus; // ENUM이면 String으로 받아서 변환
    private Long price;
}