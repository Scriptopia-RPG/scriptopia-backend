package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {

    Page<History> findByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long lastId, Pageable pageable);

    Page<History> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}
