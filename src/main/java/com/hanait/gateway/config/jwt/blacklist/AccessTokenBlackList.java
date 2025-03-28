package com.hanait.gateway.config.jwt.blacklist;

import com.hanait.gateway.config.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
public class AccessTokenBlackList {

    private final RedisTemplate<String, Object> redisBlackListTemplate;
    private final JwtProperties jwtProperties;

    @Value("${jwt.access-token-validity-in-seconds}")
    private Long accessTokenTimeoutInSeconds;

    public void setBlackList(String key, Object o) {
        Claims claims = Jwts.parserBuilder()
                .build()
                .parseClaimsJws(key)
                        .getBody();
        //Jackson2JsonRedisSerializer를 사용하여 객체 -> JSON(직렬화)
        redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(o.getClass()));
        //주어진 key, value를 redis에 저장하고 만료시간 설정
        redisBlackListTemplate.opsForValue().set(key, o, accessTokenTimeoutInSeconds * 1000, TimeUnit.MILLISECONDS);
    }

    public Object getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public boolean isTokenBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }
}
