package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemGradeDef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemGradeDefRepository extends JpaRepository<ItemGradeDef, Long> {
    Optional<ItemGradeDef> findByGrade(Grade grade);

}
