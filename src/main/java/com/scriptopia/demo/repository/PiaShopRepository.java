package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.PiaItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PiaShopRepository extends JpaRepository<PiaItem, Long> {

}

