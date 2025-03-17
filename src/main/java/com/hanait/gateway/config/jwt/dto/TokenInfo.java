package com.hanait.gateway.config.jwt.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * 토큰의 정보를 담고 있는 클래스
 */
@Data
@ToString() //accessToken이 너무 길기때문에 출력시 로그가 너무 길어져 accessToken은 제외
public class TokenInfo {
    private String accessToken;
    private Date accessTokenExpireTime;
    private String accessTokenId;
    private String refreshToken;
    private Date refreshTokenExpireTime;
    private String refreshTokenId;
    private String ownerEmail; //소유자의 이메일

    @Builder
    public TokenInfo(String accessToken, Date accessTokenExpireTime, String accessTokenId, String refreshToken, Date refreshTokenExpireTime, String refreshTokenId, String ownerEmail) {
        this.accessToken = accessToken;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.accessTokenId = accessTokenId;
        this.refreshToken = refreshToken;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
        this.refreshTokenId = refreshTokenId;
        this.ownerEmail = ownerEmail;
    }
}
