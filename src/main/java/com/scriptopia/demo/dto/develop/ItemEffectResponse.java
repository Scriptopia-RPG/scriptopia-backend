package com.scriptopia.demo.dto.develop;

import lombok.Data;

@Data
public class ItemEffectResponse {
    private String effectName;
    private String effectDescription;
    private String grade;   // enum 대신 String
}