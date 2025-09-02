package com.scriptopia.demo.domain.mongo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfoMongo {
    private String name;
    private Integer life;
    private Integer level;
    private Integer healthPoint; // 난수
    private Integer experiencePoint;
    private String trait;
    private Integer strength; // 난수
    private Integer agility; // 난수
    private Integer intelligence;  // 난수
    private Integer luck; // 난수
    private Integer gold; // 난수
}
