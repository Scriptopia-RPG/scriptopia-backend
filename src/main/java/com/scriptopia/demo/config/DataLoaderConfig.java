package com.scriptopia.demo.config;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DataLoaderConfig {

    private final EffectGradeDefRepository effectGradeDefRepository;
    private final ItemGradeDefRepository itemGradeDefRepository; // 추가

    @Bean
    public ApplicationRunner dataLoader() {
        return args -> {
            // EffectGradeDef 초기화
            Map<EffectProbability, Long> effectPriceMap = Map.of(
                    EffectProbability.COMMON, 10L,
                    EffectProbability.UNCOMMON, 30L,
                    EffectProbability.RARE, 50L,
                    EffectProbability.EPIC, 80L,
                    EffectProbability.LEGENDARY, 100L
            );

            for (EffectProbability prob : EffectProbability.values()) {
                if (prob == null) continue;
                if (effectGradeDefRepository.findByEffectProbability(prob).isEmpty()) {
                    EffectGradeDef def = new EffectGradeDef();
                    def.setEffectProbability(prob);
                    def.setPrice(effectPriceMap.get(prob));
                    def.setWeight(1.0);
                    effectGradeDefRepository.save(def);
                }
            }

            // ItemGradeDef 초기화
            Map<Grade, Long> itemGradePriceMap = Map.of(
                    Grade.COMMON, 10L,
                    Grade.UNCOMMON, 30L,
                    Grade.RARE, 50L,
                    Grade.EPIC, 80L,
                    Grade.LEGENDARY, 100L
            );

            for (Grade grade : Grade.values()) {
                if (grade == null) continue;
                if (itemGradeDefRepository.findByGrade(grade).isEmpty()) {
                    ItemGradeDef def = new ItemGradeDef();
                    def.setGrade(grade);
                    def.setPrice(itemGradePriceMap.get(grade));
                    def.setWeight(1.0);
                    itemGradeDefRepository.save(def);
                }
            }
        };
    }
}
