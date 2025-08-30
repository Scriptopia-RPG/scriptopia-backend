package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    Optional<GameSession> findBySessionId(String sessionId);

    Optional<GameSession> findByMongoId(String mongoId);

    List<GameSession> findAllByUserId(Long userId);
}
