package com.alzheimer.healthprevention.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Central entity linking a patient (referenced by userId from the backend monolith)
 * to their health prevention profile, lifestyle recommendations, and wellness tracking.
 */
@Entity
@Table(name = "health_profiles")
public class HealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Reference to the User in the backend monolith (no FK – cross-service) */
    @Column(name = "user_id", nullable = false, unique = true)
    @NotNull
    private Long userId;

    /** Reference to the MedicalRecord in the backend monolith */
    @Column(name = "medical_record_id")
    private Long medicalRecordId;

    // ─── Lifestyle metrics ────────────────────────────────────────────────────
    @Column(name = "physical_activity_level", length = 50)
    @Enumerated(EnumType.STRING)
    private ActivityLevel physicalActivityLevel = ActivityLevel.SEDENTARY;

    @Column(name = "sleep_hours_per_night")
    private Double sleepHoursPerNight;

    @Column(name = "diet_quality", length = 50)
    @Enumerated(EnumType.STRING)
    private DietQuality dietQuality = DietQuality.AVERAGE;

    @Column(name = "stress_level", length = 50)
    @Enumerated(EnumType.STRING)
    private StressLevel stressLevel = StressLevel.MODERATE;

    @Column(name = "smoking_status")
    private Boolean smokingStatus = false;

    @Column(name = "alcohol_consumption", length = 50)
    @Enumerated(EnumType.STRING)
    private AlcoholConsumption alcoholConsumption = AlcoholConsumption.NONE;

    @Column(name = "social_engagement_level", length = 50)
    @Enumerated(EnumType.STRING)
    private EngagementLevel socialEngagementLevel = EngagementLevel.MODERATE;

    @Column(name = "cognitive_training_frequency", length = 50)
    private String cognitiveTrainingFrequency; // Daily, Weekly, Rarely, Never

    // ─── Overall wellness score (calculated) ─────────────────────────────────
    @Column(name = "wellness_score")
    private Double wellnessScore = 0.0;

    @Column(name = "last_assessment_date")
    private LocalDateTime lastAssessmentDate;

    // ─── Notes ────────────────────────────────────────────────────────────────
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ─── Relationships ────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "healthProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthRecommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "healthProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WellnessActivity> wellnessActivities = new ArrayList<>();

    // ─── Timestamps ──────────────────────────────────────────────────────────
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    // ─── Getters & Setters ────────────────────────────────────────────────────
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

    public LocalDateTime getLastAssessmentDate() { return lastAssessmentDate; }
    public void setLastAssessmentDate(LocalDateTime lastAssessmentDate) { this.lastAssessmentDate = lastAssessmentDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<HealthRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<HealthRecommendation> recommendations) { this.recommendations = recommendations; }

    public List<WellnessActivity> getWellnessActivities() { return wellnessActivities; }
    public void setWellnessActivities(List<WellnessActivity> wellnessActivities) { this.wellnessActivities = wellnessActivities; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
