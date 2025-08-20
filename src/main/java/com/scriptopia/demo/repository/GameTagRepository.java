package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.GameTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameTagRepository extends JpaRepository<GameTag, Long> {
}
