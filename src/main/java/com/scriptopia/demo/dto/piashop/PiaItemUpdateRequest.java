package com.scriptopia.demo.dto.piashop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PiaItemUpdateRequest {
    private String name;      // 이름
    private Long price;       // 가격
    private String description; // 설명
}
