package com.scriptopia.demo.service;

import com.scriptopia.demo.config.fastapi.FastApiEndpoint;
import com.scriptopia.demo.dto.gamesession.*;
import com.scriptopia.demo.dto.items.ItemFastApiRequest;
import com.scriptopia.demo.dto.items.ItemFastApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

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

    // 결과 생성 (확장용)
    public CreateGameDoneResponse done(CreateGameDoneRequest request) {
        return fastApiWebClient.post()
                .uri(FastApiEndpoint.DONE.getPath())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CreateGameDoneResponse.class)
                .block();
    }


    // 아이템 생성 (확장용)
    public ItemFastApiResponse item(ItemFastApiRequest request) {
        return fastApiWebClient.post()
                .uri(FastApiEndpoint.ITEM.getPath())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ItemFastApiResponse.class)
                .block();
    }

    // 게임 종료 생성 (확장용)
    public GameEndResponse end(GameEndRequest request) {
        return fastApiWebClient.post()
                .uri(FastApiEndpoint.END.getPath())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GameEndResponse.class)
                .block();
    }

    // 게임 종료 시 빅 이벤트 타이틀 처리
    public GameTitleResponse title(GameTitleRequest request) {
        return fastApiWebClient.post()
                .uri(FastApiEndpoint.TITLE.getPath())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GameTitleResponse.class)
                .block();
    }


}
