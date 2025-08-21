package com.scriptopia.demo.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.scriptopia.demo.domain.LocalAccount;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocalAccountRepository extends JpaRepository<LocalAccount, Long> {
    Optional<LocalAccount> findByEmail(String email);

}

