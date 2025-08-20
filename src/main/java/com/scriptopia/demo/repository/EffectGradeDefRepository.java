package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.EffectGradeDef;
import com.scriptopia.demo.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EffectGradeDefRepository extends JpaRepository<EffectGradeDef, Long> {
    Optional<EffectGradeDef> findByGrade(Grade grade);
}
