package com.hanait.gateway.util;

import com.hanait.gateway.interceptor.RequestIdInterceptor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 현재 요청 컨텍스트에서 정보를 쉽게 가져올 수 있는 유틸리티 클래스
 */
public class RequestContextUtils {
    
    /**
     * 현재 요청의 UUID를 가져옵니다.
     * 현재 요청이 없거나 UUID가 설정되지 않은 경우 새로운 UUID를 생성합니다.
     * 
     * @return 현재 요청의 UUID
     */
    public static String getCurrentRequestId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String requestId = (String) request.getAttribute(RequestIdInterceptor.REQUEST_ID_ATTRIBUTE);
                if (requestId != null && !requestId.isEmpty()) {
                    return requestId;
                }
            }
        } catch (Exception e) {
            // RequestContextHolder가 사용 불가능한 경우 (예: 백그라운드 스레드) 예외가 발생할 수 있음
        }
        
        // 현재 요청 컨텍스트를 찾을 수 없는 경우 새로운 UUID 생성
        return UuidGenerator.generateUuid();
    }
}
