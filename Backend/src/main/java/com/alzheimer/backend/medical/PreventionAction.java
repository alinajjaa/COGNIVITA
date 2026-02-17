package com.alzheimer.backend.medical;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prevention_actions")
public class PreventionAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType; // e.g., "Exercise", "Medication", "Diet Change", "Cognitive Training"

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "action_date", nullable = false)
    private LocalDateTime actionDate;

    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private ActionStatus status = ActionStatus.PENDING;

    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    @Column(name = "frequency", length = 50)
    private String frequency; // e.g., "Daily", "Weekly", "Monthly"

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) { this.medicalRecord = medicalRecord; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }

    public ActionStatus getStatus() { return status; }
    public void setStatus(ActionStatus status) { this.status = status; }

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
