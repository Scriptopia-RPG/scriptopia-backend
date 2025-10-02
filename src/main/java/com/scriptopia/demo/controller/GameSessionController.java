package com.scriptopia.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scriptopia.demo.domain.GameSession;
import com.scriptopia.demo.domain.mongo.GameSessionMongo;
import com.scriptopia.demo.dto.gamesession.*;
import com.scriptopia.demo.service.GameSessionService;
import com.scriptopia.demo.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
@Tag(name = "게임 세션 API", description = "게임 세션 관련 API 입니다.")
@RequiredArgsConstructor
public class GameSessionController {

    private final GameSessionService gameSessionService;
    private final HistoryService historyService;

    /*
     * 게임 -> 게임 도중 종료
     */
    @Operation(summary = "저장 후 게임 종료")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/exit")
    public ResponseEntity<?> createGameSession(Authentication authentication, @RequestBody GameSessionRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        return gameSessionService.saveGameSession(userId, request.getGameId());
    }

    /*
     * 게임 -> 기존 게임 삭제
     */
    @Operation(summary = "기존 게임 삭제")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping()
    public ResponseEntity<?> deleteGameSession(Authentication authentication, @RequestBody GameSessionRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        return gameSessionService.deleteGameSession(userId, request.getGameId());
    }

    /*
     * 게임 -> 기존 게임 조회
     */
    @Operation(summary = "기존 게임 조회")
    @GetMapping("/me")
    public ResponseEntity<?> loadGameSession(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return gameSessionService.getGameSession(userId);
    }

    // 게임 시작
    @Operation(summary = "새 게임 생성")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<StartGameResponse> startNewGame(
            @RequestBody StartGameRequest request,
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        StartGameResponse response = gameSessionService.startNewGame(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게임 진입")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/{gameId}")
    public ResponseEntity<?> getInGameData(
            @PathVariable("gameId") String gameId,
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        // service에서 sceneType별 DTO를 반환
        Object response = gameSessionService.getInGameDataDto(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "선택지 선택")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/{gameId}/select")
    public ResponseEntity<GameSessionMongo> selectChoice(
            @PathVariable("gameId") String gameId,
            @RequestBody GameChoiceRequest request,
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        GameSessionMongo response = gameSessionService.gameChoiceSelect(userId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게임 진행")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/{gameId}/progress")
    public ResponseEntity<GameSessionMongo> keepGame(
            @PathVariable("gameId") String gameId,
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        GameSessionMongo response = gameSessionService.gameProgress(userId);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "아이템 장착/해제")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/equipItem/{gameId}/{itemId}")
    public ResponseEntity<GameSessionMongo> equipItem(
            @PathVariable("gameId") String gameId,
            @PathVariable("itemId") String itemId,
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        GameSessionMongo response = gameSessionService.gameEquipItem(userId, itemId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "아이템 버리기")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @DeleteMapping("/dropItem/{gameId}/{itemId}")
    public ResponseEntity<GameSessionMongo> dropItem(
            @PathVariable("gameId") String gameId,
            @PathVariable("itemId") String itemId,
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        GameSessionMongo response = gameSessionService.gameDropItem(userId, itemId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "아이템 구매")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/{gameId}/items/purchase/{itemId}")
    public ResponseEntity<GameSessionMongo> buyItem(
            @PathVariable("gameId") String gameId,
            @PathVariable("itemId") String itemId,
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        GameSessionMongo response = gameSessionService.gameBuyItem(userId, itemId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "아이템 판매")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/{gameId}/items/sell/{itemId}")
    public ResponseEntity<GameSessionMongo> sellItem(
            @PathVariable("gameId") String gameId,
            @PathVariable("itemId") String itemId,
            Authentication authentication) throws JsonProcessingException {

        Long userId = Long.valueOf(authentication.getName());

        GameSessionMongo response = gameSessionService.gameSellItem(userId, itemId);

        return ResponseEntity.ok(response);
    }

    /**
     * 현재는 userId, sessionId를 통해 저장하는데
     * 인증 관리 부분 끝나면 header에 token 꺼내오고 requestparameter session_id로 저장하게 수정
     */
    /*
     * 게임 -> 히스토리 생성
     */
    @Operation(summary = "")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/history")
    public ResponseEntity<?> addHistory(Authentication authentication, @RequestBody GameSessionRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        return historyService.createHistory(userId, request.getGameId());
    }

    /** 개발용: 로컬 MongoDB에 더미 세션 한 건 심어서 테스트용 ObjectId 반환 */
    @Operation(summary = "")
    @PostMapping("/history/seed")
    public ResponseEntity<?> seed(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return historyService.seedDummySession(userId);
    }
}
