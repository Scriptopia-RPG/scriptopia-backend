package com.scriptopia.demo.domain.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "game_sessions")
public class GameSessionMongo {

    @Id
    private String id; // MongoDB 기본키

    private Long userId; // MySQL 사용자 ID

    private String sceneType; // battle, choice, shop, done

    private LocalDateTime startedAt;
    private LocalDateTime updatedAt;

    private String background;
    private Integer progress;
    private List<Integer> stage;

    private PlayerInfoMongo playerInfo;
    private List<InventoryItemMongo> inventory;
    private List<ItemDefMongo> itemDef;

    private ChoiceInfoMongo choiceInfo;
    private DoneInfoMongo doneInfo;
    private ShopInfoMongo shopInfo;
    private BattleInfoMongo battleInfo;
    private RewardInfoMongo rewardInfo;
    private HistoryInfoMongo historyInfo;
}
