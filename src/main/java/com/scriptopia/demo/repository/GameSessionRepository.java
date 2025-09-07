package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    Optional<GameSession> findByUserIdAndMongoId(Long userId, String mongoId);

    @Query("select g from GameSession g join g.user u where u.id = :userId")
    Optional<GameSession> findByMongoId(@Param("userId") Long userId);

    @Query("select case when (Count(g) > 0) then true else false end from GameSession g join g.user u where u.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}
