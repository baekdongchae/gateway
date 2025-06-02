package com.hanait.gateway.config.jwt.token;

import com.hanait.gateway.config.jwt.blacklist.AccessTokenBlackList;
import com.hanait.gateway.config.jwt.blacklist.RefreshTokenList;
import com.hanait.gateway.config.jwt.token.dto.TokenInfo;
import com.hanait.gateway.config.jwt.token.dto.TokenValidationResult;
import com.hanait.gateway.model.User;
import com.hanait.gateway.model.Role;
import com.hanait.gateway.principle.UserPrinciple;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 토큰을 생성하고 검증하는 클래스
 */
@Slf4j
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_ID_KEY = "tokenId";

    private static final String TOKEN_TYPE = "tokenType";

    private final Key hashKey;
    private final long accessTokenValidationInMilliseconds;
    private final long refreshTokenValidationInMilliseconds;

    private final AccessTokenBlackList accessTokenBlackList;
    private final RefreshTokenList refreshTokenList;

    public TokenProvider(String secret, long accessTokenValidationInSeconds, long refreshTokenValidationInMilliseconds,
                         AccessTokenBlackList accessTokenBlackList, RefreshTokenList refreshTokenList) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.hashKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidationInMilliseconds = accessTokenValidationInSeconds * 1000;
        this.refreshTokenValidationInMilliseconds = refreshTokenValidationInMilliseconds * 1000;
        this.accessTokenBlackList = accessTokenBlackList;
        this.refreshTokenList = refreshTokenList;
    }

    public boolean isAccessTokenBlackList(String accessToken) {
        if (accessTokenBlackList.isTokenBlackList(accessToken)) {
            log.info("BlackListed Access Token");

            // 블랙리스트에서 해당 Access Token의 값을 조회
            Object value = accessTokenBlackList.getBlackList(accessToken);
            log.info("블랙리스트 조회 결과: key={}, value={}", accessToken, value);

            return true;
        }
        return false;
    }

    public boolean isRefreshTokenList(String refreshToken) {
        if (refreshTokenList.isRefreshTokenList(refreshToken)) {
            log.info("BlackListed refreshToken Token");
            return true;
        }
        return false;
    }

    //토큰생성
    public TokenInfo createToken(User user) {

        long currentTime = (new Date()).getTime();

        //Access 토큰 발급
        Date accessTokenExpireTime = new Date(currentTime + this.accessTokenValidationInMilliseconds);
        String accessTokenId = UUID.randomUUID().toString();
        String accessToken = issueToken(user.getUserId(), TokenType.ACCESS, Role.ROLE_USER, accessTokenId, accessTokenExpireTime);

        //Refresh 토큰 발급
        Date refreshTokenExpireTime = new Date(currentTime + this.refreshTokenValidationInMilliseconds);
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = issueToken(user.getUserId(), TokenType.REFRESH, Role.ROLE_USER, refreshTokenId, refreshTokenExpireTime);

        refreshTokenList.saveRefreshToken(refreshToken, refreshTokenId);

        return TokenInfo.builder()
                .ownerId(user.getUserId())
                .accessToken(accessToken)
                .accessTokenExpireTime(accessTokenExpireTime)
                .accessTokenId(accessTokenId)
                .refreshToken(refreshToken)
                .refreshTokenExpireTime(refreshTokenExpireTime)
                .refreshTokenId(refreshTokenId).build();
    }

    private String issueToken(String userId, TokenType tokenType, Role role, String tokenId, Date tokenExpireTime) {
        return Jwts.builder()
                .setSubject(userId)
                .claim(TOKEN_TYPE, tokenType)
                .claim(AUTHORITIES_KEY, role.name())
                .claim(TOKEN_ID_KEY, tokenId)
                .signWith(hashKey, SignatureAlgorithm.HS512)
                .setExpiration(tokenExpireTime)
                .compact();
    }

    public TokenInfo recreateAccessToken(Claims claims) {
        String userId = claims.getSubject();
        String refreshTokenId = claims.get(TOKEN_ID_KEY, String.class);

        // 새로운 Access Token 발급
        long currentTime = (new Date()).getTime();
        Date accessTokenExpireTime = new Date(currentTime + this.accessTokenValidationInMilliseconds);
        String accessTokenId = UUID.randomUUID().toString();

        String newAccessToken = issueToken(userId, TokenType.ACCESS, Role.ROLE_USER, accessTokenId, accessTokenExpireTime);

        return TokenInfo.builder()
                .ownerId(userId)
                .accessToken(newAccessToken)
                .accessTokenExpireTime(accessTokenExpireTime)
                .accessTokenId(accessTokenId)
                .refreshTokenId(refreshTokenId)  // 기존 Refresh Token ID 유지
                .build();
    }

    ////토큰 검증 후 TokenValidationResult로 검증 결과 return
    public TokenValidationResult validateToken(String token) {

        if (token == null || token.trim().isEmpty()) {
            log.debug("토큰이존재하지않음");
            return new TokenValidationResult(TokenStatus.TOKEN_WRONG_SIGNATURE, null, null, null);
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(hashKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new TokenValidationResult(TokenStatus.TOKEN_VALID, TokenType.ACCESS, claims.get(TOKEN_ID_KEY, String.class), claims);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰: {}", e.getMessage());
            return getExpiredTokenValidationResult(e);

        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명: {}", e.getMessage());
            return new TokenValidationResult(TokenStatus.TOKEN_WRONG_SIGNATURE, null, null, null);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰: {}", e.getMessage());
            return new TokenValidationResult(TokenStatus.TOKEN_HASH_NOT_SUPPORTED, null, null, null);
        } catch (IllegalArgumentException e) {
            log.info("잘못된 JWT 토큰: {}", e.getMessage());  // 예외 메시지 로그 추가
            return new TokenValidationResult(TokenStatus.TOKEN_WRONG_SIGNATURE, null, null, null);
        }
    }

    private static TokenValidationResult getExpiredTokenValidationResult(ExpiredJwtException e) {
        //만료된 토큰의 경우 토큰 자체는 정상이므로 claim들은 가져올 수 있다.
        Claims claims = e.getClaims();
        return new TokenValidationResult(TokenStatus.TOKEN_EXPIRED, TokenType.ACCESS,
                claims.get(TOKEN_ID_KEY, String.class), null);
    }

    // access 토큰과 claim을 전달받아 UsernamePasswordAuthenticationToken을 생성해 전달
    public Authentication getAuthentication(String token, Claims claims) {
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString()
                        .split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 커스텀한 UserPrinciple 객체 사용 -> 이후 추가적인 데이터를 토큰에 넣을 경우 UserPrinciple 객체 및 이 클래스의 함수들 수정 필요
        UserPrinciple principle = new UserPrinciple(claims.getSubject(), /*(claims.get(USERNAME_KEY, String.class),*/
                authorities);

        return new UsernamePasswordAuthenticationToken(principle, token, authorities);
    }

    public String extractAccessTokenFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getCredentials() instanceof String token)) {
            log.warn("SecurityContext에 인증 정보가 없거나 잘못된 타입입니다.");
            return null;
        }
        return token;
    }
}

