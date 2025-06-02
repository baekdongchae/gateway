package com.hanait.gateway.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanait.gateway.logging.api.ApiLogContext;
import com.hanait.gateway.logging.api.ApilogData;
import com.hanait.gateway.logging.UserDeviceData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 모든 API 요청에 고유 UUID를 생성하여 추가하는 인터셉터
 */
@Slf4j
@Component
public class RequestIdInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID_ATTRIBUTE = "requestId";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String PREV_REQUEST_ID_HEADER = "X-Prev-Request-ID";
    private static final String TOKEN_HEADER = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
                .filter(s -> !s.isEmpty())
                .orElse(UUID.randomUUID().toString());

        String prevUuid = request.getHeader(PREV_REQUEST_ID_HEADER);
        String token = request.getHeader(TOKEN_HEADER);
        String tokenHash = token != null ? DigestUtils.md5DigestAsHex(token.getBytes()) : null;

        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());

        String userCode = request.getHeader("X-USER-CODE");

        // Client metadata (Custom header 기반)
        String clientPage = request.getHeader("X-Client-Page");
        String clientAction = request.getHeader("X-Client-Action");

        UserDeviceData deviceData = new UserDeviceData();
        deviceData.setAppVersion(request.getHeader("X-App-Version"));
        deviceData.setDeviceName(request.getHeader("X-Device-Name"));
        deviceData.setDeviceOs(request.getHeader("X-Device-OS"));
        deviceData.setDeviceOsVersion(request.getHeader("X-Device-OS-Version"));

        // 요청 본문
        String requestBody = "";
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            requestBody = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        }

        ApilogData logData = new ApilogData();
        logData.setUuid(requestId);
        logData.setPrevUuid(prevUuid);
        logData.setTokenHash(tokenHash);
        logData.setIp(ip);
        logData.setRequestUserCode(userCode);
        logData.setApiUrl(request.getRequestURI());
        logData.setMethod(request.getMethod());
        logData.setClientPage(clientPage);
        logData.setClientAction(clientAction);
        logData.setRequestData(parseJsonSafely(requestBody));
        logData.setUserDeviceData(deviceData);
        logData.setTimestamp(LocalDateTime.now());

        request.setAttribute("requestStartTime", System.currentTimeMillis());
        ApiLogContext.init(logData);

        response.setHeader(REQUEST_ID_HEADER, requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ApiLogContext.clear();
    }

    private Object parseJsonSafely(String json) {
        try {
            return new ObjectMapper().readValue(json, Map.class);
        } catch (Exception e) {
            return json; // fallback to raw string
        }
    }
}

//@Slf4j
//@Component
//public class RequestIdInterceptor implements HandlerInterceptor {
//
//    public static final String REQUEST_ID_ATTRIBUTE = "requestId";
//    public static final String REQUEST_ID_HEADER = "X-Request-ID";
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // 요청 헤더에 이미 Request ID가 있는지 확인
//        String requestId = request.getHeader(REQUEST_ID_HEADER);
//
//        // 없으면 새로 생성
//        if (requestId == null || requestId.isEmpty()) {
//            requestId = UuidGenerator.generateUuid();
//        }
//
//        // 요청 속성에 저장
//        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
//
//        // 응답 헤더에도 추가
//        response.setHeader(REQUEST_ID_HEADER, requestId);
//
//        logData.info("API 요청 시작 [{}] {}: {}", requestId, request.getMethod(), request.getRequestURI());
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
//        logData.info("API 요청 처리 완료 [{}] {}: {} (status: {})",
//                requestId,
//                request.getMethod(),
//                request.getRequestURI(),
//                response.getStatus());
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        // 예외 발생 시 로깅
//        if (ex != null) {
//            String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
//            logData.error("API 요청 처리 중 오류 발생 [{}] {}: {} (오류: {})",
//                    requestId,
//                    request.getMethod(),
//                    request.getRequestURI(),
//                    ex.getMessage(),
//                    ex);
//        }
//    }
//}
