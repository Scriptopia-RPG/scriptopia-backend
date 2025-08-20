package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.ItemDef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDefRepository extends JpaRepository<ItemDef, Long> {
}
