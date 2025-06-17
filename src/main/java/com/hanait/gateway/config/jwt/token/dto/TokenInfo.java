package com.hanait.gateway.config.jwt.token.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Date;

/**
 * 토큰의 정보를 담고 있는 클래스
 */
@Data
@ToString() //accessToken이 너무 길기때문에 출력시 로그가 너무 길어져 accessToken은 제외
@NoArgsConstructor
public class TokenInfo {

    private Long userCode;
    private String accessToken;
    private Date accessTokenExpireTime;
    private String accessTokenId;
    private String refreshToken;
    private Date refreshTokenExpireTime;
    private String refreshTokenId;
    private String ownerId; //소유자의 이메일
    // requestId는 Builder 패턴으로 생성하지 않고 별도로 설정
    @Setter
    private String requestId; // API 요청 추적을 위한 고유 UUID
    private String IpAdd;

    @Builder
    public TokenInfo(Long userCode, String accessToken, Date accessTokenExpireTime, String accessTokenId, String refreshToken,
                     Date refreshTokenExpireTime, String refreshTokenId, String ownerId, String IpAdd) {
        this.userCode = userCode;
        this.accessToken = accessToken;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.accessTokenId = accessTokenId;
        this.refreshToken = refreshToken;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
        this.refreshTokenId = refreshTokenId;
        this.ownerId = ownerId;
        this.IpAdd = IpAdd;
    }

}
