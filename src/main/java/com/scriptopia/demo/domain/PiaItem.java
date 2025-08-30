package com.scriptopia.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class PiaItem {
    @Id @GeneratedValue
    private Long id;

    private String name;
    private Long price;
    private String description;
}
