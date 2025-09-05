package com.scriptopia.demo.dto.items;

import com.scriptopia.demo.domain.ItemType;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ItemFastApiRequest {

    private String worldView;
    private String location;
    private ItemType category;
    private String playerTrait;
    private String previousStory;

}
