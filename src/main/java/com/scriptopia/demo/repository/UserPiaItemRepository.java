package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserPiaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPiaItemRepository extends JpaRepository<UserPiaItem, Long> {
    Optional<UserPiaItem> findByUserAndPiaItem(User user, PiaItem piaItem);

    List<UserPiaItem> findByUserId(Long UserId);
}
