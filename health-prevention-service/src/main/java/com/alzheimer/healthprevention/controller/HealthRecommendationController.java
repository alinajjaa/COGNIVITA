package com.alzheimer.healthprevention.controller;

import com.alzheimer.healthprevention.dto.ApiResponse;
import com.alzheimer.healthprevention.dto.HealthRecommendationDTO;
import com.alzheimer.healthprevention.entity.HealthRecommendation;
import com.alzheimer.healthprevention.entity.RecommendationCategory;
import com.alzheimer.healthprevention.entity.RecommendationStatus;
import com.alzheimer.healthprevention.service.HealthRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/health-prevention/recommendations")
@CrossOrigin(origins = "http://localhost:4200")
public class HealthRecommendationController {

    private final HealthRecommendationService recommendationService;

    public HealthRecommendationController(HealthRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HealthRecommendation>> createRecommendation(
            @Valid @RequestBody HealthRecommendationDTO dto) {
        try {
            HealthRecommendation rec = recommendationService.createRecommendation(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Recommendation created", rec));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ApiResponse<List<HealthRecommendation>>> getByProfileId(@PathVariable Long profileId) {
        List<HealthRecommendation> recs = recommendationService.getByProfileId(profileId);
        return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved", recs));
    }

    @GetMapping("/profile/{profileId}/status/{status}")
    public ResponseEntity<ApiResponse<List<HealthRecommendation>>> getByStatus(
            @PathVariable Long profileId, @PathVariable RecommendationStatus status) {
        List<HealthRecommendation> recs = recommendationService.getByProfileIdAndStatus(profileId, status);
        return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved by status", recs));
    }

    @GetMapping("/profile/{profileId}/category/{category}")
    public ResponseEntity<ApiResponse<List<HealthRecommendation>>> getByCategory(
            @PathVariable Long profileId, @PathVariable RecommendationCategory category) {
        List<HealthRecommendation> recs = recommendationService.getByProfileIdAndCategory(profileId, category);
        return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved by category", recs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthRecommendation>> getById(@PathVariable Long id) {
        return recommendationService.getById(id)
                .map(r -> ResponseEntity.ok(ApiResponse.success("Recommendation found", r)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Recommendation not found")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthRecommendation>> updateRecommendation(
            @PathVariable Long id, @Valid @RequestBody HealthRecommendationDTO dto) {
        try {
            HealthRecommendation updated = recommendationService.updateRecommendation(id, dto);
            return ResponseEntity.ok(ApiResponse.success("Recommendation updated", updated));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<HealthRecommendation>> completeRecommendation(@PathVariable Long id) {
        try {
            HealthRecommendation rec = recommendationService.completeRecommendation(id);
            return ResponseEntity.ok(ApiResponse.success("Recommendation marked as completed", rec));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecommendation(@PathVariable Long id) {
        try {
            recommendationService.deleteRecommendation(id);
            return ResponseEntity.ok(ApiResponse.success("Recommendation deleted", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }
}
