package com.alzheimer.healthprevention.controller;

import com.alzheimer.healthprevention.dto.ApiResponse;
import com.alzheimer.healthprevention.dto.HealthProfileDTO;
import com.alzheimer.healthprevention.dto.WellnessDashboardDTO;
import com.alzheimer.healthprevention.entity.HealthProfile;
import com.alzheimer.healthprevention.entity.HealthRecommendation;
import com.alzheimer.healthprevention.service.HealthProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-prevention/profiles")
@CrossOrigin(origins = "http://localhost:4200")
public class HealthProfileController {

    private final HealthProfileService profileService;

    public HealthProfileController(HealthProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HealthProfile>> createProfile(@Valid @RequestBody HealthProfileDTO dto) {
        try {
            HealthProfile profile = profileService.createProfile(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Health profile created successfully", profile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<HealthProfile>> getProfileByUserId(@PathVariable Long userId) {
        return profileService.getProfileByUserId(userId)
                .map(p -> ResponseEntity.ok(ApiResponse.success("Profile found", p)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("No health profile found for user " + userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthProfile>> getProfileById(@PathVariable Long id) {
        return profileService.getProfileById(id)
                .map(p -> ResponseEntity.ok(ApiResponse.success("Profile found", p)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Profile not found")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HealthProfile>>> getAllProfiles() {
        List<HealthProfile> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(ApiResponse.success("Profiles retrieved", profiles));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthProfile>> updateProfile(
            @PathVariable Long id, @Valid @RequestBody HealthProfileDTO dto) {
        try {
            HealthProfile updated = profileService.updateProfile(id, dto);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable Long id) {
        try {
            profileService.deleteProfile(id);
            return ResponseEntity.ok(ApiResponse.success("Profile deleted", null));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/dashboard")
    public ResponseEntity<ApiResponse<WellnessDashboardDTO>> getDashboard(@PathVariable Long userId) {
        try {
            WellnessDashboardDTO dashboard = profileService.getDashboard(userId);
            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", dashboard));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/generate-recommendations")
    public ResponseEntity<ApiResponse<List<HealthRecommendation>>> generateRecommendations(@PathVariable Long id) {
        try {
            List<HealthRecommendation> recs = profileService.generateRecommendations(id);
            return ResponseEntity.ok(ApiResponse.success(
                    recs.size() + " evidence-based recommendations generated", recs));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }
}
