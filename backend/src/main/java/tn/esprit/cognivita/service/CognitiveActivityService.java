package tn.esprit.cognivita.service;

import tn.esprit.cognivita.entity.CognitiveActivity;
import tn.esprit.cognivita.entity.ActivityParticipation;
import java.util.List;
import java.util.Map;

public interface CognitiveActivityService {

    // CRUD Activities
    CognitiveActivity createActivity(CognitiveActivity activity);
    CognitiveActivity updateActivity(Long id, CognitiveActivity activity);
    void deleteActivity(Long id);
    CognitiveActivity getActivityById(Long id);
    List<CognitiveActivity> getAllActivities();

    // Filters
    List<CognitiveActivity> getActivitiesByType(String type);
    List<CognitiveActivity> getActivitiesByDifficulty(String difficulty);
    List<CognitiveActivity> filterActivities(String type, String difficulty);
    List<CognitiveActivity> searchActivities(String keyword);

    // Activity Status
    void deactivateActivity(Long id);

    // Participation
    ActivityParticipation startActivity(Long activityId, Long userId);
    ActivityParticipation completeActivity(Long participationId, Integer score, Integer timeSpent);
    ActivityParticipation abandonActivity(Long participationId);

    // User History
    List<ActivityParticipation> getUserHistory(Long userId);
    List<CognitiveActivity> getUserCompletedActivities(Long userId);

    // Recommendations
    List<CognitiveActivity> getRecommendationsForUser(Long userId);

    // Statistics
    Map<String, Object> getUserStatistics(Long userId);
    Map<String, Object> getGlobalStatistics();
}