package com.hanait.gateway.config.jwt;

import com.hanait.gateway.config.jwt.token.dto.TokenInfo;
import com.hanait.gateway.config.jwt.token.dto.TokenValidationResult;
import com.hanait.gateway.config.jwt.token.TokenProvider;
import com.hanait.gateway.config.jwt.token.TokenStatus;
import com.hanait.gateway.config.jwt.token.TokenType;
import com.hanait.gateway.logging.api.ApiLogContext;
import com.hanait.gateway.logging.api.ApilogData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization"; //header에서 token 정보를 추천해준다.
    private static final String BEARER_REGEX = "Bearer ([a-zA-Z0-9_\\-\\+\\/=]+)\\.([a-zA-Z0-9_\\-\\+\\/=]+)\\.([a-zA-Z0-9_.\\-\\+\\/=]*)"; //jwt token은 .을 기준으로 header, payload, signature로 나뉘어져있어 정규표현식을 통해 1차적으로 jwt token이 유효한지 확인한다.
    private static final Pattern BEARER_PATTERN = Pattern.compile(BEARER_REGEX);

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("들어온 요청 URI: {}", request.getRequestURI());
        log.info("Authorization : {}", request.getHeader("Authorization"));

        String token = resolveToken(request);
        log.info("🔍 추출된 토큰: {}", token);

        if(token == null){
            filterChain.doFilter(request, response);
            return;
        }

        TokenValidationResult tokenValidationResult = tokenProvider.validateToken(token);
        log.info("🔍 토큰 검증 결과: {}", tokenValidationResult != null ? tokenValidationResult.getTokenStatus() : "null");

        if (tokenValidationResult == null || tokenValidationResult.getTokenType() == null) {
            handleInvalidToken(request, response, filterChain);
            return;
        }

        if (tokenValidationResult.getTokenType().equals(TokenType.ACCESS)) {
            if (tokenProvider.isAccessTokenBlackList(token)) {
                handleBlackListToken(request, response, filterChain);
                return;
            }
            handleValidToken(token, tokenValidationResult);
            filterChain.doFilter(request, response);

        } else if (tokenValidationResult.getTokenType().equals(TokenType.REFRESH)) {
            if (tokenProvider.isRefreshTokenList(token)) {
                handleRefreshToken(request, response, tokenValidationResult, filterChain);
            } else {
                handleInvalidToken(request, response, filterChain);
            }
        } else {
            handleWrongTypeToken(request, response, filterChain);
        }
    }

    private void handleBlackListToken(HttpServletRequest request, HttpServletResponse response,
                                      FilterChain filterChain) throws ServletException, IOException {
        request.setAttribute("result", new TokenValidationResult(TokenStatus.TOKEN_IS_BLACKLIST, null, null, null));
        filterChain.doFilter(request, response);
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response,
                                    TokenValidationResult tokenValidationResult, FilterChain filterChain) throws IOException, ServletException {

        TokenInfo newAccessTokenInfo = tokenProvider.recreateAccessToken(tokenValidationResult.getClaims());
        log.info("Issued new access token: {}", newAccessTokenInfo.getAccessToken());

        request.setAttribute("result", new TokenValidationResult(TokenStatus.TOKEN_REFRESHED, null, null, null, newAccessTokenInfo));
        throw new BadCredentialsException("Access token refreshed"); // EntryPoint 진입 유도
    }

    private static void handleInvalidToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setAttribute("result", new TokenValidationResult(TokenStatus.TOKEN_VALIDATION_TRY_FAILED, null, null, null));
        filterChain.doFilter(request, response);
    }

    private static void handleWrongTypeToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setAttribute("result", new TokenValidationResult(TokenStatus.TOKEN_WRONG_TYPE, null, null, null));
        filterChain.doFilter(request, response);
    }

    private void handleValidToken(String token, TokenValidationResult tokenValidationResult) {
        //사용자 인증을 처리하기 위해 securityContext에 authentication을 넣어줘야 한다.
        Authentication authentication = tokenProvider.getAuthentication(token, tokenValidationResult.getClaims());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // ✅ ApilogDataContext에 사용자 정보 저장
        ApilogData logData = ApiLogContext.get();
        if (logData != null) {
            logData.setRequestUserCode(authentication.getName()); // 보통 userId
            logData.setTokenHash(DigestUtils.md5DigestAsHex(token.getBytes())); // 토큰 Hash 저장
        }
        log.info("✅ 인증 성공 - 사용자 ID: {}, 권한: {}", authentication.getName(), authentication.getAuthorities());
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); //Authorization header에서 value를 꺼내준다.

        if(bearerToken != null && BEARER_PATTERN.matcher(bearerToken).matches()) { //header에 토큰 값이 존재하고, 정규표현식으로 검사했을 때 토큰에 문제가 없는 경우
            return bearerToken.substring(7); //토큰 값에 bearer만 떼준 다음 return
        }

        return null;
    }

}
