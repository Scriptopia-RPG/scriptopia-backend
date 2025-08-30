package com.scriptopia.demo.dto.auction;

import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementHistoryResponse {
    private List<SettlementHistoryResponseItem> content;
    private PageInfo pageInfo;


    @Data
    public static class PageInfo {
        private int currentPage;
        private int pageSize;
    }
}
