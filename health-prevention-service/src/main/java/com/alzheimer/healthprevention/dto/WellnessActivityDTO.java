package com.alzheimer.healthprevention.dto;

import com.alzheimer.healthprevention.entity.ActivityType;
import com.alzheimer.healthprevention.entity.IntensityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WellnessActivityDTO {
    private Long id;
    private Long healthProfileId;

    @NotBlank(message = "Activity name is required")
    private String activityName;

    private ActivityType activityType;
    private Integer durationMinutes;

    @NotNull(message = "Activity date is required")
    private String activityDate;

    private IntensityLevel intensityLevel;
    private String moodBefore;
    private String moodAfter;
    private String notes;
    private Integer caloriesBurned;
    private String createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHealthProfileId() { return healthProfileId; }
    public void setHealthProfileId(Long healthProfileId) { this.healthProfileId = healthProfileId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getActivityDate() { return activityDate; }
    public void setActivityDate(String activityDate) { this.activityDate = activityDate; }

    public IntensityLevel getIntensityLevel() { return intensityLevel; }
    public void setIntensityLevel(IntensityLevel intensityLevel) { this.intensityLevel = intensityLevel; }

    public String getMoodBefore() { return moodBefore; }
    public void setMoodBefore(String moodBefore) { this.moodBefore = moodBefore; }

    public String getMoodAfter() { return moodAfter; }
    public void setMoodAfter(String moodAfter) { this.moodAfter = moodAfter; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
