package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.domain.SharedGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SharedGameRepository extends JpaRepository<SharedGame, Long> {
    @Query("select sg from SharedGame sg where sg.user.id = :userId")
    List<SharedGame> findAllByUserid(@Param("userId") Long userId);

    @Query("select sg.id from SharedGame sg where sg.uuid = :uuid")
    Long findByUuid(@Param("uuid") String uuid);

    // 기본(전체)
    @Query("""
        select g from SharedGame g
        where (:lastId is null or g.id < :lastId)
        order by g.id desc
    """)
    Page<SharedGame> pageAll(@Param("lastId") Long lastId, Pageable pageable);

    // 🔎 검색 전용 (태그 무시)
    @Query("""
        select g from SharedGame g
        where (:lastId is null or g.id < :lastId)
          and (
            lower(g.title) like lower(concat('%', :q, '%'))
            or lower(g.worldView) like lower(concat('%', :q, '%'))
            or lower(g.backgroundStory) like lower(concat('%', :q, '%'))
          )
        order by g.id desc
    """)
    Page<SharedGame> pageSearchOnly(@Param("lastId") Long lastId,
                                    @Param("q") String q,
                                    Pageable pageable);


    // 🏷 태그 ALL 전용 (검색 없음)
    @Query("""
        select g from SharedGame g
        join GameTag gt on gt.sharedGame = g
        join TagDef td on td = gt.tagDef
        where (:lastId is null or g.id < :lastId)
          and td.id in :tagIds
        group by g.id
        having count(distinct td.id) = :tagCount
        order by g.id desc
    """)
    Page<SharedGame> pageByAllTagsOnly(@Param("lastId") Long lastId,
                                       @Param("tagIds") List<Long> tagIds,
                                       @Param("tagCount") long tagCount,
                                       Pageable pageable);
}