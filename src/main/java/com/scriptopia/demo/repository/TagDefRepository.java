package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SocialAccount;
import com.scriptopia.demo.domain.TagDef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagDefRepository extends JpaRepository<TagDef, Long> {
    boolean existsByTagName(String tagName);
}
