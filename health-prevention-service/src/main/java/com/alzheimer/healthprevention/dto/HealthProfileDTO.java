package com.alzheimer.healthprevention.dto;

import com.alzheimer.healthprevention.entity.*;
import jakarta.validation.constraints.NotNull;

public class HealthProfileDTO {
    private Long id;

    @NotNull(message = "userId is required")
    private Long userId;

    private Long medicalRecordId;
    private ActivityLevel physicalActivityLevel;
    private Double sleepHoursPerNight;
    private DietQuality dietQuality;
    private StressLevel stressLevel;
    private Boolean smokingStatus;
    private AlcoholConsumption alcoholConsumption;
    private EngagementLevel socialEngagementLevel;
    private String cognitiveTrainingFrequency;
    private Double wellnessScore;
    private String lastAssessmentDate;
    private String notes;
    private String createdAt;
    private String updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMedicalRecordId() { return medicalRecordId; }
    public void setMedicalRecordId(Long medicalRecordId) { this.medicalRecordId = medicalRecordId; }

    public ActivityLevel getPhysicalActivityLevel() { return physicalActivityLevel; }
    public void setPhysicalActivityLevel(ActivityLevel physicalActivityLevel) { this.physicalActivityLevel = physicalActivityLevel; }

    public Double getSleepHoursPerNight() { return sleepHoursPerNight; }
    public void setSleepHoursPerNight(Double sleepHoursPerNight) { this.sleepHoursPerNight = sleepHoursPerNight; }

    public DietQuality getDietQuality() { return dietQuality; }
    public void setDietQuality(DietQuality dietQuality) { this.dietQuality = dietQuality; }

    public StressLevel getStressLevel() { return stressLevel; }
    public void setStressLevel(StressLevel stressLevel) { this.stressLevel = stressLevel; }

    public Boolean getSmokingStatus() { return smokingStatus; }
    public void setSmokingStatus(Boolean smokingStatus) { this.smokingStatus = smokingStatus; }

    public AlcoholConsumption getAlcoholConsumption() { return alcoholConsumption; }
    public void setAlcoholConsumption(AlcoholConsumption alcoholConsumption) { this.alcoholConsumption = alcoholConsumption; }

    public EngagementLevel getSocialEngagementLevel() { return socialEngagementLevel; }
    public void setSocialEngagementLevel(EngagementLevel socialEngagementLevel) { this.socialEngagementLevel = socialEngagementLevel; }

    public String getCognitiveTrainingFrequency() { return cognitiveTrainingFrequency; }
    public void setCognitiveTrainingFrequency(String cognitiveTrainingFrequency) { this.cognitiveTrainingFrequency = cognitiveTrainingFrequency; }

    public Double getWellnessScore() { return wellnessScore; }
    public void setWellnessScore(Double wellnessScore) { this.wellnessScore = wellnessScore; }

    public String getLastAssessmentDate() { return lastAssessmentDate; }
    public void setLastAssessmentDate(String lastAssessmentDate) { this.lastAssessmentDate = lastAssessmentDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
