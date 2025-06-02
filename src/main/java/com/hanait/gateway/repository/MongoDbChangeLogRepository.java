package com.hanait.gateway.repository;

import com.hanait.gateway.logging.db.MongoDbChangeLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDbChangeLogRepository extends MongoRepository<MongoDbChangeLog, String> {
}
