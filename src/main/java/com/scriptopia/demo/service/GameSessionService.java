package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.domain.mongo.*;
import com.scriptopia.demo.dto.gamesession.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.*;
import com.scriptopia.demo.utils.GameBalanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final GameSessionMongoRepository gameSessionMongoRepository;
    private final UserItemRepository userItemRepository;
    private final ItemDefRepository itemDefRepository;

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


    @Transactional
    public StartGameResponse startNewGame(Long userId, StartGameRequest request) {


        // 1. 진행중인 게임 체크
        if (gameSessionRepository.existsByUser_Id(userId)) {
            throw new CustomException(ErrorCode.E_400_GAME_ALREADY_IN_PROGRESS);
        }


        UserItem userItem = null;

        // 물건을 가져왔다면 그 물건이 해당 플레이어의 것인지, 존재하는 것인지 확인
        if (request.getItemId() != null){
            Long itemId = Long.parseLong(request.getItemId());
            userItem = userItemRepository.findByUserIdAndItemDefId(userId, itemId)
                    .orElseThrow(() -> new CustomException(ErrorCode.E_400_ITEM_NOT_OWNED));


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
        ExternalGameResponse.PlayerInfo player = externalGame.getPlayerInfo();
        player.setLife(5);
        player.setLevel(1);
        player.setExperiencePoint(0);

        // 4. 아이템 적용 및 전투력 계산
        GameBalanceUtil.applyEquippedWeaponStatsAndCombatPoint(externalGame);
        GameBalanceUtil.applyEquippedArmorStatsAndHealthPoint(externalGame);
        GameBalanceUtil.applyEquippedArtifactStats(externalGame);


        // 5. MongoDB 저장
        GameSessionMongo mongoSession = new GameSessionMongo();
        mongoSession.setUserId(userId);
        mongoSession.setSceneType(SceneType.CHOICE); // 시작은 choice 기본값
        mongoSession.setStartedAt(LocalDateTime.now());
        mongoSession.setUpdatedAt(LocalDateTime.now());
        mongoSession.setBackground(request.getBackground());
        mongoSession.setProgress(0);


        // 아이템 정보
        List<InventoryItemMongo> mongoInventory = new ArrayList<>();
        List<ItemDefMongo> mongoItemDefs = new ArrayList<>();

        // FAST API 아이템 변환
        if (externalGame.getItemDef() != null) {
            for (var item : externalGame.getItemDef()) {
                var effects = item.getItemEffect() != null
                        ? item.getItemEffect().stream()
                        .map(e -> new ItemEffectMongo(
                                e.getItemEffectName(),
                                e.getItemEffectDescription(),
                                e.getGrade(),
                                e.getItemEffectWeight()
                        ))
                        .toList()
                        : Collections.emptyList();

                ItemDefMongo itemDefMongo = new ItemDefMongo(
                        item.getItemDefId(),
                        item.getItemPicSrc(),
                        item.getName(),
                        item.getDescription(),
                        item.getCategory(),
                        item.getBaseStat(),
                        (List<ItemEffectMongo>) effects,
                        item.getStrength(),
                        item.getAgility(),
                        item.getIntelligence(),
                        item.getLuck(),
                        item.getMainStat(),
                        item.getWeight(),
                        item.getGrade(),
                        item.getPrice()
                );

                mongoItemDefs.add(itemDefMongo);
            }
        }

        if (externalGame.getInventory() != null) {
            for (var inv : externalGame.getInventory()) {
                mongoInventory.add(new InventoryItemMongo(
                        inv.getItemDefId(),
                        inv.getAcquiredAt(),
                        inv.isEquipped(),
                        inv.getSource()
                ));
            }
        }

        long maxItemDefId = mongoItemDefs.stream()
                .mapToLong(ItemDefMongo::getItemDefId)
                .max()
                .orElse(0L);

        // 2. 사용자 아이템 추가 (두 번째 위치)
        if (userItem != null) {
            ItemDef userItemDef = userItem.getItemDef();

            long newItemDefId = maxItemDefId + 1;

            ItemDefMongo userItemDefMongo = new ItemDefMongo(
                    newItemDefId,
                    userItemDef.getPicSrc(),
                    userItemDef.getName(),
                    userItemDef.getDescription(),
                    userItemDef.getItemType(),
                    userItemDef.getBaseStat(),
                    userItemDef.getItemEffects() != null
                            ? userItemDef.getItemEffects().stream()
                            .map(e -> new ItemEffectMongo(
                                    e.getEffectName(),
                                    e.getEffectDescription(),
                                    e.getEffectGradeDef().getGrade(),
                                    1
                            ))
                            .toList()
                            : Collections.emptyList(),
                    userItemDef.getStrength(),
                    userItemDef.getAgility(),
                    userItemDef.getIntelligence(),
                    userItemDef.getLuck(),
                    userItemDef.getMainStat(),
                    1,
                    userItemDef.getItemGradeDef().getGrade(),
                    userItemDef.getPrice()
            );

            // 아이템 정의 리스트 두 번째 위치에 삽입
            if (mongoItemDefs.size() >= 1) {
                mongoItemDefs.add(1, userItemDefMongo);
            } else {
                mongoItemDefs.add(userItemDefMongo);
            }

            // Inventory 리스트에도 두 번째 위치에 삽입
            InventoryItemMongo userInventoryMongo = new InventoryItemMongo(
                    userItemDefMongo.getItemDefId(),
                    LocalDateTime.now(),
                    false,
                    "USER_ITEM"
            );

            if (mongoInventory.size() >= 1) {
                mongoInventory.add(1, userInventoryMongo);
            } else {
                mongoInventory.add(userInventoryMongo);
            }
        }

        // 3. MongoSession 저장
        mongoSession.setInventory(mongoInventory);
        mongoSession.setItemDef(mongoItemDefs);



        // 플레이어 정보
        ExternalGameResponse.PlayerInfo p = externalGame.getPlayerInfo();
        PlayerInfoMongo playerMongo = new PlayerInfoMongo(
                p.getName(),
                p.getLife(),
                p.getLevel(),
                p.getExperiencePoint(),
                p.getCombatPoint(),
                p.getHealthPoint(),
                p.getTrait(),
                p.getStrength(),
                p.getAgility(),
                p.getIntelligence(),
                p.getLuck(),
                p.getGold()
        );
        mongoSession.setPlayerInfo(playerMongo);
        
        // 초기 히스토리 저장
        HistoryInfoMongo history = new HistoryInfoMongo();
        history.setWorldView(externalGame.getWorldView());
        history.setBackgroundStory(externalGame.getBackgroundStory());
        mongoSession.setHistoryInfo(history);


        int stageCount = 10; // 예: 10스테이지
        List<Integer> stageList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < stageCount; i++) {
            // 0: 작은 이벤트, 1: 큰 이벤트
            stageList.add(random.nextInt(2));
        }
        mongoSession.setStage(stageList);

        // 게임 진행 시 필요한 것들은 만들어만 두기
        mongoSession.setChoiceInfo(new ChoiceInfoMongo());
        mongoSession.setDoneInfo(new DoneInfoMongo());
        mongoSession.setShopInfo(new ShopInfoMongo());
        mongoSession.setBattleInfo(new BattleInfoMongo());
        mongoSession.setRewardInfo(new RewardInfoMongo());

        GameSessionMongo savedMongo = gameSessionMongoRepository.save(mongoSession);

        // 5. MySQL GameSession에 MongoDB PK 저장
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND)
        );

        GameSession mysqlSession = new GameSession();
        mysqlSession.setUser(user);
        mysqlSession.setMongoId(savedMongo.getId());
        gameSessionRepository.save(mysqlSession);


        StartGameResponse startGameResponse = new StartGameResponse(
                "게임이 생성되었습니다.",
                mysqlSession.getMongoId()
        );

        // 6. MongoDB PK 반환
        return startGameResponse;
    }


}
