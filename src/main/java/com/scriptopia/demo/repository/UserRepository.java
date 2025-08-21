package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.TagDef;
import com.scriptopia.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

}
