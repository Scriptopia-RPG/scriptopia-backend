package com.scriptopia.demo.config;

import com.scriptopia.demo.domain.EffectGradeDef;
import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemGradeDef;
import com.scriptopia.demo.repository.EffectGradeDefRepository;
import com.scriptopia.demo.repository.ItemGradeDefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataLoaderConfig {

    private final ItemGradeDefRepository itemGradeDefRepository;
    private final EffectGradeDefRepository effectGradeDefRepository;

    @Bean
    public ApplicationRunner dataLoader() {
        return args -> {
            // ItemGradeDef 기본 데이터
            saveItemGradeIfNotExists(Grade.COMMON, 1.0, 100);
            saveItemGradeIfNotExists(Grade.UNCOMMON, 1.0, 150L);
            saveItemGradeIfNotExists(Grade.RARE, 1.0, 200L);
            saveItemGradeIfNotExists(Grade.EPIC, 1.0, 250L);
            saveItemGradeIfNotExists(Grade.LEGENDARY, 1.0, 300L);

            // EffectGradeDef 기본 데이터
            saveEffectGradeIfNotExists(Grade.COMMON, 10L, 0.1);
            saveEffectGradeIfNotExists(Grade.UNCOMMON, 20L, 0.15);
            saveEffectGradeIfNotExists(Grade.RARE, 50L, 0.2);
            saveEffectGradeIfNotExists(Grade.EPIC, 80L, 0.25);
            saveEffectGradeIfNotExists(Grade.LEGENDARY, 100L, 0.3);
        };
    }

    private void saveItemGradeIfNotExists(Grade grade, double weight, long price) {
        itemGradeDefRepository.findByGrade(grade).orElseGet(() -> {
            ItemGradeDef def = new ItemGradeDef();
            def.setGrade(grade);
            def.setWeight(weight);
            def.setPrice(price);
            return itemGradeDefRepository.save(def);
        });
    }

    private void saveEffectGradeIfNotExists(Grade grade, long price, double weight) {
        effectGradeDefRepository.findByGrade(grade).orElseGet(() -> {
            EffectGradeDef def = new EffectGradeDef();
            def.setGrade(grade);
            def.setPrice(price);
            def.setWeight(weight);
            return effectGradeDefRepository.save(def);
        });
    }
}