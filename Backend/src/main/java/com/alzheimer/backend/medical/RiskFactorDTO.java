package com.alzheimer.backend.medical;

import java.time.LocalDateTime;

public class RiskFactorDTO {
    private Long id;
    private Long medicalRecordId;
    private String factorType;
    private String severity;
    private LocalDateTime diagnosedDate;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RiskFactorDTO() {}

    public RiskFactorDTO(RiskFactor rf) {
        this.id = rf.getId();
        this.medicalRecordId = rf.getMedicalRecord() != null ? rf.getMedicalRecord().getId() : null;
        this.factorType = rf.getFactorType();
        this.severity = rf.getSeverity() != null ? rf.getSeverity().name() : null;
        this.diagnosedDate = rf.getDiagnosedDate();
        this.notes = rf.getNotes();
        this.isActive = rf.getIsActive();
        this.createdAt = rf.getCreatedAt();
        this.updatedAt = rf.getUpdatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMedicalRecordId() { return medicalRecordId; }
    public void setMedicalRecordId(Long medicalRecordId) { this.medicalRecordId = medicalRecordId; }
    public String getFactorType() { return factorType; }
    public void setFactorType(String factorType) { this.factorType = factorType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
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
