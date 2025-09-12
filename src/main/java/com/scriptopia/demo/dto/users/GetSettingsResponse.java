package com.scriptopia.demo.dto.users;

import com.scriptopia.demo.domain.FontType;
import com.scriptopia.demo.domain.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSettingsResponse {
    private Theme theme;
    private Integer fondSize;
    private FontType font;
    private Integer lineHeight;
    private Integer wordSpacing;
}
