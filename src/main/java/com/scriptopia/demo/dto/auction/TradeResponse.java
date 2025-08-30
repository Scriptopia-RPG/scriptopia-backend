// PagedAuctionResponse.java
package com.scriptopia.demo.dto.auction;

import lombok.Data;
import java.util.List;

@Data
public class TradeResponse {
    private List<AuctionItemResponse> content;
    private PageInfo pageInfo;

    @Data
    public static class PageInfo {
        private int currentPage;
        private int pageSize;
        private long totalItems;
        private int totalPages;
    }
}