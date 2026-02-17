package com.alzheimer.backend.medical;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_factors")
public class RiskFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "factor_type", nullable = false, length = 100)
    private String factorType; // e.g., "Hypertension", "Diabetes", "Smoking", "Obesity"

    @Column(name = "severity", length = 50)
    @Enumerated(EnumType.STRING)
    private Severity severity = Severity.MEDIUM;

    @Column(name = "diagnosed_date")
    private LocalDateTime diagnosedDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_active")
    private Boolean isActive = true;

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

    public String getFactorType() { return factorType; }
    public void setFactorType(String factorType) { this.factorType = factorType; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public LocalDateTime getDiagnosedDate() { return diagnosedDate; }
    public void setDiagnosedDate(LocalDateTime diagnosedDate) { this.diagnosedDate = diagnosedDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
