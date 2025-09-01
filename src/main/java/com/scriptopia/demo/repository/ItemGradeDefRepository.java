package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemGradeDef;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemGradeDefRepository extends JpaRepository<ItemGradeDef, Long> {
    Optional<ItemGradeDef> findByGrade(Grade grade);

    @Query("SELECT igm.price FROM ItemGradeDef igm WHERE igm.grade = :grade")
    Integer findPriceByGrade(@Param("grade") Grade grade);


}
