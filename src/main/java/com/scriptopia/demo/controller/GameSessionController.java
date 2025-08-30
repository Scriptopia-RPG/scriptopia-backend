package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.gamesession.GameSessionRequest;
import com.scriptopia.demo.dto.gamesession.GameSessionResponse;
import com.scriptopia.demo.service.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/games")
@RequiredArgsConstructor
public class GameSessionController {
    private final GameSessionService gameSessionService;

    @PostMapping("/{sessionId}/exit")
    public ResponseEntity<?> createGameSession(Authentication authentication, @PathVariable String sessionId) {
        // 게임 세션 정보 저장
        Long userId = Long.valueOf(authentication.getName());

        return gameSessionService.saveGameSession(userId, sessionId);
    }

    // 정보 불러오기
    @GetMapping
    public ResponseEntity<?> loadGameSession(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return gameSessionService.getGameSession(userId);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> deleteGameSession(Authentication authentication, @PathVariable String sessionId) {
        Long userId = Long.valueOf(authentication.getName());

        return gameSessionService.deleteGameSession(userId, sessionId);
    }
}
