package com.scriptopia.demo.dto.piashop;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasePiaItemRequest {
    private String itemId;
    private int quantity;
}
