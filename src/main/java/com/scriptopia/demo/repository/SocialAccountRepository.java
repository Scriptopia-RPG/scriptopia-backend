package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.Provider;
import com.scriptopia.demo.domain.SharedGameScore;
import com.scriptopia.demo.domain.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findBySocialIdAndProvider(String id, Provider provider);
}
