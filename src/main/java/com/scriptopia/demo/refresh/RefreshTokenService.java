package com.scriptopia.demo.refresh;

import com.scriptopia.demo.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;
    private final JwtProvider jwt;
    private final PasswordEncoder passwordEncoder;

    public void saveLoginRefresh(Long userId, String refreshToken, String deviceId, String ip, String ua) {
        if (deviceId != null) {
            refreshRepository.deleteByDevice(userId, deviceId);
        }
        var jti = jwt.getJti(refreshToken);
        var exp = jwt.getExpiry(refreshToken).toInstant().getEpochSecond();

        String rtHashInput = sha256Base64Url(refreshToken);

        var session = new RefreshSession(
                userId, jti,

                passwordEncoder.encode(rtHashInput),
                deviceId, exp,
                Instant.now().getEpochSecond(),
                ip, ua
        );
        refreshRepository.save(session);
    }

    public TokenPair rotate(String refreshToken, String expectedDeviceId, List<String> roles) {
        var parsed = jwt.parse(refreshToken);
        Long userId = Long.valueOf(parsed.getBody().getSubject());
        String jti = parsed.getBody().getId();
        String deviceInToken = (String) parsed.getBody().get("device");

        if (expectedDeviceId != null && !expectedDeviceId.equals(deviceInToken)) {
            throw new IllegalArgumentException("Device mismatch");
        }

        var saved = refreshRepository.find(userId, jti)
                .orElseThrow(() -> new IllegalArgumentException("Refresh not found"));


        String input = sha256Base64Url(refreshToken);
        if (!passwordEncoder.matches(input, saved.tokenHash())) {
            throw new IllegalArgumentException("Refresh hash mismatch");
        }


        refreshRepository.delete(userId, jti);


        String newAccess  = jwt.createAccessToken(userId, roles);
        String newRefresh = jwt.createRefreshToken(userId, deviceInToken);


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

    private static String sha256Base64Url(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }
}
