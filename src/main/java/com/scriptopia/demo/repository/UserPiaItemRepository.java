package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserPiaItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPiaItemRepository extends JpaRepository<UserPiaItem, Long> {
}
