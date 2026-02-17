package com.alzheimer.backend.medical;

import java.time.LocalDateTime;

public class PreventionActionDTO {
    private Long id;
    private Long medicalRecordId;
    private String actionType;
    private String description;
    private LocalDateTime actionDate;
    private String status;
    private String result;
    private String frequency;
    private LocalDateTime completedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PreventionActionDTO() {}

    public PreventionActionDTO(PreventionAction pa) {
        this.id = pa.getId();
        this.medicalRecordId = pa.getMedicalRecord() != null ? pa.getMedicalRecord().getId() : null;
        this.actionType = pa.getActionType();
        this.description = pa.getDescription();
        this.actionDate = pa.getActionDate();
        this.status = pa.getStatus() != null ? pa.getStatus().name() : null;
        this.result = pa.getResult();
        this.frequency = pa.getFrequency();
        this.completedDate = pa.getCompletedDate();
        this.createdAt = pa.getCreatedAt();
        this.updatedAt = pa.getUpdatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMedicalRecordId() { return medicalRecordId; }
    public void setMedicalRecordId(Long medicalRecordId) { this.medicalRecordId = medicalRecordId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
