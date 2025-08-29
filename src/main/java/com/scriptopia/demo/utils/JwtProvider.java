package com.scriptopia.demo.utils;

import com.scriptopia.demo.config.JwtProperties;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties props;
    private final JwtKeyFactory keyFactory;

    private Key signingKey() {
        return keyFactory.hmacKey();
    }

    public String createAccessToken(Long userId, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(props.issuer())
                .setSubject(String.valueOf(userId)) // userId를 subject로
                .claim("roles", roles)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(props.accessExpSeconds())))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId, String deviceId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(props.issuer())
                .setSubject(String.valueOf(userId))
                .claim("device",deviceId)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(props.refreshExpSeconds())))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token);
    }

    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getBody().getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return (List<String>) parse(token).getBody().get("roles");
    }

    public String getDeviceId(String token) {
        Object v = parse(token).getBody().get("device");
        return v == null ? null : v.toString();
    }

    public String getJti(String token) {
        return parse(token).getBody().getId();
    }

    public Date getExpiry(String token) {
        return parse(token).getBody().getExpiration();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmail(String token) {
        return parse(token).getBody().getSubject();
    }

}
