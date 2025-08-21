package com.scriptopia.demo.jwt;

import com.scriptopia.demo.config.JwtProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtProvidertest {

    @Test
    void issue_and_parse() {
        // 가짜 프로퍼티
        var props = new JwtProperties("scriptopia", 1800, 1209600, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@"); // >= 64 bytes
        var keyFactory = new JwtKeyFactory(props);

        var provider = new JwtProvider(props, keyFactory);

        String at = provider.createAccessToken(1L, List.of("ROLE_USER"));
        assertThat(provider.isValid(at)).isTrue();
        assertThat(provider.getUserId(at)).isEqualTo(1L);
        assertThat(provider.getRoles(at)).containsExactly("ROLE_USER");
        System.out.println(at);
        String rt = provider.createrefreshToken(1L, "dev-abc");
        System.out.println(rt);
        assertThat(provider.isValid(rt)).isTrue();
        assertThat(provider.getDeviceId(rt)).isEqualTo("dev-abc");
    }
}
