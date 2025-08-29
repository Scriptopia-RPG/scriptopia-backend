package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.history.HistoryPageResponse;
import com.scriptopia.demo.dto.history.HistoryResponse;
import com.scriptopia.demo.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserHistoryController {
    private final HistoryService historyService;

    @GetMapping("/history")
    public ResponseEntity<List<HistoryPageResponse>> getHistory(@RequestHeader("X-User-ID") Long userId,
                                                                @RequestParam(required = false) Long lastId,
                                                                @RequestParam(defaultValue = "10") int size) {
        return historyService.fetchMyHisotry(userId, lastId, size);
    }
}
