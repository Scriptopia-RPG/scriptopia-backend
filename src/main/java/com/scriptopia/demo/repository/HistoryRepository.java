package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.History;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("select h from History h where h.uuid = :uuid")
    Optional<History> findByUuid(@Param("uuid") UUID uuid);

    @Query("""
        SELECT h FROM History h
        WHERE h.user.id = :userId
        AND (:cursor IS NULL OR h.createdAt < (SELECT h2.createdAt FROM History h2 WHERE h2.uuid = :cursor))
        ORDER BY h.createdAt DESC
        """)
    List<History> findHistoriesByUserWithCursor(
            @Param("userId") Long userId,
            @Param("cursor") UUID cursor,
            Pageable pageable
    );
}
