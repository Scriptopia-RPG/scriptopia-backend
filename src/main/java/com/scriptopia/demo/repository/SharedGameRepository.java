package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.domain.SharedGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedGameRepository extends JpaRepository<SharedGame, Long> {
}
