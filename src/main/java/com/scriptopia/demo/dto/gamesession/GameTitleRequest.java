package com.scriptopia.demo.dto.gamesession;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameTitleRequest {
    private List<String> contents;
}
