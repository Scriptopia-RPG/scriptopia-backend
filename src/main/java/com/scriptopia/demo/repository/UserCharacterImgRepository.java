package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserCharacterImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCharacterImgRepository extends JpaRepository<UserCharacterImg, Long> {
}
