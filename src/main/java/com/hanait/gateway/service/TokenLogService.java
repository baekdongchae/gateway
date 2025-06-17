package com.hanait.gateway.service;

import com.hanait.gateway.logging.api.ApiLogContext;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TokenLogService {

    private final MongoTemplate mongoTemplate;

    public void saveTokenLog(Long userCode, String accessToken, String refreshTokenHash,
                             String logType, String reason) {
        Document document = new Document();
        document.put("uuid", ApiLogContext.get().getUuid());
        document.put("request_user_code", userCode);
        document.put("access_token", DigestUtils.md5DigestAsHex(accessToken.getBytes()));
        document.put("refresh_token_hash", refreshTokenHash);
        document.put("log_type", logType);
        document.put("timestamp", Instant.now());
        document.put("reason", reason);

        mongoTemplate.insert(document, "token_logs");
    }
}
