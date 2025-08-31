package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.GameSession;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.dto.gamesession.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.GameSessionRepository;
import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.utils.GameBalanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final RestTemplateBuilder restTemplateBuilder;
    private final RestTemplate restTemplate;

    public ResponseEntity<?> getGameSession(Long userid) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        var sessions = gameSessionRepository.findAllByUser_Id(user.getId());
        var dtos = sessions.stream().map(s -> {
            var dto = new GameSessionResponse();
            dto.setId(s.getId());
            dto.setSessionId(s.getMongoId());
            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    @Transactional
    public ResponseEntity<?> saveGameSession(Long userId, String sessionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        GameSession gameSession = new GameSession();
        gameSession.setUser(user);
        gameSession.setMongoId(sessionId);
        return ResponseEntity.ok(gameSessionRepository.save(gameSession));
    }

    @Transactional
    public ResponseEntity<?> deleteGameSession(Long userId, String sessionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        GameSession gameSession = gameSessionRepository.findByUser_IdAndMongoId(user.getId(), sessionId)
                        .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));
        gameSessionRepository.delete(gameSession);

        return ResponseEntity.ok("선택하신 게임이 삭제되었습니다.");
    }


    public String startNewGame(Long userId, StartGameRequest request) {

        // 1. 진행중인 게임 체크
        if (gameSessionRepository.existsByUserIdAndSceneTypeNotDone(userId)) {
            throw new CustomException(ErrorCode.E_400_GAME_ALREADY_IN_PROGRESS);
        }


        CreateGameRequest createGameRequest = new CreateGameRequest(
                request.getBackground(),
                request.getCharacterName(),
                request.getCharacterDescription()
        );

        // 2. FastAPI 호출(테스트용 추후 변경 가능)
        String url = "http://localhost:8000/games/init";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateGameRequest> requestEntity = new HttpEntity<>(createGameRequest, headers);

        ResponseEntity<ExternalGameResponse> responseEntity =
                restTemplate.exchange(url, HttpMethod.POST, requestEntity, ExternalGameResponse.class);

        ExternalGameResponse externalGame = responseEntity.getBody();

        if (externalGame == null) {
            throw new CustomException(ErrorCode.E_500_EXTERNAL_API_ERROR);
        }

        // 3. 밸런스 재세팅
        ExternalGameResponse.PlayerInfo player = externalGame.getPlayer_info();
        player.setLife(5);
        player.setLevel(1);
        player.setExperience_point(0);

        // 4. 아이템 적용 및 전투력 계산
        GameBalanceUtil.applyEquippedWeaponStatsAndCombatPoint(externalGame);


        GameBalanceUtil.applyEquippedArmorStatsAndHealthPoint(externalGame);






        // 4. MongoDB 저장
        GameSessionMongo mongoSession = new GameSessionMongo();
        mongoSession.setUserId(userId);
        mongoSession.setSceneType("choice"); // 시작은 choice 등 기본값
        mongoSession.setStartedAt(LocalDateTime.now());
        mongoSession.setUpdatedAt(LocalDateTime.now());
        mongoSession.setBackground(background);
        mongoSession.setPlayerInfo(convertPlayerInfo(externalGame));
        mongoSession.setInventory(convertInventory(externalGame));
        mongoSession.setItemDef(convertItemDef(externalGame));
        mongoSession.setProgress(0);

        GameSessionMongo savedMongo = gameSessionMongoRepository.save(mongoSession);

        // 5. MySQL GameSession에 MongoDB PK 저장
        GameSession mysqlSession = new GameSession();
        mysqlSession.setUserId(userId);
        mysqlSession.setMongoId(savedMongo.getId());
        mysqlSession.setSceneType("choice");
        mysqlSession.setStartedAt(LocalDateTime.now());
        mysqlSession.setUpdatedAt(LocalDateTime.now());
        gameSessionRepository.save(mysqlSession);

        // 6. MongoDB PK 반환
        return savedMongo.getId();
    }

    private GameSessionMongo.PlayerInfoMongo convertPlayerInfo(ExternalGameResponse external) {
        GameSessionMongo.PlayerInfoMongo info = new GameSessionMongo.PlayerInfoMongo();
        ExternalGameResponse.PlayerInfo p = external.getPlayer_info();
        info.setName(p.getName());
        info.setLife(p.getLife());
        info.setLevel(p.getLevel());
        info.setExperiencePoint(p.getExperience_point());
        info.setCombatPoint(p.getCombat_point());
        info.setHealthPoint(p.getHealth_point());
        info.setTrait(p.getTrait());
        info.setStrength(p.getStrength());
        info.setAgility(p.getAgility());
        info.setIntelligence(p.getIntelligence());
        info.setLuck(p.getLuck());
        info.setGold(p.getGold());
        return info;
    }

    private java.util.List<GameSessionMongo.InventoryItemMongo> convertInventory(ExternalGameResponse external) {
        java.util.List<GameSessionMongo.InventoryItemMongo> list = new java.util.ArrayList<>();
        for (ExternalGameResponse.InventoryItem item : external.getInventory()) {
            GameSessionMongo.InventoryItemMongo mongoItem = new GameSessionMongo.InventoryItemMongo();
            mongoItem.setItemDefId(item.getItem_def_id());
            mongoItem.setAcquiredAt(LocalDateTime.parse(item.getAcquired_at()));
            mongoItem.setEquipped(item.isEquipped());
            mongoItem.setSource(item.getSource());
            list.add(mongoItem);
        }
        return list;
    }

    private java.util.List<GameSessionMongo.ItemDefMongo> convertItemDef(ExternalGameResponse external) {
        java.util.List<GameSessionMongo.ItemDefMongo> list = new java.util.ArrayList<>();
        for (ExternalGameResponse.ItemDef def : external.getItem_def()) {
            GameSessionMongo.ItemDefMongo mongoDef = new GameSessionMongo.ItemDefMongo();
            mongoDef.setItemDefId(def.getItem_def_id());
            mongoDef.setItemPicSrc(def.getItem_pic_src());
            mongoDef.setName(def.getName());
            mongoDef.setDescription(def.getDescription());
            mongoDef.setCategory(def.getCategory());
            mongoDef.setBaseStat(def.getBase_stat());
            mongoDef.setStrength(def.getStrength());
            mongoDef.setAgility(def.getAgility());
            mongoDef.setIntelligence(def.getIntelligence());
            mongoDef.setLuck(def.getLuck());
            mongoDef.setMainStat(def.getMain_stat());
            mongoDef.setWeight(def.getWeight());
            mongoDef.setGrade(def.getGrade());
            mongoDef.setPrice(def.getPrice());

            java.util.List<GameSessionMongo.ItemDefMongo.ItemEffectMongo> effectList = new java.util.ArrayList<>();
            for (ExternalGameResponse.ItemDef.ItemEffect eff : def.getItem_effect()) {
                GameSessionMongo.ItemDefMongo.ItemEffectMongo effMongo = new GameSessionMongo.ItemDefMongo.ItemEffectMongo();
                effMongo.setItemEffectName(eff.getItem_effect_name());
                effMongo.setItemEffectDescription(eff.getItem_effect_description());
                effMongo.setGrade(eff.getGrade());
                effMongo.setItemEffectWeight(eff.getItem_effect_weight());
                effectList.add(effMongo);
            }
            mongoDef.setItemEffect(effectList);

            list.add(mongoDef);
        }
        return list;
    }


}
