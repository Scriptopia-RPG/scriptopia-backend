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
    계정관리 : 내 히스토리 조회 -> 무한스크롤
     */
    @GetMapping("/my-page/history")
    public ResponseEntity<List<HistoryPageResponse>> getHistory(@RequestParam(required = false) UUID lastId,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return historyService.fetchMyHistory(userId, lastId, size);
    }

    /*
    계정관리 : 내가 공유한 게임 조회
     */
    @GetMapping("/my-page/games/shared")
    public ResponseEntity<?> getMySharedGames(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.getMySharedGames(userId);
    }

    /*
    계정관리 : 내 히스토리 공유하기
     */
    @PostMapping("/my-page/share/{uuid}")
    public ResponseEntity<?> share(Authentication authentication, @PathVariable UUID uuid) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.saveSharedGame(userId, uuid);
    }

    /*
    계정관리 : 내가 공유한 게임 삭제
     */
    @DeleteMapping("/my-page/share/{uuid}")
    public ResponseEntity<?> delete(Authentication authentication, @PathVariable UUID uuid) {
        Long userId = Long.valueOf(authentication.getName());

        sharedGameService.deleteSharedGame(userId, uuid);

        return ResponseEntity.ok("게임이 삭제되었습니다.");
    }

    /*
    계정관리 : 게임 세션(이어하기) 조회
     */
    @GetMapping("/my-page/game")
    public ResponseEntity<?> loadGameSession(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return gameSessionService.getGameSession(userId);
    }

    /*
    계정관리 : 게임 세션(이어하기) 삭제
     */
    @DeleteMapping("/my-page/game/{gameId}")
    public ResponseEntity<?> deleteGameSession(Authentication authentication, @PathVariable String gameId) {
        Long userId = Long.valueOf(authentication.getName());

        return gameSessionService.deleteGameSession(userId, gameId);
    }
}
