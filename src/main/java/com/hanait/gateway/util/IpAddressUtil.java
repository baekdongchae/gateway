package com.hanait.gateway.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class IpAddressUtil {

    private static final List<String> HEADER_LIST = Collections.unmodifiableList(
            Arrays.asList(
                    "X-Forwarded-For",
                    "Proxy-Client-IP",
                    "WL-Proxy-Client-IP",
                    "HTTP_X_FORWARDED_FOR",
                    "HTTP_X_FORWARDED",
                    "HTTP_X_CLUSTER_CLIENT_IP",
                    "HTTP_CLIENT_IP",
                    "HTTP_FORWARDED_FOR",
                    "HTTP_FORWARDED",
                    "HTTP_VIA",
                    "REMOTE_ADDR"
            )
    );

    public static String getClientIpAddress() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return "0.0.0.0";
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getClientIpAddress(request);
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADER_LIST) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                // X-Forwarded-For 헤더는 여러 IP가 콤마로 구분되어 있을 수 있음
                // 첫 번째 IP가 실제 클라이언트 IP
                if (header.equals("X-Forwarded-For")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    private static boolean isValidIp(String ip) {
        return ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip);
    }
}