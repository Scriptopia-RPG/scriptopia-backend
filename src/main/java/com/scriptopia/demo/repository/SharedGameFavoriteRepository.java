package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.SharedGameFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharedGameFavoriteRepository extends JpaRepository<SharedGameFavorite, Long> {
    Optional<SharedGameFavorite> findByUserIdAndSharedGameId(Long userId, Long sharedGameId);
    boolean existsByUserIdAndSharedGameId(Long userId, Long sharedGameId);
    long countBySharedGameId(Long sharedGameId);
    void deleteByUserIdAndSharedGameId(Long userId, Long sharedGameId);
}
