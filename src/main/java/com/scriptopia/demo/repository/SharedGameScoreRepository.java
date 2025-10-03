package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.SharedGameScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SharedGameScoreRepository extends JpaRepository<SharedGameScore, Long> {
    @Query("Select count(s) from SharedGameScore s where s.sharedGame.id = :sharedGameId")
    long countBySharedGameId(@Param("sharedGameId") Long sharedGameId);

    @Query("select coalesce(max(s.score), 0) from SharedGameScore s where s.sharedGame.id = :sharedGameId")
    Long maxScoreBySharedGameId(@Param("sharedGameId") Long sharedGameId);

    List<SharedGameScore> findAllBySharedGameIdOrderByScoreDescCreatedAtDesc(Long sharedGameId);
}
