package com.hanait.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfig {

    private final WebClientLoggingFilter webClientLoggingFilter;

    public WebClientConfig(WebClientLoggingFilter webClientLoggingFilter) {
        this.webClientLoggingFilter = webClientLoggingFilter;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://hapi-fhir-jpaserver-start:8090/fhir")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/fhir+json")
                .filter(webClientLoggingFilter.logRequestAndResponse())
                .build();
    }
}
