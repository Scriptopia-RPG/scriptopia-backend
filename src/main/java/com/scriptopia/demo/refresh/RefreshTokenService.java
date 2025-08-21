package com.scriptopia.demo.refresh;

import com.scriptopia.demo.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;
    private final JwtProvider jwt;
    private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder

    public void saveLoginRefresh(Long userId, String refreshToken, String deviceId, String ip, String ua) {
        if (deviceId != null) {
            refreshRepository.deleteByDevice(userId, deviceId); // 단일/디바이스 정책
        }
        var jti = jwt.getJti(refreshToken);
        var exp = jwt.getExpiry(refreshToken).toInstant().getEpochSecond();

        var session = new RefreshSession(
                userId, jti,
                passwordEncoder.encode(refreshToken),
                deviceId, exp,
                Instant.now().getEpochSecond(),
                ip, ua
        );
        refreshRepository.save(session);
    }

    public TokenPair rotate(String refreshToken, String expectedDeviceId, List<String> roles) {
        var parsed = jwt.parse(refreshToken); // 유효하지 않으면 예외(JwtException)
        Long userId = Long.valueOf(parsed.getBody().getSubject());
        String jti = parsed.getBody().getId();
        String deviceInToken = (String) parsed.getBody().get("device");

        if (expectedDeviceId != null && !expectedDeviceId.equals(deviceInToken)) {
            throw new IllegalArgumentException("Device mismatch");
        }

        var saved = refreshRepository.find(userId, jti)
                .orElseThrow(() -> new IllegalArgumentException("Refresh not found"));

        // 소유자 확인
        if (!passwordEncoder.matches(refreshToken, saved.tokenHash())) {
            throw new IllegalArgumentException("Refresh hash mismatch");
        }

        // 기존 세션 삭제(재사용 차단)
        refreshRepository.delete(userId, jti);

        // 새 토큰 발급
        String newAccess  = jwt.createAccessToken(userId, roles);
        String newRefresh = jwt.createRefreshToken(userId, deviceInToken);

        // 새 세션 저장
        saveLoginRefresh(userId, newRefresh, deviceInToken, saved.ip(), saved.ua());

        return new TokenPair(newAccess, newRefresh);
    }

    public void logout(String refreshToken) {
        var parsed = jwt.parse(refreshToken);
        long userId = Long.parseLong(parsed.getBody().getSubject());
        String jti = parsed.getBody().getId();
        refreshRepository.delete(userId, jti);
    }

    public void logoutAll(Long userId) {
        refreshRepository.deleteAllForUser(userId);
    }

    public record TokenPair(String accessToken, String refreshToken) {}
}
