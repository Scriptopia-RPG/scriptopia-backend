package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.EffectGradeDef;
import com.scriptopia.demo.domain.EffectProbability;
import com.scriptopia.demo.domain.Grade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EffectGradeDefRepository extends JpaRepository<EffectGradeDef, Long> {
    Optional<EffectGradeDef> findByGrade(Grade grade);

    @Query("SELECT egd.price FROM EffectGradeDef egd WHERE egd.grade = :grade")
    Optional<Long> findPriceByGrade(@Param("grade") Grade grade);

}
