package com.hanait.gateway.config.jwt.token;

import com.hanait.gateway.config.jwt.blacklist.AccessTokenBlackList;
import com.hanait.gateway.config.jwt.blacklist.RefreshTokenList;
import com.hanait.gateway.config.jwt.token.dto.TokenInfo;
import com.hanait.gateway.config.jwt.token.dto.TokenValidationResult;
import com.hanait.gateway.model.Member;
import com.hanait.gateway.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TokenProviderTest {
    //512byte 이상의 key를 생성해야 한다.
    private final String secrete = "CGhpIGZhc2RmZCBuYW1lIGlzIGNoYXllb2l1bWcgZWFkZmEgYXNkZm5kIGltCGhpIG15IG5hYWRzZiBhZHNmZGZhc2QgZnNhZGZhc2QgbWUgaXMgY2hheWVvaXVtZyBlbmQgZHNhZmFkc2ZpbQoIaGlhIGFzZGFzZGZhc2RteSBuYWV3ZmFzZGZhZHNmYXNkZmFzZGZhc2RmYXNkZmFzZGZhc2RmYWZzZXdhcnFld3FyZXdxcmV3cWZhc2RkZmFzZGRhYWRzZm1lIGlzIGNoYXllb2l1bWcgZWFkZmEgYXNkZm5kIGltCGhpIG15IG5hYWRzZiBhZHNmZGZhc2QgZnNhZGZhc2QgbWUgaXMgY2hheWVvaXVtZyBlbmQgaW0KCGhkZmkgYW15IG5hbWUgaXMgY3NkYWZhc2RmaGF5ZW9pdW1nIGVhZGZhIGFzZGZuZCBpbQhoaSBteSBuYWFkc2YgYWRzZmRmYXNkIHNhZGZmc2FkZmFzZCBtZSBpcyBjaGF5ZW9pdW1nIGVuZCBpbQoIaGkgYXNkZmFzIGZteSBuYW1lIGlzIGNoYXllb2l1bWcgZWFkZmEgYXNkZm5kIGltCGhpIG15IG5hYWRzZiBhZHNmZGZhc2QgZnNhZGZhc2QgbWUgaWFzZGZzIGNoYXllb2l1bWcgZW5kIGltCgo=";
    private final Long accessTokenValidTimeInSeconds = 3L; //토큰 만료 시간 3초로 세팅
    private final long refreshTokenValidationInMilliseconds = 10L;


    private final TokenProvider tokenProvider = new TokenProvider(secrete, accessTokenValidTimeInSeconds);

    @Test
    void createToken() {
        Member member = getMember();

        TokenInfo token = tokenProvider.createToken(member);
        log.info("access token={}", token.getAccessToken());
    }

    @Test
    void validateTokenValid() {
        Member member = getMember();
        TokenInfo token = tokenProvider.createToken(member);
        String accessToken = token.getAccessToken();

        TokenValidationResult tokenValidationResult = tokenProvider.validateToken(accessToken);

        Assertions.assertThat(tokenValidationResult.isValid()).isTrue();
    }

    @Test
    void validateTokenNotValid() throws InterruptedException {
        Member member = getMember();
        TokenInfo token = tokenProvider.createToken(member);
        String accessToken = token.getAccessToken();

        //토큰 만료 시간이 3초이므로 4초 sleep
        Thread.sleep(4000);
        TokenValidationResult tokenValidationResult = tokenProvider.validateToken(accessToken);

        Assertions.assertThat(tokenValidationResult.isValid()).isFalse();
    }

    private Member getMember() {
        return Member.builder()
                .email("test@test.ac.kr")
                .password("1234")
                .username("test")
                .role(Role.ROLE_USER)
                .build();
    }
}