package com.hanait.gateway.config.jwt.blacklist;

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
        redisTemplate.opsForValue().set(key, o, refreshTokenTimeoutInSeconds * 10000, TimeUnit.MILLISECONDS);
    }

    public boolean isRefreshTokenList(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
