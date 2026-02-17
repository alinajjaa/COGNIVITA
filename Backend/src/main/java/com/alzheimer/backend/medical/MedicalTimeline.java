package com.alzheimer.backend.medical;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_timeline")
public class MedicalTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "event_type", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "changed_fields", columnDefinition = "TEXT")
    private String changedFields; // JSON format of what changed

    @Column(name = "performed_by", length = 100)
    private String performedBy; // User who made the change

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) { this.medicalRecord = medicalRecord; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getChangedFields() { return changedFields; }
    public void setChangedFields(String changedFields) { this.changedFields = changedFields; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
