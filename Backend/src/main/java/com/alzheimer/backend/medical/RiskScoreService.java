package com.alzheimer.backend.medical;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RiskScoreService {

    /**
     * Calculate comprehensive risk score based on multiple factors
     * Score range: 0-100
     */
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

        // 5. Risk Factors from list (max 25 points)
        score += calculateRiskFactorsScore(record.getRiskFactorsList());

        // 6. Symptoms presence (max 20 points)
        score += calculateSymptomsScore(record.getCurrentSymptoms());

        // Cap the score between 0 and 100
        score = Math.max(0, Math.min(100, score));

        return Math.round(score * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Age is a significant risk factor for Alzheimer's
     */
    private double calculateAgeFactor(Integer age) {
        if (age == null) return 0;

        if (age < 50) return 0;
        else if (age >= 50 && age < 60) return 5;
        else if (age >= 60 && age < 65) return 10;
        else if (age >= 65 && age < 70) return 15;
        else if (age >= 70 && age < 75) return 20;
        else if (age >= 75 && age < 80) return 25;
        else return 30; // 80+
    }

    /**
     * Family history is a strong predictor
     */
    private double calculateFamilyHistoryFactor(FamilyHistory familyHistory) {
        if (familyHistory == FamilyHistory.Yes) {
            return 25;
        }
        return 0;
    }

    /**
     * Gender and age interaction (women have slightly higher risk after 65)
     */
    private double calculateGenderFactor(Gender gender, Integer age) {
        if (gender == Gender.Female && age != null && age > 65) {
            return 5;
        }
        if (gender == Gender.Male && age != null && age > 70) {
            return 3;
        }
        return 0;
    }

    /**
     * Higher education is protective
     */
    private double calculateEducationFactor(String educationLevel) {
        if (educationLevel == null || educationLevel.isEmpty()) return 0;

        String level = educationLevel.toLowerCase();
        if (level.contains("phd") || level.contains("doctorate") || level.contains("master")) {
            return -10;
        } else if (level.contains("bachelor") || level.contains("college")) {
            return -5;
        } else if (level.contains("high school")) {
            return -2;
        }
        return 0;
    }

    /**
     * Calculate score from individual risk factors
     */
    private double calculateRiskFactorsScore(List<RiskFactor> factors) {
        if (factors == null || factors.isEmpty()) return 0;

        double score = 0;
        int activeFactors = 0;

        for (RiskFactor factor : factors) {
            if (Boolean.TRUE.equals(factor.getIsActive())) {
                activeFactors++;
                
                // Base score per factor
                double factorScore = 3;

                // Adjust by severity
                switch (factor.getSeverity()) {
                    case LOW:
                        factorScore = 2;
                        break;
                    case MEDIUM:
                        factorScore = 3;
                        break;
                    case HIGH:
                        factorScore = 5;
                        break;
                    case CRITICAL:
                        factorScore = 7;
                        break;
                }

                // Extra weight for specific high-risk factors
                String type = factor.getFactorType().toLowerCase();
                if (type.contains("diabetes") || type.contains("hypertension") || 
                    type.contains("cardiovascular")) {
                    factorScore *= 1.3;
                }

                score += factorScore;
            }
        }

        // Cap at 25 points
        return Math.min(25, score);
    }

    /**
     * Presence of symptoms increases risk
     */
    private double calculateSymptomsScore(String symptoms) {
        if (symptoms == null || symptoms.trim().isEmpty()) return 0;

        String symptomsLower = symptoms.toLowerCase();
        double score = 5; // Base score for having any symptoms

        // Check for critical symptoms
        String[] criticalSymptoms = {
            "memory loss", "confusion", "disorientation", 
            "difficulty speaking", "personality change",
            "poor judgment", "withdrawal", "mood changes"
        };

        for (String symptom : criticalSymptoms) {
            if (symptomsLower.contains(symptom)) {
                score += 2;
            }
        }

        // Cap at 20 points
        return Math.min(20, score);
    }

    /**
     * Determine risk level based on score
     */
    public RiskLevel determineRiskLevel(double score) {
        if (score <= 25) return RiskLevel.LOW;
        else if (score <= 50) return RiskLevel.MEDIUM;
        else if (score <= 75) return RiskLevel.HIGH;
        else return RiskLevel.CRITICAL;
    }

    /**
     * Update risk score for a medical record
     */
    public void updateRiskScore(MedicalRecord record) {
        double score = calculateRiskScore(record);
        record.setRiskScore(score);
        record.setRiskLevel(determineRiskLevel(score));
        record.setLastRiskCalculation(LocalDateTime.now());
    }
}
