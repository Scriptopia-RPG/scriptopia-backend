package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.history.HistoryPageResponse;
import com.scriptopia.demo.service.GameSessionService;
import com.scriptopia.demo.service.HistoryService;
import com.scriptopia.demo.service.SharedGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final HistoryService historyService;
    private final SharedGameService sharedGameService;
    private final GameSessionService gameSessionService;


    /*
    게임 공유 -> 게임 공유하기
     */
    @PostMapping("/my-page/share/{uuid}")
    public ResponseEntity<?> share(Authentication authentication, @PathVariable UUID uuid) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.saveSharedGame(userId, uuid);
    }

    /*
    유저 -> 사용자 게임 기록 조회
     */
    @GetMapping("/my-page/history")
    public ResponseEntity<List<HistoryPageResponse>> getHistory(@RequestParam(required = false) UUID lastId,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return historyService.fetchMyHistory(userId, lastId, size);
    }

    /*
    게임 공유 -> 공유한 게임 조회(내가 공유한 게임 조회)
     */
    @GetMapping("/my-page/games/shared")
    public ResponseEntity<?> getMySharedGames(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.getMySharedGames(userId);
    }


    /*
    게임 공유 -> 공유한 게임 삭제
     */
    @DeleteMapping("/my-page/share/{uuid}")
    public ResponseEntity<?> delete(Authentication authentication, @PathVariable UUID uuid) {
        Long userId = Long.valueOf(authentication.getName());

        sharedGameService.deleteSharedGame(userId, uuid);

        return ResponseEntity.ok("게임이 삭제되었습니다.");
    }

    /*
    게임 -> 기존 게임 조회
     */
    @GetMapping("/my-page/game")
    public ResponseEntity<?> loadGameSession(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return gameSessionService.getGameSession(userId);
    }
}
