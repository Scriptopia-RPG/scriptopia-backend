package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.SharedGameScore;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SharedGameScoreRepository extends JpaRepository<SharedGameScore, Long> {
    @Query("Select count(s) from SharedGameScore s where s.sharedGame.id = :sharedGameId")
    long countBySharedGameId(Long sharedGameId);

    @Query("select coalesce(max(s.score), 0) from SharedGameScore s where s.sharedGame.id = :sharedGameId")
    Long maxScoreBySharedGameId(@Param("sharedGameId") Long sharedGameId);

    List<SharedGameScore> findAllBySharedGameIdOrderByScoreDescCreatedAtDesc(Long sharedGameId);
}
