package com.scriptopia.demo.dto.auction;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.domain.MainStat;
import lombok.Data;
import java.util.List;

@Data
public class TradeFilterRequest {

    private Long pageIndex;   // 0부터 시작
    private Long pageSize;    // 한 페이지당 아이템 수
    private String itemName;           // 판매 아이템 이름
    private ItemType category;         // 아이템 분류 (WEAPON, ARMOR, ARTIFACT, POTION)
    private Long minPrice;             // 최소 가격 (nullable)
    private Long maxPrice;             // 최대 가격 (nullable)
    private Grade grade;               // 아이템 등급 (nullable)
    private List<Grade> effectGrades;  // 아이템 효과 등급 필터 (nullable)
    private MainStat mainStat;         // 주 스탯 (nullable)
}