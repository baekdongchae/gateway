package com.example.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/fhir")
public class FhirProxyController {
    
    private final RestTemplateBuilder restTemplateBuilder;
    
    @Autowired
    public FhirProxyController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }
    
    @GetMapping("/metadata")
    public ResponseEntity<String> getFhirMetadata() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        
        // HAPI FHIR 서버의 메타데이터 엔드포인트 호출
        String hapiFhirUrl = "http://hapi-fhir-jpaserver-start:8080/fhir/metadata";
        
        ResponseEntity<String> response = restTemplate.getForEntity(hapiFhirUrl, String.class);
        return response;
    }
    
    @GetMapping("/health")
    public String healthCheck() {
        try {
            RestTemplate restTemplate = restTemplateBuilder.build();
            
            // HAPI FHIR 서버의 상태 확인
            String hapiFhirUrl = "http://hapi-fhir-jpaserver-start:8080/fhir/metadata";
            restTemplate.getForEntity(hapiFhirUrl, String.class);
            
            return "Connection to HAPI FHIR server successful";
        } catch (Exception e) {
            return "Error connecting to HAPI FHIR server: " + e.getMessage();
        }
    }
}
