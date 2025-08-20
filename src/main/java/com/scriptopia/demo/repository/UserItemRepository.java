package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
}
