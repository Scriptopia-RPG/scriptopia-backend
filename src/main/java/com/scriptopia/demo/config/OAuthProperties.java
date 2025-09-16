package com.scriptopia.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {

    private ProviderProperties google;
    private ProviderProperties kakao;
    private ProviderProperties naver;

    @Getter
    @Setter
    public static class ProviderProperties {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String scope;
    }
}
