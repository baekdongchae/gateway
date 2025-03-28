package com.hanait.gateway.config.jwt.blacklist;

import com.hanait.gateway.config.jwt.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenList {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenTimeoutInSeconds;

    public void saveRefreshToken(String key, Object o) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(o.getClass()));
        redisTemplate.opsForValue().set(key, o, refreshTokenTimeoutInSeconds * 1000, TimeUnit.MILLISECONDS);

        // 디버깅용 로그 추가
        log.info("Refresh Token 저장: key={}, value={}", key, o);

        // Redis에 실제로 저장되었는지 조회
        Object savedValue = redisTemplate.opsForValue().get(key);
        log.info("저장된 Refresh Token 확인: key={}, value={}", key, savedValue);
    }

    public boolean isRefreshTokenList(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
