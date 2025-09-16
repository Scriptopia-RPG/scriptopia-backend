package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.EffectGradeDef;
import com.scriptopia.demo.domain.EffectProbability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EffectGradeDefRepository extends JpaRepository<EffectGradeDef, Long> {
    Optional<EffectGradeDef> findByEffectProbability(EffectProbability effectProbability);

    @Query("SELECT egd.price FROM EffectGradeDef egd WHERE egd.effectProbability = :effectProbability")
    Optional<Long> findPriceByEffectProbability(@Param("effectProbability") EffectProbability effectProbability);

}
