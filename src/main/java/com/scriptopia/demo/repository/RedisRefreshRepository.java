package com.scriptopia.demo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scriptopia.demo.record.RefreshSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRefreshRepository implements RefreshRepository {

    private final StringRedisTemplate redis;
    private final ObjectMapper om;

    private static String kSession(long userId, String jti) { return "rt:%d:%s".formatted(userId, jti); }
    private static String kUserIdx(long userId) {return "rt:idx:u:%d".formatted(userId); }
    private static String kDeviceIdx(long userId, String deviceId) { return "rt:idx:u:%d:d:%s".formatted(userId, deviceId); }

    @Override
    public void save(RefreshSession s) {
        long now = Instant.now().getEpochSecond();
        long ttlSec = Math.max(1, s.expEpochSec() - now);

        String sessionKey = kSession(s.userId(), s.jti());
        String userIdxKey = kUserIdx(s.userId());

        try {
            String json = om.writeValueAsString(s);

            // 세션 저장 + TTL
            redis.opsForValue().set(sessionKey, json, Duration.ofSeconds(ttlSec));

            // 유저 인덱스(JTI 모음) 갱신
            redis.opsForSet().add(userIdxKey, s.jti());
            Long currentTtl = redis.getExpire(userIdxKey);
            if (currentTtl == null || currentTtl < ttlSec) {
                redis.expire(userIdxKey, Duration.ofSeconds(ttlSec));
            }

            // 디바이스 인덱스
            if (s.deviceId() != null) {
                String deviceKey = kDeviceIdx(s.userId(), s.deviceId());

                redis.opsForValue().set(deviceKey, s.jti(), Duration.ofSeconds(ttlSec));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save refresh session", e);
        }
    }

    @Override
    public Optional<RefreshSession> find(long userId, String jti) {
        String json = redis.opsForValue().get(kSession(userId, jti));
        if (json == null) return Optional.empty();
        try {
            return Optional.of(om.readValue(json, RefreshSession.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(long userId, String jti) {
        redis.delete(kSession(userId, jti));
        redis.opsForSet().remove(kUserIdx(userId), jti);

    }

    @Override
    public Optional<RefreshSession> findByDevice(long userId, String deviceId) {
        String jti = redis.opsForValue().get(kDeviceIdx(userId, deviceId));
        if (jti == null) return Optional.empty();
        return find(userId, jti);
    }

    @Override
    public void deleteByDevice(long userId, String deviceId) {
        String deviceKey = kDeviceIdx(userId, deviceId);
        String jti = redis.opsForValue().get(deviceKey);
        if (jti != null) {
            delete(userId, jti);
        }
        redis.delete(deviceKey);
    }

    @Override
    public void deleteAllForUser(long userId) {
        String userIdxKey = kUserIdx(userId);
        Set<String> jtIs = redis.opsForSet().members(userIdxKey);
        if (jtIs != null) {
            for (String jti : jtIs) {
                redis.delete(kSession(userId, jti));
            }
        }
        redis.delete(userIdxKey);
    }
}