package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.SharedGameScore;
import com.scriptopia.demo.domain.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
}
