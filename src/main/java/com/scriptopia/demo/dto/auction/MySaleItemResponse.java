package com.scriptopia.demo.dto.auction;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySaleItemResponse {

    private List<MySaleItemResponseItem> content;
    private PageInfo pageInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int currentPage;
        private int pageSize;
    }

}
