package com.scriptopia.demo.dto.piashop;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PiaItemRequest {
    private String name;
    private Long price;
    private String description;
}
