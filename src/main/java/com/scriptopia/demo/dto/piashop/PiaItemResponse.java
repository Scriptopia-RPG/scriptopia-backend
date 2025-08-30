package com.scriptopia.demo.dto.piashop;

import com.scriptopia.demo.domain.PiaItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PiaItemResponse {
    private Long id;
    private String name;
    private Long price;
    private String description;


    public static PiaItemResponse fromEntity(PiaItem item) {
        return new PiaItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getDescription()
        );
    }

}
