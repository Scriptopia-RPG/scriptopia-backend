package com.scriptopia.demo.domain;

import com.scriptopia.demo.dto.ItemGradeDef;
import com.scriptopia.demo.dto.ItemType;
import com.scriptopia.demo.dto.MainStat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private MainStat mainStat;

    private LocalDateTime createdAt;
}
