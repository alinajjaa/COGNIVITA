package com.alzheimer.backend.medical;

import com.alzheimer.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

    private final TimelineRepository timelineRepository;

    public TimelineController(TimelineRepository timelineRepository) {
        this.timelineRepository = timelineRepository;
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<ApiResponse<List<TimelineDTO>>> getTimeline(
            @PathVariable Long medicalRecordId) {
        try {
            List<TimelineDTO> events = timelineRepository
                    .findByMedicalRecordIdOrderByEventDateDesc(medicalRecordId)
                    .stream().map(TimelineDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Timeline retrieved", events));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve timeline", e.getMessage()));
        }
    }

    @GetMapping("/medical-record/{medicalRecordId}/type/{eventType}")
    public ResponseEntity<ApiResponse<List<TimelineDTO>>> getTimelineByType(
            @PathVariable Long medicalRecordId, @PathVariable String eventType) {
        try {
            EventType type = EventType.valueOf(eventType.toUpperCase());
            List<TimelineDTO> events = timelineRepository
                    .findByMedicalRecordIdAndEventType(medicalRecordId, type)
                    .stream().map(TimelineDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Timeline filtered", events));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to filter timeline", e.getMessage()));
        }
    }

    @GetMapping("/medical-record/{medicalRecordId}/filter")
    public ResponseEntity<ApiResponse<List<TimelineDTO>>> getTimelineByDateRange(
            @PathVariable Long medicalRecordId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            List<TimelineDTO> events = timelineRepository
                    .findByMedicalRecordIdAndEventDateBetweenOrderByEventDateDesc(medicalRecordId, start, end)
                    .stream().map(TimelineDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Timeline filtered", events));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to filter timeline", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TimelineDTO>>> getTimelineByUser(@PathVariable Long userId) {
        try {
            List<TimelineDTO> events = timelineRepository.findByUserId(userId)
                    .stream().map(TimelineDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("User timeline retrieved", events));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user timeline", e.getMessage()));
        }
    }
}
