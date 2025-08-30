package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.SharedGameScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SharedGameScoreRepository extends JpaRepository<SharedGameScore, Long> {
    @Query("Select count(s) from SharedGameScore s where s.sharedGame.id = :sharedGameId")
    long countBySharedGameId(Long sharedGameId);

    @Query("select max(s.score) from SharedGameScore s where s.sharedGame.id = :sharedGameId")
    Long maxScoreBySharedGameId(Long sharedGameId);
}
