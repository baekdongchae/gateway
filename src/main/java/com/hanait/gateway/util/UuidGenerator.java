package com.hanait.gateway.util;

import java.util.UUID;

/**
 * API 호출시 고유 UUID를 생성하는 유틸리티 클래스
 */
public class UuidGenerator {
    
    /**
     * 새로운 UUID를 생성합니다.
     * 
     * @return 생성된 UUID 문자열
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 대시(-)가 없는 UUID를 생성합니다.
     * 
     * @return 대시가 없는 UUID 문자열
     */
    public static String generateCompactUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
