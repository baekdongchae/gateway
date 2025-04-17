package com.hanait.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class FhirResourceService {

    private final WebClient webClient;

    @Autowired
    public FhirResourceService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> getAllResources() {
        return webClient.get()
                .uri("/fhir")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.err.println("Error retrieving all resources: " + e.getMessage());
                    return Mono.just("{ \"error\": \"Failed to retrieve resources\", \"message\": \"" + e.getMessage() + "\" }");
                });
    }

    public Mono<String> getResourcesByType(String resourceType) {
        return webClient.get()
                .uri("/fhir/" + resourceType)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.err.println("Error retrieving resources by type: " + e.getMessage());
                    return Mono.just("{ \"error\": \"Failed to retrieve resources of type " + resourceType + "\", \"message\": \"" + e.getMessage() + "\" }");
                });
    }

    public Mono<String> getResourceById(String resourceType, String id) {
        return webClient.get()
                .uri("/fhir/" + resourceType + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.err.println("Error retrieving resource by ID: " + e.getMessage());
                    return Mono.just("{ \"error\": \"Resource not found or error occurred\", \"message\": \"" + e.getMessage() + "\" }");
                });
    }

    public Mono<String> createResource(String resourceType, String resourceBody) {
        return webClient.post()
                .uri("/fhir/" + resourceType)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(resourceBody))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.err.println("Error creating resource: " + e.getMessage());
                    return Mono.just("{ \"error\": \"Failed to create resource\", \"message\": \"" + e.getMessage() + "\" }");
                });
    }

    public Mono<String> updateResource(String resourceType, String id, String resourceBody) {
        return webClient.put()
                .uri("/fhir/" + resourceType + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(resourceBody))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.err.println("Error updating resource: " + e.getMessage());
                    return Mono.just("{ \"error\": \"Failed to update resource\", \"message\": \"" + e.getMessage() + "\" }");
                });
    }

    public Mono<String> deleteResource(String resourceType, String id) {
        return webClient.delete()
                .uri("/fhir/" + resourceType + "/" + id)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.err.println("Error deleting resource: " + e.getMessage());
                    return Mono.just("{ \"error\": \"Failed to delete resource\", \"message\": \"" + e.getMessage() + "\" }");
                });
    }
}