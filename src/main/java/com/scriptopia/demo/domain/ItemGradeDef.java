package com.scriptopia.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ItemGradeDef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double weight;

    @Enumerated(EnumType.STRING)
    private Grade grade;  // enum 필드 추가
}
