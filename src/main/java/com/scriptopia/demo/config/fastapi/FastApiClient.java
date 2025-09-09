package com.scriptopia.demo.config.fastapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class FastApiClient {

    @Bean
    public WebClient fastApiWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8000")
                .build();
    }
}