package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    boolean existsByUserItem(UserItem userItem);


    // itemName 우선 검색 (연관 테이블 조인)
    @Query("""
        SELECT DISTINCT a FROM Auction a
        JOIN FETCH a.userItem ui
        JOIN FETCH ui.user u
        JOIN FETCH ui.itemDef idf
        LEFT JOIN FETCH idf.itemEffects ie
        LEFT JOIN FETCH ie.effectGradeDef e
        WHERE LOWER(idf.name) LIKE LOWER(CONCAT('%', :itemName, '%'))
        ORDER BY a.createdAt DESC
        """)
    Page<Auction> findByItemName(@Param("itemName") String itemName, Pageable pageable);


    // 필터 조건 검색 (itemName 없는 경우)
    @Query("""
    SELECT DISTINCT a
    FROM Auction a
    JOIN a.userItem ui
    JOIN ui.itemDef id
    LEFT JOIN id.itemEffects ie
    WHERE (:category IS NULL OR id.itemType = :category)
      AND (:grade IS NULL OR id.itemGradeDef.grade = :grade)
      AND (:minPrice IS NULL OR a.price >= :minPrice)
      AND (:maxPrice IS NULL OR a.price <= :maxPrice)
      AND (:mainStat IS NULL OR id.mainStat = :mainStat)
      AND (
            :effectGrades IS NULL 
            OR EXISTS (
                SELECT 1 FROM ItemEffect ie2 
                WHERE ie2.itemDef = id 
                AND ie2.effectGradeDef.grade IN :effectGrades
            )
      )
""")
    Page<Auction> findByFilters(
            @Param("category") ItemType category,
            @Param("grade") Grade grade,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("mainStat") MainStat mainStat,
            @Param("effectGrades") List<Grade> effectGrades,
            Pageable pageable
    );



}