package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.LocalAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalAccountRepository extends JpaRepository<LocalAccount, Long> {
}
