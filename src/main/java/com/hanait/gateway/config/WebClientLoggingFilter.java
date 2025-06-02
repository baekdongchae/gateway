package com.hanait.gateway.config;

import com.hanait.gateway.logging.api.ApiLogContext;
import com.hanait.gateway.logging.api.ApilogData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.time.ZoneOffset;

@Slf4j
@Component
public class WebClientLoggingFilter {

    private final MongoTemplate mongoTemplate;

    public WebClientLoggingFilter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public ExchangeFilterFunction logRequestAndResponse() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            StringBuilder sb = new StringBuilder("🟦 WebClient Request\n");
            sb.append("➡️ Method: ").append(request.method()).append("\n");
            sb.append("➡️ URL: ").append(request.url()).append("\n");

            sb.append("📝 Headers:\n");
            request.headers().forEach((name, values) ->
                    values.forEach(value -> sb.append(name).append(": ").append(value).append("\n")));

            log.info(sb.toString());
            return Mono.just(request);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(response -> {
            long responseEndTime = System.currentTimeMillis(); // 응답 종료 시점

            return response.bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> {
                        StringBuilder sb = new StringBuilder("🟩 WebClient Response\n");
                        sb.append("⬅️ Status: ").append(response.statusCode()).append("\n");

                        sb.append("📦 Headers:\n");
                        response.headers().asHttpHeaders()
                                .forEach((name, values) ->
                                        values.forEach(value -> sb.append(name).append(": ").append(value).append("\n")));

                        sb.append("📄 Body:\n").append(body);
                        log.info(sb.toString());

                        // ApilogData 저장
                        ApilogData logData = ApiLogContext.get();
                        if (logData != null) {
                            try {
                                logData.setResponseData(body);

                                long requestStartTime = logData.getTimestamp()
                                        .atZone(ZoneOffset.systemDefault())
                                        .toInstant()
                                        .toEpochMilli();
                                long elapsed = responseEndTime - requestStartTime;
                                logData.setResponseTimeMs(elapsed);

                                mongoTemplate.save(logData);
                            } catch (Exception e) {
                                log.warn("❌ ApilogData 저장 실패: {}", e.getMessage(), e);
                            }
                        }

                        // 응답 body 포함된 ClientResponse 반환
                        return Mono.just(ClientResponse.from(response)
                                .body(body)
                                .build());
                    });
        }));
    }
}
