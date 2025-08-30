package com.scriptopia.demo.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySaleItemRequest {
    private Long pageIndex;
    private Long pageSize;
}
