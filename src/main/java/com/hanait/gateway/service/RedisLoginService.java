package com.hanait.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisLoginService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long ATTEMPT_TTL = 10 * 60; // 10분
    private static final long BLOCK_TTL = 30 * 60;   // 30분

    private final RedisTemplate<String, Integer> integerRedisTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisLoginService(RedisTemplate<String, Integer> integerRedisTemplate,
                             @Qualifier("customStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.integerRedisTemplate = integerRedisTemplate;
        this.redisTemplate = redisTemplate;
    }

    public int increaseSignInAttempts(Long userCode) {
        String key = "login:attempts:" + userCode;

        Integer attempts = integerRedisTemplate.opsForValue().get(key);
        log.debug("[Redis] 현재 로그인 시도 횟수 (기존): {} - userCode: {}", attempts, userCode);

        if (attempts == null) attempts = 0;
        attempts += 1;

        integerRedisTemplate.opsForValue().set(key, attempts, ATTEMPT_TTL, TimeUnit.SECONDS);
        log.info("[Redis] 로그인 시도 [{}회] 저장 및 TTL({}초) 설정 - userCode: {}", attempts, ATTEMPT_TTL, userCode);

        if (attempts >= MAX_ATTEMPTS) {
            blockUser(userCode);
        }

        return attempts;  // 카운트 반환
    }

    public boolean isUserBlocked(Long userCode) {
        String key = "login:block:" + userCode;
        boolean hasBlockKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));

        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        String value = redisTemplate.opsForValue().get(key);

        log.debug("[Redis] 차단 키 존재 여부: {}, TTL: {}, 값: {} - userCode: {}", hasBlockKey, ttl, value, userCode);
        return hasBlockKey;
    }

    public void blockUser(Long userCode) {
        String key = "login:block:" + userCode;
        redisTemplate.opsForValue().set(key, "BLOCKED", BLOCK_TTL, TimeUnit.SECONDS);
        log.warn("[Redis] 사용자 차단 설정 완료 - userCode: {}, TTL: {}초", userCode, BLOCK_TTL);
    }

    public void clearSignInAttempts(String userId) {
        String attemptKey = "login:attempts:" + userId;
        String blockKey = "login:block:" + userId;

        boolean wasBlocked = Boolean.TRUE.equals(redisTemplate.hasKey(blockKey));
        integerRedisTemplate.delete(attemptKey);
        redisTemplate.delete(blockKey);

        log.info("[Redis] 로그인 시도 횟수 및 차단 키 삭제 - userId: {}", userId);
        if (wasBlocked) {
            log.info("[Redis] 사용자 차단 해제 기록 - userId: {}", userId);
        }
    }
}



