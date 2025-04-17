package com.hanait.gateway.controller;

import com.hanait.gateway.service.FhirResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/fhir/resources")
public class FhirResourceController {

    private final FhirResourceService resourceService;

    @Autowired
    public FhirResourceController(FhirResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getAllResources() {
        return resourceService.getAllResources();
    }

    @GetMapping(value = "/{resourceType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getResourcesByType(@PathVariable String resourceType) {
        return resourceService.getResourcesByType(resourceType);
    }

    @GetMapping(value = "/{resourceType}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getResourceById(@PathVariable String resourceType, @PathVariable String id) {
        return resourceService.getResourceById(resourceType, id);
    }

    @PostMapping(value = "/{resourceType}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> createResource(
            @PathVariable String resourceType,
            @RequestBody String resourceBody) {

        return resourceService.createResource(resourceType, resourceBody);
    }

    @PutMapping(value = "/{resourceType}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> updateResource(
            @PathVariable String resourceType,
            @PathVariable String id,
            @RequestBody String resourceBody) {

        return resourceService.updateResource(resourceType, id, resourceBody);
    }

    @DeleteMapping(value = "/{resourceType}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> deleteResource(
            @PathVariable String resourceType,
            @PathVariable String id) {

        return resourceService.deleteResource(resourceType, id);
    }
}