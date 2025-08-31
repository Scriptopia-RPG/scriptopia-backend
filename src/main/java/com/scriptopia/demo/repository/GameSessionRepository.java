package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    Optional<GameSession> findByUser_IdAndMongoId(Long userId, String mongoId);

    List<GameSession> findAllByUser_Id(Long userId);

    boolean existsByUserIdAndSceneTypeNotDone(Long userId);
}
