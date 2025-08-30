package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.LocalAccount;
import com.scriptopia.demo.domain.PiaItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PiaItemRepository extends JpaRepository<PiaItem, Long> {
    boolean existsByName(String name);  // 이름으로 중복 체크
}
