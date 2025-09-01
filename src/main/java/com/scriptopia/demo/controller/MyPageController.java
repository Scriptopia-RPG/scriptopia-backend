package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.history.HistoryPageResponse;
import com.scriptopia.demo.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final HistoryService historyService;

    @GetMapping("/user/my-page/history")
    public ResponseEntity<List<HistoryPageResponse>> getHistory(@RequestParam(required = false) Long lastId,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return historyService.fetchMyHisotry(userId, lastId, size);
    }
}
