package com.alzheimer.healthprevention.dto;

import java.util.Map;

public class WellnessDashboardDTO {
    private Long userId;
    private Long healthProfileId;
    private Double wellnessScore;
    private String wellnessLevel; // POOR, FAIR, GOOD, EXCELLENT
    private int totalRecommendations;
    private int activeRecommendations;
    private int completedRecommendations;
    private int totalActivities;
    private int activitiesThisWeek;
    private int totalActivityMinutes;
    private Map<String, Integer> activitiesByType;
    private Map<String, Integer> recommendationsByCategory;
    private String lastAssessmentDate;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getHealthProfileId() { return healthProfileId; }
    public void setHealthProfileId(Long healthProfileId) { this.healthProfileId = healthProfileId; }

    public Double getWellnessScore() { return wellnessScore; }
    public void setWellnessScore(Double wellnessScore) { this.wellnessScore = wellnessScore; }

    public String getWellnessLevel() { return wellnessLevel; }
    public void setWellnessLevel(String wellnessLevel) { this.wellnessLevel = wellnessLevel; }

    public int getTotalRecommendations() { return totalRecommendations; }
    public void setTotalRecommendations(int totalRecommendations) { this.totalRecommendations = totalRecommendations; }

    public int getActiveRecommendations() { return activeRecommendations; }
    public void setActiveRecommendations(int activeRecommendations) { this.activeRecommendations = activeRecommendations; }

    public int getCompletedRecommendations() { return completedRecommendations; }
    public void setCompletedRecommendations(int completedRecommendations) { this.completedRecommendations = completedRecommendations; }

    public int getTotalActivities() { return totalActivities; }
    public void setTotalActivities(int totalActivities) { this.totalActivities = totalActivities; }

    public int getActivitiesThisWeek() { return activitiesThisWeek; }
    public void setActivitiesThisWeek(int activitiesThisWeek) { this.activitiesThisWeek = activitiesThisWeek; }

    public int getTotalActivityMinutes() { return totalActivityMinutes; }
    public void setTotalActivityMinutes(int totalActivityMinutes) { this.totalActivityMinutes = totalActivityMinutes; }

    public Map<String, Integer> getActivitiesByType() { return activitiesByType; }
    public void setActivitiesByType(Map<String, Integer> activitiesByType) { this.activitiesByType = activitiesByType; }

    public Map<String, Integer> getRecommendationsByCategory() { return recommendationsByCategory; }
    public void setRecommendationsByCategory(Map<String, Integer> recommendationsByCategory) { this.recommendationsByCategory = recommendationsByCategory; }

    public String getLastAssessmentDate() { return lastAssessmentDate; }
    public void setLastAssessmentDate(String lastAssessmentDate) { this.lastAssessmentDate = lastAssessmentDate; }
}
