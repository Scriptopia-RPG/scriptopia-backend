package com.scriptopia.demo.dto.items;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.Stat;
import lombok.Data;

import java.util.List;

@Data
public class ItemDefRequest {

    private String worldView;
    private String location;
    private ItemType category;
    private Stat baseStat;
    private Stat mainStat;
    private Grade grade;
    private List<Grade> itemEffect;
    private int strength;
    private int agility;
    private int intelligence;
    private int luck;
    private Long price;
    private String playerTrait;
    private String previousStory;

}