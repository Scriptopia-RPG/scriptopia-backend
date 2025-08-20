package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.GameSession;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.dto.gamesession.GameSessionRequest;
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
    // User Service 리포 가져오기
    // 사용자 인증 부분 필요

    @Transactional
    public ResponseEntity<?> saveGameSession(GameSessionRequest gameSessionRequest) {
        // 토큰을 통한 사용자 인증
        GameSession gameSession = new GameSession();
        return ResponseEntity.ok(gameSessionRepository.save(gameSession));
    }

    @Transactional
    public ResponseEntity<?> updateGameSession(GameSessionRequest gameSessionRequest) {
        // 토큰을 통한 사용자 인증
        GameSession gameSession = new GameSession();
        return ResponseEntity.ok(gameSessionRepository.save(gameSession));
    }

    @Transactional
    public void deleteGameSession(GameSessionRequest gameSessionRequest) {
        // 토큰을 통한 사용자 인증
        // 게임 세션 삭제
    }
}
