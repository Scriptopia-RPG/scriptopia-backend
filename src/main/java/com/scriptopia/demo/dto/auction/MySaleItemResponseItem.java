package com.scriptopia.demo.dto.auction;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySaleItemResponseItem {
    private Long auctionId;
    private Long price;
    private LocalDateTime createdAt;
    private ItemDto item;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {
        private Long itemDefId;
        private String name;
        private String itemGrade;
        private String itemType;
        private String mainStat;
        private String picSrc;
    }
}
