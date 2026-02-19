package com.alzheimer.healthprevention.controller;

import com.alzheimer.healthprevention.dto.ApiResponse;
import com.alzheimer.healthprevention.dto.WellnessActivityDTO;
import com.alzheimer.healthprevention.entity.WellnessActivity;
import com.alzheimer.healthprevention.service.WellnessActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/health-prevention/activities")
@CrossOrigin(origins = "http://localhost:4200")
public class WellnessActivityController {

    private final WellnessActivityService activityService;

    public WellnessActivityController(WellnessActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WellnessActivity>> logActivity(
            @Valid @RequestBody WellnessActivityDTO dto) {
        try {
            WellnessActivity activity = activityService.logActivity(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Activity logged successfully", activity));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ApiResponse<List<WellnessActivity>>> getActivitiesByProfile(
            @PathVariable Long profileId) {
        List<WellnessActivity> activities = activityService.getActivitiesByProfileId(profileId);
        return ResponseEntity.ok(ApiResponse.success("Activities retrieved", activities));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WellnessActivity>> getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id)
                .map(a -> ResponseEntity.ok(ApiResponse.success("Activity found", a)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Activity not found")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WellnessActivity>> updateActivity(
            @PathVariable Long id, @Valid @RequestBody WellnessActivityDTO dto) {
        try {
            WellnessActivity updated = activityService.updateActivity(id, dto);
            return ResponseEntity.ok(ApiResponse.success("Activity updated", updated));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable Long id) {
        try {
            activityService.deleteActivity(id);
            return ResponseEntity.ok(ApiResponse.success("Activity deleted", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }
}
