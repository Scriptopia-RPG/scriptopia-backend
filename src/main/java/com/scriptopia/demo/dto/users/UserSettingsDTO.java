package com.scriptopia.demo.dto.users;

import com.scriptopia.demo.domain.FontType;
import com.scriptopia.demo.domain.Theme;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSettingsDTO {

    @NotNull(message = "E_400")
    private Theme theme;

    @NotNull(message = "E_400")
    @Min(value = 10, message = "E_400")
    @Max(value = 24, message = "E_400")
    private Integer fontSize;

    @NotNull(message = "E_400")
    private FontType font;

    @NotNull(message = "E_400")
    @Min(value = 1, message = "E_400")
    @Max(value = 10, message = "E_400")
    private Integer lineHeight;

    @NotNull(message = "E_400")
    @Min(value = 1, message = "E_400")
    @Max(value = 10, message = "E_400")
    private Integer wordSpacing;
}
