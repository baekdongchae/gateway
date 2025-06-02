package com.hanait.gateway.controller;

import ca.uhn.fhir.context.FhirContext;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/fhir")
public class PatientController {

    private final WebClient webClient;

    public PatientController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/test")
    public ResponseEntity<?> testAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("🧪 컨트롤러 인증 객체: {}", authentication);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchPatientByName(@RequestParam String name) {
        log.info("search controller");
        String jsonResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/Patient")
                        .queryParam("name", name)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/fhir+json")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            log.info("Received JSON response: " + jsonResponse);
            return ResponseEntity.ok(jsonResponse);  // 실제 받은 JSON 응답을 그대로 반환해봄
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No content found");
        }
    }

    @PostMapping("/create")
    public Mono<String> createPatient(@RequestBody String patientJson) {
        return webClient.post()
                .uri("/Patient")
                .header(HttpHeaders.CONTENT_TYPE, "application/fhir+json")
                .bodyValue(patientJson)
                .retrieve()
                .bodyToMono(String.class);
    }

}

