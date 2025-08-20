package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.SharedGameScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedGameScoreRepository extends JpaRepository<SharedGameScore, Long> {
}
