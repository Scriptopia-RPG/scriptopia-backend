package com.scriptopia.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ItemDef {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemGradeDef itemGradeDef;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String picSrc;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    private Integer baseStat;
    private Integer strength;
    private Integer agility;
    private Integer intelligence;
    private Integer luck;

    @Enumerated(EnumType.STRING)
    private Stat stat;

    private LocalDateTime createdAt;

    private Long price;

    @OneToMany(mappedBy = "itemDef", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEffect> itemEffects = new ArrayList<>();

}