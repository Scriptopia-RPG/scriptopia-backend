package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.gamesession.GameSessionRequest;
import com.scriptopia.demo.dto.gamesession.GameSessionResponse;
import com.scriptopia.demo.service.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game-session")
@RequiredArgsConstructor
public class GameSessionController {
    private final GameSessionService gameSessionService;

    @PostMapping
    public ResponseEntity<?> createGameSession(@RequestHeader("X-User-ID") Long id) {
        // 게임 세션 정보 저장
        return gameSessionService.saveGameSession(id);
    }

    // 정보 불러오기
    @GetMapping
    public ResponseEntity<?> loadGameSession(@RequestHeader("X-User-ID") Long id) {
        return gameSessionService.getGameSession(id);

    }

    // 수정
    @PutMapping
    public ResponseEntity<?> updateGameSession(@RequestHeader("X-User-ID") Long id) {
        return gameSessionService.updateGameSession(id);
    }

    // 삭제
    @DeleteMapping
    public void deleteGameSession(@RequestHeader("X-User-ID") Long id) {
        gameSessionService.deleteGameSession(id);
    }
}
