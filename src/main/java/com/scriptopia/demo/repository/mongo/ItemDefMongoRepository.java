package com.scriptopia.demo.repository.mongo;

import com.scriptopia.demo.domain.ItemDef;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemDefMongoRepository extends MongoRepository<ItemDef, String> {
}
