package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    Optional<GameSession> findByUser_IdAndMongoId(Long userId, String mongoId);

    List<GameSession> findAllByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);

    @Query("select g from GameSession g join g.user u where u.id = :userId")
    Optional<GameSession> findByMongoId(@Param("userId") Long userId);
}
