package com.scriptopia.demo.dto.history;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class HistoryPageResponseDto {
    private List<HistoryPageResponse> data;
    private UUID nextCursor;
    private boolean hasNext;
}
