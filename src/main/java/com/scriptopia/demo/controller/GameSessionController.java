package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.gamesession.GameSessionRequest;
import com.scriptopia.demo.service.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameSessionController {
    private final GameSessionService gameSessionService;

    @PostMapping("/game-session")
    public ResponseEntity<?> createGameSession(@RequestBody GameSessionRequest gameSessionRequest) {
        // 게임 세션 정보 저장
        return gameSessionService.saveGameSession(gameSessionRequest);
    }

    // 정보 불러오기

    // 수정

    // 삭제
}
