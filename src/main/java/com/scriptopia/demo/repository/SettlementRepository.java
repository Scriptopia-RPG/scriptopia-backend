package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}
