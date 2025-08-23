package com.scriptopia.demo.dto.devlop;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDefResponse {
    private Long id;
    private String name;
    private String description;
    private String picSrc;
    private String itemType;       // enum 대신 String으로 전달
    private String mainStat;       // enum 대신 String
    private Integer baseStat;
    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer luck;
    private Long price;
    private LocalDateTime createdAt;

    private List<ItemEffectResponse> effects;
}