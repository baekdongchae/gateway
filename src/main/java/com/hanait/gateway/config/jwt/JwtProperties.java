package com.hanait.gateway.config.jwt;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml에 설정한 값들을 가져오기 위한 클래스
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String header;
    private String secret;
    private Long accessTokenValidityInSeconds;
    private Long refreshTokenValidityInSeconds;
}
