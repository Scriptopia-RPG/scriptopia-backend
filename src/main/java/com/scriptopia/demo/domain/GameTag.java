package com.scriptopia.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class GameTag {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private SharedGame sharedGame;

    @ManyToOne(fetch = FetchType.LAZY)
    private TagDef tagDef;
}
