package com.alzheimer.healthprevention.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * OpenFeign client to communicate with the main backend service (port 8081).
 * Uses direct URL because the backend monolith is not registered with Eureka.
 */
@FeignClient(name = "backend-service", url = "${backend.service.url:http://localhost:8081}")
public interface BackendServiceClient {

    /**
     * Fetch a user's basic info for enriching health profiles.
     */
    @GetMapping("/api/users/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") Long userId);

    /**
     * Fetch medical record summary for risk-score enrichment.
     */
    @GetMapping("/api/medical-records/user/{userId}")
    Map<String, Object> getMedicalRecordByUserId(@PathVariable("userId") Long userId);
}
