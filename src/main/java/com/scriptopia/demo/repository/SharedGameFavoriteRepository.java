package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.SharedGameFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedGameFavoriteRepository extends JpaRepository<SharedGameFavorite, Long> {
}
