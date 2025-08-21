package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.GameSession;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.dto.gamesession.GameSessionRequest;
import com.scriptopia.demo.dto.gamesession.GameSessionResponse;
import com.scriptopia.demo.repository.GameSessionRepository;
import com.scriptopia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    // TODO User Service 리포 가져오기
    // TODO 사용자 인증 부분 필요

    public ResponseEntity<GameSessionResponse> getGameSession(Long id) {
        // TODO 토큰을 통한 사용자 인증 구현
        GameSessionResponse gameSessionResponse = new GameSessionResponse();
        return ResponseEntity.ok(gameSessionResponse);
    }

    @Transactional
    public ResponseEntity<?> saveGameSession(Long id) {
        // TODO 토큰을 통한 사용자 인증
        GameSession gameSession = new GameSession();
        return ResponseEntity.ok(gameSessionRepository.save(gameSession));
    }

    @Transactional
    public ResponseEntity<?> updateGameSession(Long id) {
        // TODO 토큰을 통한 사용자 인증
        GameSession gameSession = new GameSession();
        return ResponseEntity.ok(gameSessionRepository.save(gameSession));
    }

    @Transactional
    public void deleteGameSession(Long id) {
        // TODO 토큰을 통한 사용자 인증
        GameSession gameSession = new GameSession();
        gameSessionRepository.delete(gameSession);
    }
}
