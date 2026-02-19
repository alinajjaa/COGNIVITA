package com.alzheimer.healthprevention.dto;

import com.alzheimer.healthprevention.entity.Priority;
import com.alzheimer.healthprevention.entity.RecommendationCategory;
import com.alzheimer.healthprevention.entity.RecommendationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HealthRecommendationDTO {
    private Long id;
    private Long healthProfileId;

    @NotNull(message = "Category is required")
    private RecommendationCategory category;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private Priority priority;
    private RecommendationStatus status;
    private Boolean evidenceBased;
    private String targetDate;
    private String completedDate;
    private String createdAt;
    private String updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHealthProfileId() { return healthProfileId; }
    public void setHealthProfileId(Long healthProfileId) { this.healthProfileId = healthProfileId; }

    public RecommendationCategory getCategory() { return category; }
    public void setCategory(RecommendationCategory category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public RecommendationStatus getStatus() { return status; }
    public void setStatus(RecommendationStatus status) { this.status = status; }

    public Boolean getEvidenceBased() { return evidenceBased; }
    public void setEvidenceBased(Boolean evidenceBased) { this.evidenceBased = evidenceBased; }

    public String getTargetDate() { return targetDate; }
    public void setTargetDate(String targetDate) { this.targetDate = targetDate; }

    public String getCompletedDate() { return completedDate; }
    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
