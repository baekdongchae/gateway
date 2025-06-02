package com.hanait.gateway.logging.api;

import com.hanait.gateway.logging.UserDeviceData;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "api_request")
public class ApilogData {

    private String uuid; // request ID
    private String prevUuid; // 이전 요청 ID (선택)

    private String ip;
    private String tokenHash;

    private String apiUrl;
    private String method;

    private String clientPage;
    private String clientAction;

    private String requestUserCode;

    private Object requestData;
    private Object responseData;

    private UserDeviceData userDeviceData;

    private long responseTimeMs;

    private LocalDateTime timestamp;
}
