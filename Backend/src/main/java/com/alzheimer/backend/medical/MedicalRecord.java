package com.alzheimer.backend.medical;

import com.alzheimer.backend.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Basic Medical Info
    @Column(name = "age")
    private Integer age;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "education_level", length = 100)
    private String educationLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "family_history")
    private FamilyHistory familyHistory = FamilyHistory.No;

    @Column(name = "risk_factors", columnDefinition = "TEXT")
    private String riskFactors;

    @Column(name = "current_symptoms", columnDefinition = "TEXT")
    private String currentSymptoms;

    @Column(name = "diagnosis_notes", columnDefinition = "TEXT")
    private String diagnosisNotes;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Risk Assessment Fields
    @Column(name = "risk_score")
    private Double riskScore = 0.0;

    @Column(name = "risk_level", length = 50)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel = RiskLevel.LOW;

    @Column(name = "last_risk_calculation")
    private LocalDateTime lastRiskCalculation;

    // Relationships
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RiskFactor> riskFactorsList = new ArrayList<>();

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreventionAction> preventionActions = new ArrayList<>();

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalTimeline> timeline = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public FamilyHistory getFamilyHistory() { return familyHistory; }
    public void setFamilyHistory(FamilyHistory familyHistory) { this.familyHistory = familyHistory; }

    public String getRiskFactors() { return riskFactors; }
    public void setRiskFactors(String riskFactors) { this.riskFactors = riskFactors; }

    public String getCurrentSymptoms() { return currentSymptoms; }
    public void setCurrentSymptoms(String currentSymptoms) { this.currentSymptoms = currentSymptoms; }

    public String getDiagnosisNotes() { return diagnosisNotes; }
    public void setDiagnosisNotes(String diagnosisNotes) { this.diagnosisNotes = diagnosisNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public LocalDateTime getLastRiskCalculation() { return lastRiskCalculation; }
    public void setLastRiskCalculation(LocalDateTime lastRiskCalculation) {
        this.lastRiskCalculation = lastRiskCalculation;
    }

    public List<RiskFactor> getRiskFactorsList() { return riskFactorsList; }
    public void setRiskFactorsList(List<RiskFactor> riskFactorsList) {
        this.riskFactorsList = riskFactorsList;
    }

    public List<PreventionAction> getPreventionActions() { return preventionActions; }
    public void setPreventionActions(List<PreventionAction> preventionActions) {
        this.preventionActions = preventionActions;
    }

    public List<MedicalTimeline> getTimeline() { return timeline; }
    public void setTimeline(List<MedicalTimeline> timeline) { this.timeline = timeline; }
}
