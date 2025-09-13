package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

    Optional<UserSetting> findByUserId(Long userId);
}
