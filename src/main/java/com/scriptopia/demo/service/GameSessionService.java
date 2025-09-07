package com.scriptopia.demo.service;

import com.mongodb.client.MongoClient;
import com.scriptopia.demo.repository.mongo.GameSessionMongoRepository;
import com.scriptopia.demo.repository.mongo.ItemDefMongoRepository;
import com.scriptopia.demo.utils.InitGameData;
import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.domain.mongo.*;
import com.scriptopia.demo.dto.gamesession.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.*;
import com.scriptopia.demo.dto.gamesession.ExternalGameResponse.PlayerInfo;
import com.scriptopia.demo.dto.gamesession.ExternalGameResponse.ItemDef;


import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final ItemDefMongoRepository itemDefMongoRepository;
    private final GameSessionRepository gameSessionRepository;
    private final ItemGradeDefRepository itemGradeDefRepository;
    private final EffectGradeDefRepository effectGradeDefRepository;
    private final UserRepository userRepository;
    private final GameSessionMongoRepository gameSessionMongoRepository;
    private final UserItemRepository userItemRepository;
    private final MongoClient mongo;

    public boolean duplcatedGameSession(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        boolean game = gameSessionRepository.existsByUserId(user.getId());

        if(game) {
            return true;
        }
        else return false;
    }

    public ResponseEntity<?> getGameSession(Long userid) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));


        GameSession sessions = gameSessionRepository.findByMongoId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_STORED_GAME_NOT_FOUND));

        return ResponseEntity.ok(sessions);
    }

    @Transactional
    public ResponseEntity<?> saveGameSession(Long userId, String sessionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));
        boolean game = gameSessionRepository.existsByUserId(user.getId());

        if(!game) {
            GameSession gameSession = new GameSession();
            gameSession.setUser(user);
            gameSession.setMongoId(sessionId);
            return ResponseEntity.ok(gameSessionRepository.save(gameSession));
        }
        else throw new CustomException(ErrorCode.E_404_Duplicated_Game_Session);
    }

    @Transactional
    public ResponseEntity<?> deleteGameSession(Long userId, String sessionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        GameSession gameSession = gameSessionRepository.findByUserIdAndMongoId(user.getId(), sessionId)
                        .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));
        gameSessionRepository.delete(gameSession);

        return ResponseEntity.ok("선택하신 게임이 삭제되었습니다.");
    }


    @Transactional
    public StartGameResponse startNewGame(Long userId, StartGameRequest request) {

        // 1. 진행중인 게임 체크
        if (gameSessionRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.E_400_GAME_ALREADY_IN_PROGRESS);
        }


        UserItem userItem = null;

        // 물건을 가져왔다면 그 물건이 해당 플레이어의 것인지, 존재하는 것인지 확인
        if (request.getItemId() != null){
            Long itemId = Long.parseLong(request.getItemId());
            userItem = userItemRepository.findByUserIdAndItemDefId(userId, itemId)
                    .orElseThrow(() -> new CustomException(ErrorCode.E_400_ITEM_NOT_OWNED));

            if (userItem.getRemainingUses() <= 0) {
                throw new CustomException(ErrorCode.E_400_ITEM_NO_USES_LEFT);
            }
        }


        CreateGameRequest createGameRequest = new CreateGameRequest(
                request.getBackground(),
                request.getCharacterName(),
                request.getCharacterDescription()
        );


        // WebClient 인스턴스 생성
        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:8000")
                .build();

        // FastAPI 호출(테스트용 추후 변경 가능)
        ExternalGameResponse externalGame = client.post()
                .uri("/games/init")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createGameRequest) //
                .retrieve()
                .bodyToMono(ExternalGameResponse.class)
                .block(); //

        // 응답 검증
        if (externalGame == null) {
            throw new CustomException(ErrorCode.E_500_EXTERNAL_API_ERROR);
        }

        InitGameData initGameData = new InitGameData(
                externalGame.getPlayerInfo().getStartStat(),
                Grade.COMMON,
                itemGradeDefRepository,
                effectGradeDefRepository
        );


        // GameSession Data
        GameSessionMongo mongoSession = new GameSessionMongo();
        mongoSession.setUserId(userId);
        mongoSession.setSceneType(SceneType.CHOICE); // 시작은 choice 기본값
        mongoSession.setStartedAt(LocalDateTime.now());
        mongoSession.setUpdatedAt(LocalDateTime.now());
        mongoSession.setPreChoice(null);
        mongoSession.setBackground(externalGame.getBackgroundStory());
        mongoSession.setLocation(externalGame.getLocation());
        mongoSession.setProgress(0);
        mongoSession.setStage(initGameData.getStages());

        // PlayerInfo Data
        PlayerInfo playerInfo = externalGame.getPlayerInfo();

        PlayerInfoMongo playerInfoMongo = PlayerInfoMongo.builder()
                .name(playerInfo.getName())
                .life(initGameData.getLife())
                .level(initGameData.getLevel())
                .healthPoint(initGameData.getHealthPoint())
                .experiencePoint(initGameData.getExperiencePoint())
                .trait(playerInfo.getTrait())
                .strength(initGameData.getPlayerStr())
                .agility(initGameData.getPlayerAgi())
                .intelligence(initGameData.getPlayerInt())
                .luck(initGameData.getPlayerLuk())
                .gold(initGameData.getGold())
                .build();

        mongoSession.setPlayerInfo(playerInfoMongo);

        // NpcInfo Data
        mongoSession.setNpcInfo(new NpcInfoMongo());

        //ItemEffect Data
        ItemDef itemDef = externalGame.getItemDef();
        ItemDef.ItemEffect itemEffect = itemDef.getItemEffect();

        List<ItemEffectMongo> itemEffectsMongo = new ArrayList<>();
        itemEffectsMongo.add(
                ItemEffectMongo.builder()
                .itemEffectName(itemEffect.getItemEffectName())
                .itemEffectDescription(itemEffect.getItemEffectDescription())
                .effectProbability(EffectProbability.COMMON)
                .build()
        );

        //ItemDef Data
        ItemDefMongo itemDefMongo = ItemDefMongo.builder()
                .itemPicSrc("common item img")
                .name(itemDef.getName())
                .description(itemDef.getDescription())
                .category(ItemType.WEAPON)
                .baseStat(initGameData.getBaseStat())
                .itemEffect(itemEffectsMongo)
                .strength(initGameData.getItemStr())
                .agility(initGameData.getItemAgi())
                .intelligence(initGameData.getItemInt())
                .luck(initGameData.getItemLuk())
                .mainStat(itemDef.getMainStat())
                .grade(Grade.COMMON)
                .price(initGameData.getItemPrice())
                .build();

        ItemDefMongo savedItemDefMongo = itemDefMongoRepository.save(itemDefMongo);

        // CreatedItem Data
        List<String> createdItems = new ArrayList<>();
        createdItems.add(savedItemDefMongo.getId());

        mongoSession.setCreatedItems(createdItems);

        // Inventory Data
        List<InventoryMongo> inventoryMongoList = new ArrayList<>();
        inventoryMongoList.add(InventoryMongo.builder()
                .ItemDefId(savedItemDefMongo.getId())
                .acquiredAt(LocalDateTime.now())
                .equipped(true)
                .source("StartWeapon")
                .build()
        );

        mongoSession.setInventory(inventoryMongoList);

        // ChoiceInfo Data
        mongoSession.setChoiceInfo(new ChoiceInfoMongo());

        //DoneInfoMongo Data
        mongoSession.setDoneInfo(new DoneInfoMongo());

        //ShopInfoMongo Data
        mongoSession.setShopInfo(new ShopInfoMongo());

        //BattleInfoMongo Data
        mongoSession.setBattleInfo(new BattleInfoMongo());

        //RewardInfoMongo Data
        mongoSession.setRewardInfo(new RewardInfoMongo());

        //HistoryInfoMongo Data
        mongoSession.setHistoryInfo(
                HistoryInfoMongo.builder()
                .worldView(externalGame.getWorldView())
                .backgroundStory(externalGame.getBackgroundStory())
                .worldPrompt(request.getBackground())
                .build()
        );

        // 최종 GameSession 저장
        GameSessionMongo savedMongo = gameSessionMongoRepository.save(mongoSession);

        // MySQL GameSession MongoDB PK 저장
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND)
        );

        GameSession mysqlSession = new GameSession();
        mysqlSession.setUser(user);
        mysqlSession.setMongoId(savedMongo.getId());
        gameSessionRepository.save(mysqlSession);

        if (userItem != null) {
            userItem.setRemainingUses(userItem.getRemainingUses() - 1);
            userItemRepository.save(userItem);
        }

        // MongoDB PK 반환
        return new StartGameResponse(
                "게임이 생성되었습니다.",
                mysqlSession.getMongoId()
        );
    }


    @Transactional
    public CreateGameChoiceRequest mapToCreateGameChoiceRequest(Long userId) {

        // 1. MySQL에서 게임 세션 확인
        if (!gameSessionRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.E_404_STORED_GAME_NOT_FOUND);
        }


        GameSession gameSession = gameSessionRepository.findByMongoId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        String gameId = gameSession.getMongoId();
        GameSessionMongo gameSessionMongo = gameSessionMongoRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        // 3. DTO로 매핑
        CreateGameChoiceRequest response = new CreateGameChoiceRequest();
        response.setWorldView(gameSessionMongo.getHistoryInfo().getWorldView());

        //
        response.setCurrentStory(gameSessionMongo.getHistoryInfo().getBackgroundStory());
        response.setCurrentChoice(null); // 초기값


        response.setLocation(gameSessionMongo.getLocation());
        response.setEventType(ChoiceEventType.getChoiceEventType());
        response.setNpcRank(NpcGrade);

        // playerInfo 매핑
        CreateGameChoiceRequest.PlayerInfo playerInfo = new CreateGameChoiceRequest.PlayerInfo();
        playerInfo.setName(gameSessionMongo.getPlayerInfo().getName());
        playerInfo.setTrait(gameSessionMongo.getPlayerInfo().getTrait());
        response.setPlayerInfo(playerInfo);

        // itemInfo 매핑
        List<CreateGameChoiceRequest.ItemInfo> itemInfoList = gameSessionMongo.getInventory().stream()
                .map(inv -> {
                    CreateGameChoiceRequest.ItemInfo itemInfo = new CreateGameChoiceRequest.ItemInfo();
                    ItemDefMongo itemDef = itemDefMongoRepository.findById(inv.getItemDefId())
                            .orElseThrow(() -> new CustomException(ErrorCode.E_404_ITEM_NOT_FOUND));
                    itemInfo.setName(itemDef.getName());
                    itemInfo.setDescription(itemDef.getDescription());
                    return itemInfo;
                }).toList();
        response.setItemInfo(itemInfoList);

        return response;
    }
}
