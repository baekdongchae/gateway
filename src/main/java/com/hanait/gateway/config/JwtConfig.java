package com.hanait.gateway.config;

import com.hanait.gateway.config.jwt.JwtAccessDeniedHandler;
import com.hanait.gateway.config.jwt.JwtAuthenticationEntryPoint;
import com.hanait.gateway.config.jwt.JwtProperties;
import com.hanait.gateway.config.jwt.blacklist.AccessTokenBlackList;
import com.hanait.gateway.config.jwt.blacklist.RefreshTokenList;
import com.hanait.gateway.config.jwt.token.TokenProvider;
import com.hanait.gateway.service.TokenLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    private final AccessTokenBlackList accessTokenBlackList;
    private final RefreshTokenList refreshTokenList;
    private final TokenLogService tokenLogService;


    @Bean
    public TokenProvider tokenProvider(JwtProperties jwtProperties) {
        return new TokenProvider(jwtProperties.getSecret(), jwtProperties.getAccessTokenValidityInSeconds(),
                jwtProperties.getRefreshTokenValidityInSeconds(), accessTokenBlackList, refreshTokenList, tokenLogService);
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }
}
