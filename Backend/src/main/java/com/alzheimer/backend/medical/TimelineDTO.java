package com.alzheimer.backend.medical;

import java.time.LocalDateTime;

public class TimelineDTO {
    private Long id;
    private Long medicalRecordId;
    private LocalDateTime eventDate;
    private String eventType;
    private String description;
    private String changedFields;
    private String performedBy;
    private LocalDateTime createdAt;

    public TimelineDTO() {}

    public TimelineDTO(MedicalTimeline t) {
        this.id = t.getId();
        this.medicalRecordId = t.getMedicalRecord() != null ? t.getMedicalRecord().getId() : null;
        this.eventDate = t.getEventDate();
        this.eventType = t.getEventType() != null ? t.getEventType().name() : null;
        this.description = t.getDescription();
        this.changedFields = t.getChangedFields();
        this.performedBy = t.getPerformedBy();
        this.createdAt = t.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMedicalRecordId() { return medicalRecordId; }
    public void setMedicalRecordId(Long medicalRecordId) { this.medicalRecordId = medicalRecordId; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getChangedFields() { return changedFields; }
    public void setChangedFields(String changedFields) { this.changedFields = changedFields; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
