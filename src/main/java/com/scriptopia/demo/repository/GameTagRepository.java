package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.GameTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameTagRepository extends JpaRepository<GameTag, Long> {
    @Query("select gt.tagDef.tagName " +
            "from GameTag gt " +
            "where gt.sharedGame.id = :sharedGameId")
    List<String> findTagNamesBySharedGameId(@Param("sharedGameId") Long sharedGameId);
}
