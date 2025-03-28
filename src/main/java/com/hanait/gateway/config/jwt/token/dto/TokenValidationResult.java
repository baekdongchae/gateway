package com.hanait.gateway.config.jwt.token.dto;

import com.hanait.gateway.config.jwt.token.TokenStatus;
import com.hanait.gateway.config.jwt.token.TokenType;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TokenValidationResult {
    private TokenStatus tokenStatus; //토큰 상태
    private TokenType tokenType; //토큰 타입
    private String tokenId; //토큰 id
    private Claims claims;
    private TokenInfo tokenInfo = null;

    public TokenValidationResult(TokenStatus tokenStatus, TokenType tokenType, String tokenId, Claims claims) {
        this.tokenStatus = tokenStatus;
        this.tokenType = tokenType;
        this.tokenId = tokenId;
        this.claims = claims;
    }

    public TokenValidationResult(TokenStatus tokenStatus, TokenType tokenType, String tokenId, Claims claims, TokenInfo tokenInfo) {
        this.tokenStatus = tokenStatus;
        this.tokenType = tokenType;
        this.tokenId = tokenId;
        this.claims = claims;
        this.tokenInfo = tokenInfo;
    }

    public String getEmail() {
        if (claims == null) {
            throw new IllegalStateException("Claim value is null");
        }
        return claims.getSubject(); //토큰 소유자의 이메일 return
    }


    public boolean isValid() {
        return TokenStatus.TOKEN_VALID == this.tokenStatus; //toeknStauts가 정상 토큰이면 true return
    }
}
