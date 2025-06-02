package com.hanait.gateway.logging.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiloggingAspect {

    private final MongoTemplate mongoTemplate;

    @Around("execution(* com.hanait.gateway..*Controller.*(..))")
    public Object logDataApi(ProceedingJoinPoint joinPoint) throws Throwable {
        ApilogData logDataApi = ApiLogContext.get();
        long start = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed();
            if (logDataApi != null) {
                logDataApi.setResponseData(result);
            }
            return result;
        } catch (Exception ex) {
            if (logDataApi != null) {
                logDataApi.setResponseData(Map.of("error", ex.getMessage()));
            }
            throw ex;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            if (logDataApi != null) {
                logDataApi.setResponseTimeMs(elapsed);
                mongoTemplate.save(logDataApi);
            } else {
                log.warn("ApilogContext에 로그 데이터가 없습니다. (null)");
            }
        }
    }
}
