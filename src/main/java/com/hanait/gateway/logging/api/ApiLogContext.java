package com.hanait.gateway.logging.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiLogContext {

    private static final ThreadLocal<ApilogData> context = new ThreadLocal<>();

    public static void init(ApilogData logData) {
        context.set(logData);
    }

    public static ApilogData get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }

    // ✅ UUID 직접 접근
    public static String getRequestId() {
        ApilogData logData = get();
        return logData != null ? logData.getUuid() : null;
    }

    // ✅ userCode 직접 접근
    public static String getUserCode() {
        ApilogData logData = get();
        return logData != null ? logData.getRequestUserCode() : null;
    }

    // ✅ 현재 Context 존재 여부 확인
    public static boolean isInitialized() {
        return get() != null;
    }


}
