package com.scriptopia.demo.dto.auction;

import com.scriptopia.demo.domain.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionItemResponse {

    private Long auctionId;
    private Long price;
    private LocalDateTime createdAt;

    private UserDto seller;
    private ItemDto item;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Long userId;
        private String nickname;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {
        private Long userItemId;
        private Long itemDefId;
        private String name;
        private String description;
        private String picSrc;
        private int remainingUses;
        private TradeStatus tradeStatus;
        private String grade;
        private int baseStat;
        private int strength;
        private int agility;
        private int intelligence;
        private int luck;
        private List<ItemEffectDto> effects;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemEffectDto {
        private String effectName;
        private String effectDescription;
        private String grade;
    }
}