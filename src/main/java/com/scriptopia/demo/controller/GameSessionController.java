package com.scriptopia.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scriptopia.demo.domain.mongo.GameSessionMongo;
import com.scriptopia.demo.domain.mongo.ItemDefMongo;
import com.scriptopia.demo.dto.gamesession.*;
import com.scriptopia.demo.service.GameSessionService;
import com.scriptopia.demo.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameSessionController {
    private final GameSessionService gameSessionService;
    private final HistoryService historyService;

    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping("/{gameId}/exit")
    public ResponseEntity<?> createGameSession(Authentication authentication, @PathVariable String gameId) {
        // 게임 세션 정보 저장
        Long userId = Long.valueOf(authentication.getName());

        return gameSessionService.saveGameSession(userId, gameId);
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    @DeleteMapping("/{gameId}")
    public ResponseEntity<?> deleteGameSession(Authentication authentication, @PathVariable String sessionId) {
        Long userId = Long.valueOf(authentication.getName());

        return gameSessionService.deleteGameSession(userId, sessionId);
    }
    
    
    // 게임 시작
    @PostMapping
    public ResponseEntity<StartGameResponse> startNewGame(
            @RequestBody StartGameRequest request,
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        StartGameResponse response = gameSessionService.startNewGame(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 테스트 중
     */
    @PostMapping("/test")
    public ResponseEntity<GameSessionMongo> testGame(
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        GameSessionMongo response = gameSessionService.mapToCreateGameChoiceRequest(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentGame(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());



    }

    /**
     * 현재는 userId, sessionId를 통해 저장하는데
     * 인증 관리 부분 끝나면 header에 token 꺼내오고 requestparameter session_id로 저장하게 수정
     */
    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping("/{gameId}/history")
    public ResponseEntity<?> addHistory(@PathVariable String gameId, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return historyService.createHistory(userId, gameId);
    }

    /** 개발용: 로컬 MongoDB에 더미 세션 한 건 심어서 테스트용 ObjectId 반환 */
    @PostMapping("/history/seed")
    public ResponseEntity<?> seed(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return historyService.seedDummySession(userId);
    }

}
