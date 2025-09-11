package com.scriptopia.demo.config;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.repository.LocalAccountRepository;
import com.scriptopia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final LocalAccountRepository localAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;




    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByNickname("admin")) {

            User admin = new User();
            admin.setPia(999999L);
            admin.setNickname("admin");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setLastLoginAt(LocalDateTime.now());
            admin.setProfileImgUrl(null);
            admin.setRole(Role.ADMIN);
            admin.setLoginType(LoginType.LOCAL);

            User adminUser = userRepository.save(admin);


            LocalAccount localAccount = new LocalAccount();
            localAccount.setUser(adminUser);
            localAccount.setEmail(adminUsername);
            localAccount.setPassword(passwordEncoder.encode(adminPassword));
            localAccount.setUpdatedAt(LocalDateTime.now());
            localAccount.setStatus(UserStatus.VERIFIED);

            LocalAccount adminAccount = localAccountRepository.save(localAccount);


        }
    }
}