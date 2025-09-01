package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.history.HistoryPageResponse;
import com.scriptopia.demo.service.HistoryService;
import com.scriptopia.demo.service.SharedGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final HistoryService historyService;
    private final SharedGameService sharedGameService;

    @GetMapping("/user/my-page/history")
    public ResponseEntity<List<HistoryPageResponse>> getHistory(@RequestParam(required = false) Long lastId,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return historyService.fetchMyHisotry(userId, lastId, size);
    }

    @GetMapping("/user/my-page/games/shared")
    public ResponseEntity<?> getMySharedGames(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.getMySharedGames(userId);
    }

    @PostMapping("/user/my-page/share/{hid}")
    public ResponseEntity<?> share(Authentication authentication, @PathVariable Long hid) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.saveSharedGame(userId, hid);
    }

    @DeleteMapping("/user/my-page/share/{gameid}")
    public ResponseEntity<?> delete(Authentication authentication, @PathVariable Long gameid) {
        Long userId = Long.valueOf(authentication.getName());

        sharedGameService.deletesharedGame(userId, gameid);

        return ResponseEntity.ok("게임이 삭제되었습니다.");
    }
}
