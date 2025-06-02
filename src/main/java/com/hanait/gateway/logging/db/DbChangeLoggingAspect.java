package com.hanait.gateway.logging.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanait.gateway.logging.api.ApiLogContext;
import com.hanait.gateway.repository.MongoDbChangeLogRepository;
import com.hanait.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DbChangeLoggingAspect {

    private final ObjectMapper objectMapper;
    private final MongoDbChangeLogRepository mongoDbChangeLogRepository;

    private final UserRepository userRepository;

    @Around("@annotation(logDbChange)")
    public Object logDbChange(ProceedingJoinPoint joinPoint, LogDbChange logDbChange) throws Throwable {
        String tableName = logDbChange.table();
        String operation = logDbChange.operation();

        Object id = extractId(joinPoint);
        Object previous = fetchPreviousData(tableName, id);

        Object result = joinPoint.proceed();

        Object changed = fetchChangedData(tableName, id);

        MongoDbChangeLog logEntry = MongoDbChangeLog.builder()
                .uuid(ApiLogContext.get() != null ? ApiLogContext.get().getUuid() : UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .requestUserCode(ApiLogContext.get() != null ? ApiLogContext.get().getRequestUserCode() : null)
                .dbTable(tableName)
                .operation(operation)
                .previousData(objectMapper.convertValue(previous, Map.class))
                .changedData(objectMapper.convertValue(changed, Map.class))
                .build();

        mongoDbChangeLogRepository.save(logEntry);
        log.info("[DB_LOG] {} - {}", operation, tableName);

        return result;
    }

    private Object extractId(ProceedingJoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Long || arg instanceof String || arg instanceof Integer)
                .findFirst().orElse(null);
    }

    private Object fetchPreviousData(String tableName, Object id) {
        if ("user".equalsIgnoreCase(tableName)) {
            return userRepository.findById(Long.parseLong(id.toString())).orElse(null);
        }
        return null;
    }

    private Object fetchChangedData(String tableName, Object id) {
        return fetchPreviousData(tableName, id); // 동일하게 처리
    }
}
