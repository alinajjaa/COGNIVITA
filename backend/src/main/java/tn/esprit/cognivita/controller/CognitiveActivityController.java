package tn.esprit.cognivita.controller;

import tn.esprit.cognivita.entity.CognitiveActivity;
import tn.esprit.cognivita.entity.ActivityParticipation;
import tn.esprit.cognivita.service.CognitiveActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activities")  // ← MODIFIÉ: plus de "/api" ici car déjà dans context-path
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CognitiveActivityController {

    private final CognitiveActivityService activityService;

    // ============= CRUD OPERATIONS =============

    @PostMapping
    public ResponseEntity<CognitiveActivity> createActivity(@RequestBody CognitiveActivity activity) {
        CognitiveActivity created = activityService.createActivity(activity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CognitiveActivity>> getAllActivities() {
        List<CognitiveActivity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CognitiveActivity> getActivityById(@PathVariable Long id) {
        CognitiveActivity activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CognitiveActivity> updateActivity(
            @PathVariable Long id,
            @RequestBody CognitiveActivity activity) {
        CognitiveActivity updated = activityService.updateActivity(id, activity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateActivity(@PathVariable Long id) {
        activityService.deactivateActivity(id);
        return ResponseEntity.ok().build();
    }

    // ============= FILTERS =============

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CognitiveActivity>> getActivitiesByType(@PathVariable String type) {
        List<CognitiveActivity> activities = activityService.getActivitiesByType(type);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<CognitiveActivity>> getActivitiesByDifficulty(@PathVariable String difficulty) {
        List<CognitiveActivity> activities = activityService.getActivitiesByDifficulty(difficulty);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CognitiveActivity>> filterActivities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty) {
        List<CognitiveActivity> activities = activityService.filterActivities(type, difficulty);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CognitiveActivity>> searchActivities(@RequestParam String keyword) {
        List<CognitiveActivity> activities = activityService.searchActivities(keyword);
        return ResponseEntity.ok(activities);
    }

    // ============= USER HISTORY =============

    @GetMapping("/users/{userId}/history")
    public ResponseEntity<List<ActivityParticipation>> getUserHistory(@PathVariable Long userId) {
        List<ActivityParticipation> history = activityService.getUserHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/users/{userId}/completed")
    public ResponseEntity<List<CognitiveActivity>> getUserCompletedActivities(@PathVariable Long userId) {
        List<CognitiveActivity> activities = activityService.getUserCompletedActivities(userId);
        return ResponseEntity.ok(activities);
    }

    // ============= PARTICIPATION =============

    @PostMapping("/{activityId}/start")
    public ResponseEntity<ActivityParticipation> startActivity(
            @PathVariable Long activityId,
            @RequestParam Long userId) {
        ActivityParticipation participation = activityService.startActivity(activityId, userId);
        return new ResponseEntity<>(participation, HttpStatus.CREATED);
    }

    @PutMapping("/participations/{participationId}/complete")
    public ResponseEntity<ActivityParticipation> completeActivity(
            @PathVariable Long participationId,
            @RequestParam Integer score,
            @RequestParam Integer timeSpent) {
        ActivityParticipation participation = activityService.completeActivity(participationId, score, timeSpent);
        return ResponseEntity.ok(participation);
    }

    @PutMapping("/participations/{participationId}/abandon")
    public ResponseEntity<ActivityParticipation> abandonActivity(@PathVariable Long participationId) {
        ActivityParticipation participation = activityService.abandonActivity(participationId);
        return ResponseEntity.ok(participation);
    }

    // ============= STATISTICS =============

    @GetMapping("/statistics/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long userId) {
        Map<String, Object> statistics = activityService.getUserStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/global")
    public ResponseEntity<Map<String, Object>> getGlobalStatistics() {
        Map<String, Object> statistics = activityService.getGlobalStatistics();
        return ResponseEntity.ok(statistics);
    }

    // ============= RECOMMENDATIONS =============

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<CognitiveActivity>> getRecommendations(@PathVariable Long userId) {
        List<CognitiveActivity> recommendations = activityService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(recommendations);
    }
}