package com.scriptopia.demo.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TagDef {
    @Id
    @GeneratedValue
    private Long id;

    private String tagName;
}
