package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.SharedGameFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SharedGameFavoriteRepository extends JpaRepository<SharedGameFavorite, Long> {
    Optional<SharedGameFavorite> findByUserIdAndSharedGameId(Long userId, Long sharedGameId);

    boolean existsByUserIdAndSharedGameId(Long userId, Long sharedGameId);

    long countBySharedGameId(Long sharedGameId);

    @Query("""
            Select case when Count(sgf) > 0 then true else false end from SharedGameFavorite sgf
            join sgf.user u join sgf.sharedGame sg where u.id = :userId and sg.id = :sharedGameId
            """)
    boolean existsLikeSharedGame(@Param("userId") Long userId, @Param("sharedGameId") Long sharedGameId);
}
