package com.scriptopia.demo.repository.mongo;

import com.scriptopia.demo.domain.mongo.ItemDefMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemDefMongoRepository extends MongoRepository<ItemDefMongo, String> {
}
