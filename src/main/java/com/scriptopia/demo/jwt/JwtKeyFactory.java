package com.scriptopia.demo.jwt;

import com.scriptopia.demo.config.JwtProperties;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtKeyFactory {
    private final JwtProperties props;
    public JwtKeyFactory(final JwtProperties props) { this.props = props; }

    public Key hmacKey() {
        byte[] keyBytes = toKeyBytes(props.secret());
        if (keyBytes.length < 32) {
            throw new IllegalStateException();
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static byte[] toKeyBytes(String secret) {
        try {
            return Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException notBase64) {
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }
}
