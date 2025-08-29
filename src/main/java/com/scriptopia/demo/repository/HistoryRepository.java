package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Integer> {

}
