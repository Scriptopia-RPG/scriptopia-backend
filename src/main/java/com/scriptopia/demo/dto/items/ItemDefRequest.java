package com.scriptopia.demo.dto.items;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.Stat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class ItemDefRequest {

    private String worldView;
    private String location;
    private ItemType category;
    private String playerTrait;
    private String previousStory;


}