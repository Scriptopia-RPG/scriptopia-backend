package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserCharacterImg;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserCharacterImgRepository extends JpaRepository<UserCharacterImg, Long> {
    Optional<UserCharacterImg> findByUserIdAndImgUrl(Long userId, String imgUrl);
    boolean existsByUserIdAndImgUrl(Long userId, String imgUrl);
    List<UserCharacterImg> findAllByUserId(Long userId);
}
