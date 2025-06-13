package com.hanait.gateway.logging.db;

import com.hanait.gateway.model.User;
import com.hanait.gateway.principle.UserPrinciple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginLogger {

    private final MongoTemplate mongoTemplate;

    public void logSuccess(String uuid, Long userCode, String clientIp) {
        Document doc = new Document()
                .append("uuid", uuid)
                .append("request_user_code", userCode)
                .append("count", 0)
                .append("blocked", false)
                .append("block_reason", null)
                .append("timestamp", LocalDateTime.now().toString())
                .append("block_time", null)
                .append("client_ip", clientIp);

        mongoTemplate.getCollection("login_block").insertOne(doc);
        log.info("MongoDB 로그인 성공 기록 저장 - userCode: {}", userCode);
    }

    public void logFailure(String uuid, Long userCode, int count, String clientIp) {
        Document doc = new Document()
                .append("uuid", uuid)
                .append("request_user_code", userCode)
                .append("count", count)
                .append("blocked", false)
                .append("block_reason", null)
                .append("timestamp", LocalDateTime.now().toString())
                .append("block_time", null)
                .append("client_ip", clientIp);

        mongoTemplate.getCollection("login_block").insertOne(doc);
        log.info("MongoDB 로그인 실패 기록 저장 - userCode: {}, count: {}", userCode, count);
    }

    public void logBlocked(String uuid, Long userCode, int count, String clientIp) {
        Document doc = new Document()
                .append("uuid", uuid)
                .append("request_user_code", userCode)
                .append("count", count)
                .append("blocked", true)
                .append("block_reason", "로그인 5회 실패")
                .append("timestamp", LocalDateTime.now().toString())
                .append("block_time", LocalDateTime.now().toString())
                .append("client_ip", clientIp);

        mongoTemplate.getCollection("login_block").insertOne(doc);
        log.warn("MongoDB 로그인 차단 기록 저장 - userCode: {}", userCode);
    }


}
