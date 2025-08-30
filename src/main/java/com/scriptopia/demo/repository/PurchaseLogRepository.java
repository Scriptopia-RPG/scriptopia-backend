package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.PiaItemPurchaseLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseLogRepository extends JpaRepository<PiaItemPurchaseLog, Long> {
}
