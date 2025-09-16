package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SocialAccount;
import com.scriptopia.demo.domain.TagDef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagDefRepository extends JpaRepository<TagDef, Long> {
    boolean existsByTagName(String tagName);

    Optional<TagDef> findByTagName(String tagName);
}
