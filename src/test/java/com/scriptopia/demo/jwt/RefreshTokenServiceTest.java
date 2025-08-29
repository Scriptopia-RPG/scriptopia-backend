package com.scriptopia.demo.jwt;

import com.scriptopia.demo.config.JwtProperties;
import com.scriptopia.demo.repository.RedisRefreshRepository;
import com.scriptopia.demo.repository.RefreshRepository;
import com.scriptopia.demo.service.RefreshTokenService;
import com.scriptopia.demo.utils.JwtKeyFactory;
import com.scriptopia.demo.utils.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RefreshTokenServiceTest {

    @Autowired StringRedisTemplate redis;
    @Autowired JwtProperties props;
    @Autowired PasswordEncoder passwordEncoder;

    JwtProvider jwt;
    RefreshTokenService refreshSvc;
    RefreshRepository refreshRepository;

    @BeforeEach
    void setup() {
        // 테스트마다 Redis DB 비우기
        var conn = redis.getConnectionFactory().getConnection();
        conn.serverCommands().flushDb();

        // 실제 구성요소 생성 (프로덕션 코드와 동일)
        var keyFactory = new JwtKeyFactory(props);
        this.jwt = new JwtProvider(props, keyFactory);
        this.refreshRepository = new RedisRefreshRepository(redis, new com.fasterxml.jackson.databind.ObjectMapper());
        this.refreshSvc = new RefreshTokenService(refreshRepository, jwt, passwordEncoder);
    }

    @Test
    void login_save_rotate_logout_flow() {
        long userId = 2L;
        String deviceId = "dev-win";
        List<String> roles = List.of("ROLE_USER");

        // 로그인: RT 발급 + 저장
        String rt1 = jwt.createRefreshToken(userId, deviceId);
        refreshSvc.saveLoginRefresh(userId, rt1, deviceId, "127.0.0.1", "JUnit");

        // 저장 확인
        var jti1 = jwt.getJti(rt1);
        assertThat(refreshRepository.find(userId, jti1)).isPresent();

        // 회전: 기존 RT로 새 AT/RT 발급 → 기존 세션 삭제
        var pair = refreshSvc.rotate(rt1, deviceId, roles);
        assertThat(pair.accessToken()).isNotBlank();
        assertThat(pair.refreshToken()).isNotBlank();

        // 기존 세션은 삭제확인
        assertThat(refreshRepository.find(userId, jti1)).isEmpty();

        // 새 세션 저장 확인
        var rt2 = pair.refreshToken();
        var jti2 = jwt.getJti(rt2);
        assertThat(refreshRepository.find(userId, jti2)).isPresent();

        assertThatThrownBy(() -> refreshSvc.rotate(rt1, deviceId, roles))
                .isInstanceOf(IllegalArgumentException.class);

        // 로그아웃: 현재 RT 삭제
        refreshSvc.logout(rt2);
        assertThat(refreshRepository.find(userId, jti2)).isEmpty();
    }
}
