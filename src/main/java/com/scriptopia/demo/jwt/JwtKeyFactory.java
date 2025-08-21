package com.scriptopia.demo.jwt;


import com.scriptopia.demo.config.JwtProperties;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtKeyFactory {
    private final JwtProperties props;

    public JwtKeyFactory(final JwtProperties props) {this.props = props;}

    public Key hmacKey(){
        // 256비트로 키를 생성(사용을 provider에서)
        return Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }
}
