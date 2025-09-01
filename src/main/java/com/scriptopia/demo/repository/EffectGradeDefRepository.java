package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.EffectGradeDef;
import com.scriptopia.demo.domain.Grade;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EffectGradeDefRepository extends JpaRepository<EffectGradeDef, Long> {
    Optional<EffectGradeDef> findByGrade(Grade grade);

    @Query("SELECT egd.price FROM EffectGradeDef egd WHERE egd.grade = :grade")
    Integer findPriceByGrade(@Param("grade") Grade grade);
}
