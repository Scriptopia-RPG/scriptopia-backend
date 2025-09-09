package com.scriptopia.demo.service;

import com.scriptopia.demo.config.fastapi.FastApiEndpoint;
import com.scriptopia.demo.dto.gamesession.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class FastApiService {

    private final WebClient fastApiWebClient;

    // 게임 초기화
    public ExternalGameResponse initGame(CreateGameRequest request) {
        return fastApiWebClient.post()
                .uri(FastApiEndpoint.INIT.getPath())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalGameResponse.class)
                .block();
    }

    // 선택지 생성
    public CreateGameChoiceResponse makeChoice(CreateGameChoiceRequest request) {
        return fastApiWebClient.post()
                .uri(FastApiEndpoint.CHOICE.getPath())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CreateGameChoiceResponse.class)
                .block();
    }

    // 전투 호출 (확장용)
    public CreateGameBattleResponse battle(CreateGameBattleRequest request) {
        return fastApiWebClient.post()
                .uri(FastApiEndpoint.BATTLE.getPath())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CreateGameBattleResponse.class)
                .block();
    }
}
