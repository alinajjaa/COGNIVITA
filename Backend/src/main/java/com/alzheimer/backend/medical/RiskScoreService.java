package com.alzheimer.backend.medical;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RiskScoreService {

    private final RiskFactorRepository riskFactorRepository;

    public RiskScoreService(RiskFactorRepository riskFactorRepository) {
        this.riskFactorRepository = riskFactorRepository;
    }

    /**
     * Calculate comprehensive risk score based on multiple factors.
     * Loads risk factors directly from DB to avoid LazyInitializationException.
     * Score range: 0-100
     */
    @Transactional(readOnly = true)
    public double calculateRiskScore(MedicalRecord record) {
        double score = 0;

        // 1. Age Factor (max 30 points)
        score += calculateAgeFactor(record.getAge());

        // 2. Family History (max 25 points)
        score += calculateFamilyHistoryFactor(record.getFamilyHistory());

        // 3. Gender Factor (max 10 points)
        score += calculateGenderFactor(record.getGender(), record.getAge());

        // 4. Education Level (protective factor, -10 to 0 points)
        score += calculateEducationFactor(record.getEducationLevel());

        // 5. Risk Factors - loaded directly from DB (avoids lazy loading issue)
        if (record.getId() != null) {
            List<RiskFactor> factors = riskFactorRepository.findByMedicalRecordId(record.getId());
            score += calculateRiskFactorsScore(factors);
        }

        // 6. Symptoms presence (max 20 points)
        score += calculateSymptomsScore(record.getCurrentSymptoms());

        // Cap the score between 0 and 100
        score = Math.max(0, Math.min(100, score));

        return Math.round(score * 100.0) / 100.0;
    }

    private double calculateAgeFactor(Integer age) {
        if (age == null) return 0;
        if (age < 50) return 0;
        else if (age < 60) return 5;
        else if (age < 65) return 10;
        else if (age < 70) return 15;
        else if (age < 75) return 20;
        else if (age < 80) return 25;
        else return 30;
    }

    private double calculateFamilyHistoryFactor(FamilyHistory familyHistory) {
        return (familyHistory == FamilyHistory.Yes) ? 25 : 0;
    }

    private double calculateGenderFactor(Gender gender, Integer age) {
        if (gender == Gender.Female && age != null && age > 65) return 5;
        if (gender == Gender.Male && age != null && age > 70) return 3;
        return 0;
    }

    private double calculateEducationFactor(String educationLevel) {
        if (educationLevel == null || educationLevel.isEmpty()) return 0;
        String level = educationLevel.toLowerCase();
        if (level.contains("phd") || level.contains("doctorate") || level.contains("master")) return -10;
        else if (level.contains("bachelor") || level.contains("college")) return -5;
        else if (level.contains("high school")) return -2;
        return 0;
    }

    private double calculateRiskFactorsScore(List<RiskFactor> factors) {
        if (factors == null || factors.isEmpty()) return 0;
        double score = 0;
        for (RiskFactor factor : factors) {
            if (Boolean.TRUE.equals(factor.getIsActive())) {
                double factorScore;
                switch (factor.getSeverity()) {
                    case LOW:      factorScore = 2; break;
                    case MEDIUM:   factorScore = 3; break;
                    case HIGH:     factorScore = 5; break;
                    case CRITICAL: factorScore = 7; break;
                    default:       factorScore = 3; break;
                }
                String type = factor.getFactorType() != null ? factor.getFactorType().toLowerCase() : "";
                if (type.contains("diabetes") || type.contains("hypertension") || type.contains("cardiovascular")) {
                    factorScore *= 1.3;
                }
                score += factorScore;
            }
        }
        return Math.min(25, score);
    }

    private double calculateSymptomsScore(String symptoms) {
        if (symptoms == null || symptoms.trim().isEmpty()) return 0;
        String symptomsLower = symptoms.toLowerCase();
        double score = 5;
        String[] criticalSymptoms = {
            "memory loss", "confusion", "disorientation",
            "difficulty speaking", "personality change",
            "poor judgment", "withdrawal", "mood changes"
        };
        for (String symptom : criticalSymptoms) {
            if (symptomsLower.contains(symptom)) score += 2;
        }
        return Math.min(20, score);
    }

    public RiskLevel determineRiskLevel(double score) {
        if (score <= 25) return RiskLevel.LOW;
        else if (score <= 50) return RiskLevel.MEDIUM;
        else if (score <= 75) return RiskLevel.HIGH;
        else return RiskLevel.CRITICAL;
    }

    @Transactional
    public void updateRiskScore(MedicalRecord record) {
        double score = calculateRiskScore(record);
        record.setRiskScore(score);
        record.setRiskLevel(determineRiskLevel(score));
        record.setLastRiskCalculation(LocalDateTime.now());
    }
}
