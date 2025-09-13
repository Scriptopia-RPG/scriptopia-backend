package com.scriptopia.demo.service;

import com.scriptopia.demo.dto.gamesession.ingame.InGameBattleResponse;
import com.scriptopia.demo.dto.gamesession.ingame.InGameChoiceResponse;
import com.scriptopia.demo.dto.gamesession.ingame.InGameDoneResponse;
import com.scriptopia.demo.dto.items.ItemDefRequest;
import com.scriptopia.demo.dto.items.ItemFastApiResponse;
import com.scriptopia.demo.mapper.InGameMapper;
import com.scriptopia.demo.repository.mongo.GameSessionMongoRepository;
import com.scriptopia.demo.repository.mongo.ItemDefMongoRepository;
import com.scriptopia.demo.utils.GameBalanceUtil;
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

import java.awt.*;
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
    private final FastApiService fastApiService;
    private final ItemDefService itemDefService;
    private final InGameMapper inGameMapper;


    public ResponseEntity<?> getGameSession(Long userid) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));


        GameSession sessions = gameSessionRepository.findByMongoId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_STORED_GAME_NOT_FOUND));

        GameSessionResponse gameSessionResponse = new GameSessionResponse();
        gameSessionResponse.setSessionId(sessions.getMongoId());

        return ResponseEntity.ok(gameSessionResponse);
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


        ExternalGameResponse externalGame = fastApiService.initGame(createGameRequest);

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
                .itemDefId(savedItemDefMongo.getId())
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

        gameToChoice(userId);

        // MongoDB PK 반환
        return new StartGameResponse(
                "게임이 생성되었습니다.",
                mysqlSession.getMongoId()
        );
    }

    public Object getInGameDataDto(Long userId){
        if (!gameSessionRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.E_404_STORED_GAME_NOT_FOUND);
        }

        GameSession gameSession = gameSessionRepository.findByMongoId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        String gameId = gameSession.getMongoId();
        GameSessionMongo gameSessionMongo = gameSessionMongoRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        SceneType currentSceneType = gameSessionMongo.getSceneType();
        if (currentSceneType == SceneType.CHOICE) {
            return InGameChoiceResponse.builder()
                    .sceneType("CHOICE")
                    .startedAt(gameSessionMongo.getStartedAt())
                    .updatedAt(gameSessionMongo.getUpdatedAt())
                    .background(gameSessionMongo.getBackground())
                    .location(gameSessionMongo.getLocation())
                    .progress(gameSessionMongo.getProgress())
                    .stageSize(gameSessionMongo.getStage() != null ? gameSessionMongo.getStage().size() : 0)
                    .playerInfo(inGameMapper.mapPlayer(gameSessionMongo.getPlayerInfo()))
                    .npcInfo(inGameMapper.mapNpc(gameSessionMongo.getNpcInfo()))
                    .inventory(inGameMapper.mapInventory(gameSessionMongo.getInventory())  )
                    .choiceInfo(inGameMapper.mapChoice(gameSessionMongo.getChoiceInfo()))
                    .build();

        } else if (currentSceneType == SceneType.DONE) {

            RewardInfoMongo rewardInfo = gameSessionMongo.getRewardInfo();

            List<String> gainedItemNames = List.of();
            if (rewardInfo != null && rewardInfo.getGainedItemDefId() != null) {
                gainedItemNames = rewardInfo.getGainedItemDefId().stream()
                        .map(itemDefId -> itemDefMongoRepository.findById(itemDefId)
                                .map(ItemDefMongo::getName)
                                .orElse("Unknown Item"))
                        .toList();
            }


            return InGameDoneResponse.builder()
                    .sceneType("DONE")
                    .startedAt(gameSessionMongo.getStartedAt())
                    .updatedAt(gameSessionMongo.getUpdatedAt())
                    .background(gameSessionMongo.getBackground())
                    .location(gameSessionMongo.getLocation())
                    .progress(gameSessionMongo.getProgress())
                    .stageSize(gameSessionMongo.getStage() != null ? gameSessionMongo.getStage().size() : 0)
                    .playerInfo(inGameMapper.mapPlayer(gameSessionMongo.getPlayerInfo()))
                    .npcInfo(inGameMapper.mapNpc(gameSessionMongo.getNpcInfo()))
                    .inventory(inGameMapper.mapInventory(gameSessionMongo.getInventory()))
                    .rewardInfo(
                            InGameDoneResponse.RewardInfoResponse.builder()
                                    .gainedItemNames(gainedItemNames)
                                    .rewardStrength(rewardInfo != null && rewardInfo.getRewardStrength() != null ? rewardInfo.getRewardStrength() : 0)
                                    .rewardAgility(rewardInfo != null && rewardInfo.getRewardAgility() != null ? rewardInfo.getRewardAgility() : 0)
                                    .rewardIntelligence(rewardInfo != null && rewardInfo.getRewardIntelligence() != null ? rewardInfo.getRewardIntelligence() : 0)
                                    .rewardLuck(rewardInfo != null && rewardInfo.getRewardLuck() != null ? rewardInfo.getRewardLuck() : 0)
                                    .rewardLife(rewardInfo != null && rewardInfo.getRewardLife() != null ? rewardInfo.getRewardLife() : 0)
                                    .rewardGold(rewardInfo != null && rewardInfo.getRewardGold() != null ? rewardInfo.getRewardGold() : 0)
                                    .build()
                    )
                    .build();


        } else if (currentSceneType == SceneType.SHOP) {

        } else if (currentSceneType == SceneType.BATTLE) {
            BattleInfoMongo battleInfo = gameSessionMongo.getBattleInfo();

            return InGameBattleResponse.builder()
                    .sceneType("BATTLE")
                    .startedAt(gameSessionMongo.getStartedAt())
                    .updatedAt(gameSessionMongo.getUpdatedAt())
                    .background(gameSessionMongo.getBackground())
                    .location(gameSessionMongo.getLocation())
                    .progress(gameSessionMongo.getProgress())
                    .stageSize(gameSessionMongo.getStage() != null ? gameSessionMongo.getStage().size() : 0)
                    .playerInfo(inGameMapper.mapPlayer(gameSessionMongo.getPlayerInfo()))
                    .npcInfo(inGameMapper.mapNpc(gameSessionMongo.getNpcInfo()))
                    .inventory(inGameMapper.mapInventory(gameSessionMongo.getInventory()))
                    .playerHp(battleInfo != null && battleInfo.getPlayerHp() != null
                            ? battleInfo.getPlayerHp().stream().map(Long::intValue).toList()
                            : List.of())
                    .enemyHp(battleInfo != null && battleInfo.getEnemyHp() != null
                            ? battleInfo.getEnemyHp().stream().map(Long::intValue).toList()
                            : List.of())
                    .battleStory(battleInfo != null && battleInfo.getBattleTurn() != null
                            ? battleInfo.getBattleTurn().stream()
                            .map(bs -> InGameBattleResponse.BattleStoryResponse.builder()
                                    .turnInfo(bs.getTurnInfo())
                                    .build())
                            .toList()
                            : List.of())
                    .playerWin(battleInfo != null ? battleInfo.getPlayerWin() : null)
                    .curTurnId(battleInfo != null ? battleInfo.getCurTurnId() : null)
                    .build();

        }

        return null;
    }


    /**
     * 게임 진행
     * @param userId
     */
    @Transactional
    public GameSessionMongo gameProgress(Long userId) {

        if (!gameSessionRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.E_404_STORED_GAME_NOT_FOUND);
        }


        GameSession gameSession = gameSessionRepository.findByMongoId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        String gameId = gameSession.getMongoId();
        GameSessionMongo gameSessionMongo = gameSessionMongoRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        SceneType currentSceneType = gameSessionMongo.getSceneType();
        switch (currentSceneType) {
            case SceneType.CHOICE -> {
                gameToChoice(userId);
            }
            case SceneType.BATTLE -> {
                gameToDone(userId);
            }
            case SceneType.DONE -> {
                gameToChoice(userId);
            }
            default -> throw new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND);
        }

        return gameSessionMongo;
    }

        /**
         * 선택지 생성
         * @param userId
         */
    @Transactional
    public GameSessionMongo gameToChoice(Long userId) {

        if (!gameSessionRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.E_404_STORED_GAME_NOT_FOUND);
        }


        GameSession gameSession = gameSessionRepository.findByMongoId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        String gameId = gameSession.getMongoId();
        GameSessionMongo gameSessionMongo = gameSessionMongoRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));

        CreateGameChoiceRequest fastApiRequest = new CreateGameChoiceRequest();
        fastApiRequest.setWorldView(gameSessionMongo.getHistoryInfo().getWorldView());
        fastApiRequest.setLocation(gameSessionMongo.getLocation());


        List<String> statInfo = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            statInfo.add(Stat.getRandomMainStat().toString());
        }


        fastApiRequest.setChoiceStat(statInfo);


        int progress = gameSessionMongo.getProgress();
        List<Integer> stage = gameSessionMongo.getStage();
        int currentEventStage = stage.get(progress); // 짝수면 -1한 history의 이야기를 넣어줘야 함

        if (currentEventStage == 0) {
            fastApiRequest.setCurrentStory(gameSessionMongo.getBackground());
            fastApiRequest.setCurrentChoice(gameSessionMongo.getPreChoice());
        } else {
            if (currentEventStage % 2 == 1) {
                fastApiRequest.setCurrentStory(gameSessionMongo.getHistoryInfo().getBackgroundStory());
                fastApiRequest.setCurrentChoice(null);
            } else {
                fastApiRequest.setCurrentChoice(null);
                switch (currentEventStage) {
                    case 2:
                        fastApiRequest.setCurrentStory(gameSessionMongo.getHistoryInfo().getEpilogue1Content());
                        break;
                    case 4:
                        fastApiRequest.setCurrentStory(gameSessionMongo.getHistoryInfo().getEpilogue2Content());
                        break;
                    case 6:
                        fastApiRequest.setCurrentStory(gameSessionMongo.getHistoryInfo().getEpilogue3Content());
                        break;
                }
            }
        }

        ChoiceEventType currentEventType = ChoiceEventType.getChoiceEventType();
        fastApiRequest.setEventType(currentEventType);

        int currentNpcRank = 0;
        if (currentEventType == ChoiceEventType.LIVING) {
            int currentChapter = progress / (stage.size() / 3 + 1) + 1;
            currentNpcRank = NpcGrade.getNpcNumberByRandom(currentChapter);
            fastApiRequest.setNpcRank(currentNpcRank);
        }

        // playerInfo 매핑
        CreateGameChoiceRequest.PlayerInfo playerInfo = new CreateGameChoiceRequest.PlayerInfo();
        playerInfo.setName(gameSessionMongo.getPlayerInfo().getName());
        playerInfo.setTrait(gameSessionMongo.getPlayerInfo().getTrait());
        fastApiRequest.setPlayerInfo(playerInfo);

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
        fastApiRequest.setItemInfo(itemInfoList);

        CreateGameChoiceResponse createGameChoiceResponse = fastApiService.makeChoice(fastApiRequest);

        if (createGameChoiceResponse == null) {
            throw new CustomException(ErrorCode.E_500_EXTERNAL_API_ERROR);
        }

        gameSessionMongo.setSceneType(SceneType.CHOICE);
        gameSessionMongo.setUpdatedAt(LocalDateTime.now());
        gameSessionMongo.setBackground(createGameChoiceResponse.getChoiceInfo().getStory());
        gameSessionMongo.setProgress(gameSessionMongo.getProgress());

        NpcInfoMongo npcInfoMongo = null;
        if (currentNpcRank > 0){
            int[] npcStat = GameBalanceUtil.getNpcStatsByRank(currentNpcRank);
            npcInfoMongo = NpcInfoMongo.builder()
                    .rank(currentNpcRank)
                    .name(createGameChoiceResponse.getNpcInfo().getName())
                    .trait(createGameChoiceResponse.getNpcInfo().getTrait())
                    .NpcWeaponName(createGameChoiceResponse.getNpcInfo().getNpcWeaponName())
                    .NpcWeaponDescription(createGameChoiceResponse.getNpcInfo().getNpcWeaponDescription())
                    .strength(npcStat[0])
                    .agility(npcStat[1])
                    .intelligence(npcStat[2])
                    .luck(npcStat[3])
                    .build();

            gameSessionMongo.setNpcInfo(npcInfoMongo);
        }


        List<ChoiceMongo> choiceList = new ArrayList<>();
        for (int i = 0; i < createGameChoiceResponse.getChoiceInfo().getChoice().size(); i++) {
            var choice = createGameChoiceResponse.getChoiceInfo().getChoice().get(i);

            ChoiceMongo choiceMongo = ChoiceMongo.builder()
                    .detail(choice.getDetail())
                    .stats(Stat.valueOf(statInfo.get(i)))
                    .probability(GameBalanceUtil.getChoiceProbability(Stat.valueOf(statInfo.get(i)), gameSessionMongo.getPlayerInfo()))
                    .resultType(ChoiceResultType.nextResultType(currentEventType))
                    .rewardType(RewardType.getRandomRewardType())
                    .build();

            choiceList.add(choiceMongo);
        }


        ChoiceMongo promptChoice = ChoiceMongo.builder()
                .detail(null)
                .stats(null)
                .probability(null)
                .resultType(ChoiceResultType.nextResultType(currentEventType))
                .rewardType(RewardType.getRandomRewardType())
                .build();
        choiceList.add(promptChoice);


        ChoiceInfoMongo choiceInfoMongo = ChoiceInfoMongo.builder()
                .eventType(fastApiRequest.getEventType())
                .story(createGameChoiceResponse.getChoiceInfo().getStory())
                .choice(choiceList)
                .build();

        gameSessionMongo.setChoiceInfo(choiceInfoMongo);


        gameSessionMongoRepository.save(gameSessionMongo);

        return gameSessionMongo;
    }

    /**
     * @param userId
     * @return win?
     */
    @Transactional
    public int gameToBattle(Long userId) {
        if (!gameSessionRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.E_404_STORED_GAME_NOT_FOUND);
        }


        GameSession gameSession = gameSessionRepository.findByMongoId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        String gameId = gameSession.getMongoId();
        GameSessionMongo gameSessionMongo = gameSessionMongoRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        // 장착된 플레이어 아이템
        List<ItemDefMongo> equippedItems = new ArrayList<>();
        for (InventoryMongo inv : gameSessionMongo.getInventory()) {
            if (inv.isEquipped()) {
                ItemDefMongo item = itemDefMongoRepository.findById(inv.getItemDefId())
                        .orElseThrow(() -> new CustomException(ErrorCode.E_404_ITEM_NOT_FOUND));
                equippedItems.add(item);
            }
        }

        // weapon, armor, artifact 분류
        ItemDefMongo weapon = null;
        ItemDefMongo armor = null;
        ItemDefMongo artifact = null;

        for (ItemDefMongo item : equippedItems) {
            switch (item.getCategory()) {
                case ItemType.WEAPON -> weapon = item;
                case ItemType.ARMOR -> armor = item;
                case ItemType.ARTIFACT -> artifact = item;
            }
        }

        int playerDmg = (weapon == null) ? 22 : weapon.getBaseStat();
        int playerHp = (armor == null) ? gameSessionMongo.getPlayerInfo().getHealthPoint() : armor.getBaseStat();

        int npcRank = gameSessionMongo.getNpcInfo().getRank();
        int playerWeaponDmg = GameBalanceUtil.getPlayerWeaponDmg(gameSessionMongo.getPlayerInfo(), weapon);
        int playerCombatPoint = GameBalanceUtil.getBattlePlayerCombatPoint(gameSessionMongo.getPlayerInfo(), weapon, effectGradeDefRepository);
        int npcCombatPoint = GameBalanceUtil.getNpcCombatPoint(npcRank);
        Integer playerWin = GameBalanceUtil.simulateBattle(playerCombatPoint,npcCombatPoint);


        List<List<Integer>> battleLog = GameBalanceUtil.getBattleLog(playerWin, playerDmg, playerHp, playerCombatPoint, npcRank);


        // Builder 패턴으로 CreateGameBattleRequest 구성
        CreateGameBattleRequest fastApiRequest = CreateGameBattleRequest.builder()
                .turnCount(battleLog.size())
                .worldView(gameSessionMongo.getHistoryInfo().getWorldView())
                .location(gameSessionMongo.getLocation())
                .playerName(gameSessionMongo.getPlayerInfo().getName())
                .playerTrait(gameSessionMongo.getPlayerInfo().getTrait())
                .playerDmg(playerWeaponDmg)
                .playerWeapon(weapon != null ? mapToItemEffect(weapon) : null)
                .playerArmor(armor != null ? mapToItemEffect(armor) : null)
                .playerArtifact(artifact != null ? mapToItemEffect(artifact) : null)
                .npcName(gameSessionMongo.getNpcInfo().getName())
                .npcTrait(gameSessionMongo.getNpcInfo().getTrait())
                .npcDmg(npcCombatPoint)
                .npcWeapon(gameSessionMongo.getNpcInfo().getNpcWeaponName())
                .npcWeaponDescription(gameSessionMongo.getNpcInfo().getNpcWeaponDescription())
                .battleResult(playerWin)
                .hpLog( battleLog )
                .build();


        System.out.println("플레이어 공격력 = " + playerDmg + "플레이어 체력 = " + playerHp + " npc 공격력 = " + npcCombatPoint + " npc 체력 = " + GameBalanceUtil.getNpcHealthPoint(npcRank));
        System.out.println("전투로그 = " + battleLog + "   턴 = " + battleLog.size() + " 이긴사람 = " + playerWin);

        CreateGameBattleResponse fastApiResponse = fastApiService.battle(fastApiRequest);

        if (fastApiResponse == null) {
            throw new CustomException(ErrorCode.E_500_EXTERNAL_API_ERROR);
        }


        List<BattleStoryMongo> turnLogs = fastApiResponse.getBattleInfo().getTurnInfo()
                .stream()
                .map(info -> BattleStoryMongo.builder().turnInfo(info).build())
                .toList();

        BattleInfoMongo battleInfoMongo = BattleInfoMongo.builder()
                .curTurnId(0L)
                .playerHp(battleLog.stream().map(t -> t.get(0).longValue()).toList())
                .enemyHp(battleLog.stream().map(t -> t.get(1).longValue()).toList())
                .battleTurn(turnLogs)
                .playerWin( playerWin == 1 )
                .build();


        gameSessionMongo.setBattleInfo(battleInfoMongo);
        gameSessionMongo.setBackground(fastApiResponse.getBattleInfo().getReCap());
        gameSessionMongo.setSceneType(SceneType.BATTLE);
        gameSessionMongo.setUpdatedAt(LocalDateTime.now());


        gameSessionMongoRepository.save(gameSessionMongo);

        return playerWin;
    }


    @Transactional
    public GameSessionMongo gameToDone(Long userId) {
        if (!gameSessionRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.E_404_STORED_GAME_NOT_FOUND);
        }

        GameSession gameSession = gameSessionRepository.findByMongoId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        String gameId = gameSession.getMongoId();
        GameSessionMongo gameSessionMongo = gameSessionMongoRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));


        CreateGameDoneRequest fastApiRequest = CreateGameDoneRequest.builder()
                .worldView(gameSessionMongo.getHistoryInfo().getWorldView())
                .location(gameSessionMongo.getLocation())
                .previousStory(gameSessionMongo.getBackground())
                .selectedChoice(gameSessionMongo.getPreChoice())
                .resultContent(RewardType.getRewardSummary(gameSessionMongo.getRewardInfo()))
                .playerName(gameSessionMongo.getPlayerInfo().getName())
                .playerVictory( (gameSessionMongo.getSceneType() == SceneType.BATTLE))
                .build();


        CreateGameDoneResponse fastApiResponse = fastApiService.done(fastApiRequest);


        if (fastApiResponse == null) {
            throw new CustomException(ErrorCode.E_500_EXTERNAL_API_ERROR);
        }


        gameSessionMongo.setSceneType(SceneType.DONE);
        gameSessionMongo.setUpdatedAt(LocalDateTime.now());
        gameSessionMongo.setLocation(fastApiResponse.getDoneInfo().getNewLocation());
        gameSessionMongo.setBackground(fastApiResponse.getDoneInfo().getReCap());
        gameSessionMongo.setProgress(gameSessionMongo.getProgress() + 1);


        int currentProgress = gameSessionMongo.getProgress();
        List<Integer> stage = gameSessionMongo.getStage();
        int currentEventStage = stage.get(currentProgress);
        HistoryInfoMongo historyInfoMongo = gameSessionMongo.getHistoryInfo();

        if (currentEventStage > 0) {
            switch (currentEventStage) {
                case 1:
                case 2:
                    historyInfoMongo.setEpilogue1Content(fastApiResponse.getDoneInfo().getReCap());
                    break;
                case 3:
                case 4:
                    historyInfoMongo.setEpilogue2Content(fastApiResponse.getDoneInfo().getReCap());
                    break;
                case 5:
                case 6:
                    historyInfoMongo.setEpilogue3Content(fastApiResponse.getDoneInfo().getReCap());
                    break;
            }
        }

        /**
         * 
         * reward가 있으면 그대로 현재를 갱신한 후 mongoDB 저장
         */
        PlayerInfoMongo playerInfoMongo = gameSessionMongo.getPlayerInfo();
        RewardInfoMongo rewardInfoMongo = gameSessionMongo.getRewardInfo();
        List<InventoryMongo> inventory = gameSessionMongo.getInventory();
        List<String> inGameItem = gameSessionMongo.getCreatedItems();


        playerInfoMongo = GameBalanceUtil.updateReward(playerInfoMongo, rewardInfoMongo);
        inventory = GameBalanceUtil.updateRewardItem(inventory, rewardInfoMongo);
        inGameItem = GameBalanceUtil.updateInGameItem(inGameItem, rewardInfoMongo);

        gameSessionMongo.setPlayerInfo(playerInfoMongo);
        gameSessionMongo.setInventory(inventory);
        gameSessionMongo.setCreatedItems(inGameItem);


        gameSessionMongo.setHistoryInfo(historyInfoMongo);
        gameSessionMongoRepository.save(gameSessionMongo);

        return gameSessionMongo;
    }



    @Transactional
    public GameSessionMongo gameChoiceSelect(Long userId, GameChoiceRequest request) {
        if (!gameSessionRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.E_404_STORED_GAME_NOT_FOUND);
        }

        GameSession gameSession = gameSessionRepository.findByMongoId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));

        String gameId = gameSession.getMongoId();
        GameSessionMongo gameSessionMongo = gameSessionMongoRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));

        Stat userChoiceStat = null;
        Integer probability = null;

        Integer choiceIndex = request.getChoiceIndex();
        choiceIndex = (choiceIndex == null) ? 3 : choiceIndex;
        ChoiceMongo choiceMongo = gameSessionMongo.getChoiceInfo().getChoice().get(choiceIndex);

        if (request.getChoiceIndex() == null) {
            // 사용자 프롬프트 입력 → FAST API 호출해야 함
        } else {
            userChoiceStat = choiceMongo.getStats();
            probability = GameBalanceUtil.getChoiceProbability(userChoiceStat, gameSessionMongo.getPlayerInfo());
        }

        RewardType rewardType = choiceMongo.getRewardType();
        ChoiceResultType nextScene = choiceMongo.getResultType();
        boolean isPass = GameBalanceUtil.isPass(probability);

        RewardInfoMongo rewardInfo;

        switch (nextScene) {
            case CHOICE -> {
                // 보상 없음 → 그냥 다음 Choice 리턴
                return gameToChoice(userId);
            }
            case BATTLE -> {
                isPass = (gameToBattle(userId) == 1);
                gameSessionMongo = gameSessionMongoRepository.findById(gameId).get();
                rewardInfo = handleReward(gameSessionMongo, rewardType, isPass);


            }
            case DONE -> {
                gameToDone(userId);

                gameSessionMongo = gameSessionMongoRepository.findById(gameId).get();
                rewardInfo = handleReward(gameSessionMongo, rewardType, isPass);
            }
            default -> throw new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND);
        }

        // 보상 저장
        gameSessionMongo.setRewardInfo(rewardInfo);
        return gameSessionMongoRepository.save(gameSessionMongo);
    }




    @Transactional
    public GameSessionMongo gameEquipItem(Long userId, String itemId) {
        // 1. 게임 세션 조회
        GameSession gameSession = gameSessionRepository.findByMongoId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));

        GameSessionMongo gameSessionMongo = gameSessionMongoRepository.findById(gameSession.getMongoId())
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));

        PlayerInfoMongo playerInfo = gameSessionMongo.getPlayerInfo();
        List<InventoryMongo> inventory = gameSessionMongo.getInventory();

        // 2. 장착하려는 아이템 가져오기 (기존 ID 유지)
        InventoryMongo targetInventory = inventory.stream()
                .filter(inv -> inv.getItemDefId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_ITEM_NOT_FOUND));

        ItemDefMongo targetDef = itemDefMongoRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_ITEM_NOT_FOUND));

        ItemType category = targetDef.getCategory();

        // 3. Toggle: 이미 장착되어 있으면 해제
        if (targetInventory.isEquipped()) {
            targetInventory.setEquipped(false);
            removeStats(playerInfo, targetDef);
            return gameSessionMongoRepository.save(gameSessionMongo);
        }

        // 4. 같은 카테고리 장착 아이템 찾기
        InventoryMongo currentlyEquipped = inventory.stream()
                .filter(InventoryMongo::isEquipped)
                .filter(inv -> {
                    ItemDefMongo def = itemDefMongoRepository.findById(inv.getItemDefId()).orElse(null);
                    return def != null && def.getCategory() == category;
                })
                .findFirst()
                .orElse(null);

        // 5. 기존 장착 해제 및 스탯 제거
        if (currentlyEquipped != null) {
            ItemDefMongo oldDef = itemDefMongoRepository.findById(currentlyEquipped.getItemDefId())
                    .orElseThrow(() -> new CustomException(ErrorCode.E_404_ITEM_NOT_FOUND));
            currentlyEquipped.setEquipped(false);
            removeStats(playerInfo, oldDef);
        }

        // 6. 새 아이템 장착 및 스탯 적용
        targetInventory.setEquipped(true);
        addStats(playerInfo, targetDef);

        // 7. 저장 (ID 유지)
        return gameSessionMongoRepository.save(gameSessionMongo);
    }

    // 스탯 더하기
    private void addStats(PlayerInfoMongo player, ItemDefMongo item) {
        player.setStrength(player.getStrength() + safeStat(item.getStrength()));
        player.setAgility(player.getAgility() + safeStat(item.getAgility()));
        player.setIntelligence(player.getIntelligence() + safeStat(item.getIntelligence()));
        player.setLuck(player.getLuck() + safeStat(item.getLuck()));
    }

    // 스탯 빼기
    private void removeStats(PlayerInfoMongo player, ItemDefMongo item) {
        player.setStrength(player.getStrength() - safeStat(item.getStrength()));
        player.setAgility(player.getAgility() - safeStat(item.getAgility()));
        player.setIntelligence(player.getIntelligence() - safeStat(item.getIntelligence()));
        player.setLuck(player.getLuck() - safeStat(item.getLuck()));
    }

    // null-safe 처리
    private int safeStat(Integer stat) {
        return stat != null ? stat : 0;
    }




    /**
     * battle에서 사용 
     * item -> request로 쉽게 매필
     */
    // ItemDefMongo -> CreateGameBattleRequest.Item 변환
    private CreateGameBattleRequest.Item mapToItemEffect(ItemDefMongo item) {
        List<CreateGameBattleRequest.Item.ItemEffect> effects = item.getItemEffect().stream()
                .map(e -> CreateGameBattleRequest.Item.ItemEffect.builder()
                        .name(e.getItemEffectName())
                        .description(e.getItemEffectDescription())
                        .build())
                .toList();

        return CreateGameBattleRequest.Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .effects(effects)
                .build();
    }


    private ItemDefMongo convertToItemDefMongo(ItemFastApiResponse response) {
        List<ItemEffectMongo> effects = new ArrayList<>();

        if (response.getItemEffect() != null) {
            for (ItemFastApiResponse.ItemEffect e : response.getItemEffect()) {
                ItemEffectMongo effectMongo = ItemEffectMongo.builder()
                        .itemEffectName(e.getItemEffectName())
                        .itemEffectDescription(e.getItemEffectDescription())
                        .build();

                effects.add(effectMongo);
            }
        }

        return ItemDefMongo.builder()
                .name(response.getItemName())
                .description(response.getItemDescription())
                .itemEffect(effects)
                .build();
    }


    /**
     * 보상 처리 (ITEM 포함)
     */
    private RewardInfoMongo handleReward(GameSessionMongo gameSessionMongo, RewardType rewardType, boolean isPass) {
        RewardInfoMongo rewardInfo = GameBalanceUtil.getReward(rewardType, isPass);

        if (rewardType == RewardType.ITEM && isPass) {
            ItemDefRequest itemDefRequest = ItemDefRequest.builder()
                    .worldView(gameSessionMongo.getHistoryInfo().getWorldView())
                    .location(gameSessionMongo.getLocation())
                    .playerTrait(null)
                    .previousStory(gameSessionMongo.getBackground())
                    .build();

            String itemMongoId = itemDefService.createItem(itemDefRequest);
            List<String> gainItemList = rewardInfo.getGainedItemDefId();
            if (gainItemList == null) {
                gainItemList = new ArrayList<>(); // null이면 새 리스트 생성
            }
            gainItemList.add(itemMongoId);
            rewardInfo.setGainedItemDefId(gainItemList);
        }
        return rewardInfo;
    }
}
