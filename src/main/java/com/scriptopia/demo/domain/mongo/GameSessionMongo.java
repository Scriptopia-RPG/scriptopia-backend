package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.SceneType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "game_sessions")
@AllArgsConstructor
@NoArgsConstructor
public class GameSessionMongo {

    @Id
    private String id; // MongoDB 기본키

    private Long userId; // MySQL 사용자 ID

    private SceneType sceneType; // battle, choice, shop, done

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
