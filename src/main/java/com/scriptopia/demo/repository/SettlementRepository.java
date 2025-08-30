package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.domain.Settlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    // userId 기준으로 페이징 조회
    Page<Settlement> findByUserId(Long userId, Pageable pageable);
}
