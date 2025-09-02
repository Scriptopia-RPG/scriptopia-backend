package com.scriptopia.demo.repository.mongo;

import com.scriptopia.demo.domain.mongo.GameSessionMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GameSessionMongoRepository extends MongoRepository<GameSessionMongo, String> {


    // userId로 진행 중인 게임 조회
    Optional<GameSessionMongo> findByUserIdAndSceneTypeNot(String userId, String sceneType);
}