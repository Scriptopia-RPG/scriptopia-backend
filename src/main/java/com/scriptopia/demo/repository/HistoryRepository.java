package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.History;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("select h from History h where h.uuid = :uuid")
    Optional<History> findByUuid(@Param("uuid") UUID uuid);

    @Query("select h.id from History h where h.user.id = : userId and h.uuid = :uuid")
    Optional<Long> findByUserIdAndUuid(@Param("userId") Long userId, @Param("uuid") UUID uuid);

    Page<History> findByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long lastId, Pageable pageable);

    Page<History> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}
