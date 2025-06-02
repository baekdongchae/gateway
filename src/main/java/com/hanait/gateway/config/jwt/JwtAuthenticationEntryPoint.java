package com.hanait.gateway.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanait.gateway.config.jwt.token.dto.TokenInfo;
import com.hanait.gateway.config.jwt.token.dto.TokenValidationResult;
import com.hanait.gateway.config.jwt.status.ResponseStatusCode;
import com.hanait.gateway.config.jwt.token.TokenStatus;
import com.hanait.gateway.config.jwt.token.ApiResponseJson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.hanait.gateway.config.jwt.status.ResponseStatusCode.TOKEN_REFRESHED;
import static com.hanait.gateway.config.jwt.status.ResponseStatusCode.TOKEN_WRONG_TYPE;

/**
 * 인증(Authentication) 예외가 발생했을때 처리하는 클래스
 * ExceptionTranslationFilter 아래에서 AuthenticationException 발생 -> JwtAuthenticationEntryPoint에서 처리
 */

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String VALIDATION_RESULT_KEY = "result";
    private static final String ERROR_MESSAGE_KEY = "errMsg";
    private static final String TOKEN_INFO_KEY = "tokenInfo";
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //추후 구현할 JwtFilter에서 인증 결과를 전달할 때 TokenValidationResult를 담아서 넘겨주므로 request에서
        // TokenValidationResult를 꺼낸다.
        TokenValidationResult result = (TokenValidationResult) request.getAttribute(VALIDATION_RESULT_KEY);
//        String errorMessage = result.getTokenStatus().getMessage();
        String errorMessage;
        int errorCode;
        TokenInfo tokenInfo = null;

        if (result == null || result.getTokenStatus() == null) {
            errorMessage = TokenStatus.TOKEN_VALIDATION_TRY_FAILED.getMessage();
            errorCode = ResponseStatusCode.TOKEN_VALIDATION_TRY_FAILED;
        } else {
            switch (result.getTokenStatus()) {
                case TOKEN_EXPIRED -> errorCode = ResponseStatusCode.TOKEN_EXPIRED;
                case TOKEN_IS_BLACKLIST -> errorCode = ResponseStatusCode.TOKEN_IS_BLACKLIST;
                case TOKEN_WRONG_SIGNATURE -> errorCode = ResponseStatusCode.TOKEN_WRONG_SIGNATURE;
                case TOKEN_HASH_NOT_SUPPORTED -> errorCode = ResponseStatusCode.TOKEN_HASH_NOT_SUPPORTED;
                case WRONG_AUTH_HEADER -> errorCode = ResponseStatusCode.NO_AUTH_HEADER;
                case TOKEN_WRONG_TYPE -> errorCode = ResponseStatusCode.TOKEN_WRONG_TYPE;
                case TOKEN_REFRESHED -> {
                    errorCode = ResponseStatusCode.TOKEN_REFRESHED;
                    tokenInfo = result.getTokenInfo();
                }
                default -> errorCode = ResponseStatusCode.SERVER_ERROR;
            }

            errorMessage = result.getTokenStatus().getMessage();
        }

        sendError(response, errorMessage, errorCode, tokenInfo);
    }

    private void sendError(HttpServletResponse response, String msg, int code, TokenInfo tokenInfo) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> data = new HashMap<>();
        data.put(ERROR_MESSAGE_KEY, msg);
        if (tokenInfo != null) {
            data.put(TOKEN_INFO_KEY, tokenInfo);
        }

        ApiResponseJson responseJson = new ApiResponseJson(HttpStatus.UNAUTHORIZED, code, data);
        String jsonToString = objectMapper.writeValueAsString(responseJson);
        response.getWriter().write(jsonToString);
    }

}
