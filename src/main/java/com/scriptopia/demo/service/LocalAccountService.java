package com.scriptopia.demo.service;

import com.scriptopia.demo.repository.LocalAccountRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LocalAccountService {

    private final LocalAccountRepository localAccountRepository;

    /** 이메일로 사용자 + 패스워드 해시 + 롤 조회 */
    public Optional<AccountInfo> loadByEmail(String email) {
        return localAccountRepository.findByEmail(email).map(a ->
                new AccountInfo(
                        a.getId(),
                        a.getEmail(),
                        a.getPassword(),          // ← DB에 저장된 해시(BCrypt)
                        List.of("ROLE_USER")      // ← 현재는 기본 롤로 처리
                )
        );
    }

    public List<String> getRoles(Long userId) {
        return List.of("ROLE_USER");
    }

    @Getter
    @AllArgsConstructor
    public static class AccountInfo {
        private Long id;
        private String email;
        private String passwordHash;
        private List<String> roles;
    }
}
