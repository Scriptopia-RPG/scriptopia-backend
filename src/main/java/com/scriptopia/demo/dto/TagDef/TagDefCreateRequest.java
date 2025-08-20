package com.scriptopia.demo.dto.TagDef;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TagDefCreateRequest {
    private String tagName;
}
