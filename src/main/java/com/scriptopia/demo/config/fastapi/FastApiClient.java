package com.scriptopia.demo.config.fastapi;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FastApiClient {

    private final WebClient webClient;

    public FastApiClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8000") // 환경별로 application.yml에 두는 것이 좋음
                .build();
    }

    public WebClient.RequestBodySpec post(FastApiEndpoint endpoint) {
        return webClient.post()
                .uri(endpoint.getPath())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    public WebClient getWebClient() {
        return webClient;
    }

}