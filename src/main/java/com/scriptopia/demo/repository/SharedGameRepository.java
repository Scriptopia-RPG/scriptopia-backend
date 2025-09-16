package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.domain.SharedGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SharedGameRepository extends JpaRepository<SharedGame, Long> {
    @Query("select sg from SharedGame sg where sg.user.id = :userId")
    List<SharedGame> findAllByUserid(@Param("userId") Long userId);

    @Query("select sg from SharedGame sg where sg.uuid = :uuid")
    Optional<SharedGame> findByUuid(@Param("uuid") UUID uuid);

    @Query("select g.sharedAt from SharedGame g where g.id = :id")
    LocalDateTime findSharedAtById(@Param("id") Long id);

    // 최신순
    @Query("""
    select g
    from SharedGame g
    where (:tagEmpty = true
           or exists (select 1 from GameTag gt where gt.sharedGame = g and gt.tagDef.id in :tagIds))
      and (:qBlank = true
           or lower(coalesce(g.title,''))           like :qLike
           or lower(coalesce(g.worldView,''))       like :qLike
           or lower(coalesce(g.backgroundStory,'')) like :qLike)
      and (
           :useCursor = false
           or g.sharedAt < :lastKey
           or (g.sharedAt = :lastKey and g.id < :lastId)
      )
    order by g.sharedAt desc, g.id desc
    """)
    List<SharedGame> sliceLatest(
            @Param("tagIds") List<Long> tagIds, @Param("tagEmpty") boolean tagEmpty,
            @Param("qLike") String qLike, @Param("qBlank") boolean qBlank,
            @Param("useCursor") boolean useCursor,
            @Param("lastKey") LocalDateTime lastKey, @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("""
    select g
    from SharedGame g
    where (:tagEmpty = true
           or exists (select 1 from GameTag gt where gt.sharedGame = g and gt.tagDef.id in :tagIds))
      and (:qBlank = true
           or lower(coalesce(g.title,'')) like :qLike
           or lower(coalesce(g.worldView,'')) like :qLike
           or lower(coalesce(g.backgroundStory,'')) like :qLike)
      and (
           :useCursor = false
           or (
               (select count(s.id) from SharedGameScore s where s.sharedGame = g) < :lastKey
               or (
                   (select count(s2.id) from SharedGameScore s2 where s2.sharedGame = g) = :lastKey
                   and g.id < :lastId
               )
           )
      )
    order by (select count(s3.id) from SharedGameScore s3 where s3.sharedGame = g) desc,
             g.id desc
    """)
    List<SharedGame> slicePopular(
            @Param("tagIds") List<Long> tagIds, @Param("tagEmpty") boolean tagEmpty,
            @Param("qLike") String qLike, @Param("qBlank") boolean qBlank,
            @Param("useCursor") boolean useCursor,
            @Param("lastKey") Long lastKey, @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("""
    select g
    from SharedGame g
    where (:tagEmpty = true
           or exists (select 1 from GameTag gt where gt.sharedGame = g and gt.tagDef.id in :tagIds))
      and (:qBlank = true
           or lower(coalesce(g.title,'')) like :qLike
           or lower(coalesce(g.worldView,'')) like :qLike
           or lower(coalesce(g.backgroundStory,'')) like :qLike)
      and (
           :useCursor = false
           or (
               (select coalesce(max(s.score),0) from SharedGameScore s where s.sharedGame = g) < :lastKey
               or (
                   (select coalesce(max(s2.score),0) from SharedGameScore s2 where s2.sharedGame = g) = :lastKey
                   and g.id < :lastId
               )
           )
      )
    order by (select coalesce(max(s3.score),0) from SharedGameScore s3 where s3.sharedGame = g) desc,
             g.id desc
    """)
    List<SharedGame> sliceTopScore(
            @Param("tagIds") List<Long> tagIds, @Param("tagEmpty") boolean tagEmpty,
            @Param("qLike") String qLike, @Param("qBlank") boolean qBlank,
            @Param("useCursor") boolean useCursor,
            @Param("lastKey") Long lastKey, @Param("lastId") Long lastId,
            Pageable pageable
    );

}