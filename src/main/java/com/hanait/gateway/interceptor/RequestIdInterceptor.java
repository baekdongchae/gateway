package com.hanait.gateway.interceptor;

import com.hanait.gateway.util.UuidGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 모든 API 요청에 고유 UUID를 생성하여 추가하는 인터셉터
 */
@Slf4j
@Component
public class RequestIdInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID_ATTRIBUTE = "requestId";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청 헤더에 이미 Request ID가 있는지 확인
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        
        // 없으면 새로 생성
        if (requestId == null || requestId.isEmpty()) {
            requestId = UuidGenerator.generateUuid();
        }
        
        // 요청 속성에 저장
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        
        // 응답 헤더에도 추가
        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        log.info("API 요청 시작 [{}] {}: {}", requestId, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
        log.info("API 요청 처리 완료 [{}] {}: {} (status: {})", 
                requestId, 
                request.getMethod(), 
                request.getRequestURI(), 
                response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 예외 발생 시 로깅
        if (ex != null) {
            String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
            log.error("API 요청 처리 중 오류 발생 [{}] {}: {} (오류: {})", 
                    requestId, 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    ex.getMessage(), 
                    ex);
        }
    }
}
