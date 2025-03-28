package com.hanait.gateway.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 인가(Authorization) 예외가 발생했을때 처리하는 클래스
 * ExceptionTranslationFilter 아래에서 AccessDeniedException 발생 -> JwtAccessDeniedHandler에서 처리
 * 유효한 토큰이지만, 권한이 부족해 접근할 수 없을 경우 발생한 예외를 처리한다.
 */
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN");
    }
}
