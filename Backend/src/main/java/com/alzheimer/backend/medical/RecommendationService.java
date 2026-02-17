package com.alzheimer.backend.medical;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    /**
     * Generate personalized prevention recommendations based on risk profile
     */
    public List<String> generateRecommendations(MedicalRecord record, double riskScore) {
        List<String> recommendations = new ArrayList<>();

        // Critical Risk Level (76-100)
        if (riskScore > 75) {
            recommendations.add("🔴 URGENT: Immediate consultation with a neurologist is strongly recommended");
            recommendations.add("🧠 Schedule comprehensive cognitive assessment (MMSE, MoCA tests)");
            recommendations.add("🏥 Consider referral to memory clinic or specialized care center");
            recommendations.add("💊 Discuss potential pharmacological interventions with your physician");
            recommendations.add("👨‍👩‍👧 Family education on Alzheimer's care and support strategies");
        }
        // High Risk Level (51-75)
        else if (riskScore > 50) {
            recommendations.add("⚠️ Schedule appointment with neurologist within next month");
            recommendations.add("🧠 Annual cognitive screening recommended (MMSE, Clock Drawing Test)");
            recommendations.add("🏃‍♂️ Engage in moderate physical exercise 5 times per week (30 min)");
            recommendations.add("🥗 Follow Mediterranean or MIND diet strictly");
            recommendations.add("🧘‍♀️ Practice stress reduction techniques daily (meditation, yoga)");
            recommendations.add("🎯 Daily cognitive training exercises (puzzles, memory games)");
        }
        // Medium Risk Level (26-50)
        else if (riskScore > 25) {
            recommendations.add("📅 Annual check-up with primary care physician recommended");
            recommendations.add("🧠 Bi-annual cognitive health screening");
            recommendations.add("🏃‍♂️ Regular physical activity: 150 min/week moderate exercise");
            recommendations.add("🥗 Maintain balanced diet rich in omega-3, antioxidants");
            recommendations.add("😴 Ensure 7-8 hours of quality sleep per night");
            recommendations.add("🧩 Engage in mentally stimulating activities regularly");
            recommendations.add("👥 Maintain active social life and relationships");
        }
        // Low Risk Level (0-25)
        else {
            recommendations.add("✅ Continue maintaining healthy lifestyle habits");
            recommendations.add("🏃‍♂️ Regular physical activity: at least 150 min/week");
            recommendations.add("🥗 Balanced nutrition with emphasis on brain-healthy foods");
            recommendations.add("🧠 Keep mind active: reading, learning new skills, hobbies");
            recommendations.add("😴 Maintain good sleep hygiene");
            recommendations.add("📅 Routine health check-ups as per age guidelines");
        }

        // Age-specific recommendations
        if (record.getAge() != null) {
            if (record.getAge() > 65) {
                recommendations.add("💉 Ensure vaccinations are up-to-date (flu, pneumonia)");
                recommendations.add("🩺 Monitor cardiovascular health closely");
                recommendations.add("💊 Regular medication review to prevent cognitive side effects");
            }
            if (record.getAge() > 50) {
                recommendations.add("🔍 Monitor blood pressure and cholesterol levels");
                recommendations.add("🍷 Limit alcohol consumption (max 1 drink/day)");
            }
        }

        // Family history specific
        if (record.getFamilyHistory() == FamilyHistory.Yes) {
            recommendations.add("🧬 Consider genetic counseling and testing if not already done");
            recommendations.add("📚 Educate yourself about early warning signs");
            recommendations.add("🗓️ More frequent cognitive monitoring than average-risk individuals");
        }

        // Risk factor specific
        if (record.getRiskFactorsList() != null && !record.getRiskFactorsList().isEmpty()) {
            boolean hasCardiovascular = record.getRiskFactorsList().stream()
                .anyMatch(rf -> rf.getFactorType().toLowerCase().contains("hypertension") ||
                              rf.getFactorType().toLowerCase().contains("diabetes") ||
                              rf.getFactorType().toLowerCase().contains("cardiovascular"));
            
            if (hasCardiovascular) {
                recommendations.add("❤️ Strict management of cardiovascular risk factors is essential");
                recommendations.add("💊 Ensure medications for chronic conditions are taken as prescribed");
            }
        }

        // Symptom-based recommendations
        if (record.getCurrentSymptoms() != null && !record.getCurrentSymptoms().trim().isEmpty()) {
            String symptoms = record.getCurrentSymptoms().toLowerCase();
            if (symptoms.contains("memory") || symptoms.contains("confusion")) {
                recommendations.add("📝 Keep a daily journal to track cognitive changes");
                recommendations.add("⏰ Use calendars, reminders, and organizational tools");
            }
            if (symptoms.contains("sleep") || symptoms.contains("insomnia")) {
                recommendations.add("😴 Establish consistent sleep schedule and bedtime routine");
                recommendations.add("🛌 Create optimal sleep environment (dark, cool, quiet)");
            }
        }

        // General brain health recommendations
        recommendations.add("🚭 Avoid smoking and secondhand smoke");
        recommendations.add("🎵 Engage in activities you enjoy (music, art, social events)");
        recommendations.add("📖 Continuous learning: take classes, read, learn new skills");

        return recommendations;
    }

    /**
     * Generate lifestyle modification recommendations
     */
    public List<String> generateLifestyleRecommendations(MedicalRecord record) {
        List<String> recommendations = new ArrayList<>();

        recommendations.add("Exercise: Aerobic activity 30 min, 5 days/week");
        recommendations.add("Diet: Mediterranean diet with plenty of vegetables, fruits, fish");
        recommendations.add("Sleep: Aim for 7-9 hours of quality sleep");
        recommendations.add("Mental Stimulation: Daily puzzles, reading, learning");
        recommendations.add("Social Engagement: Regular interaction with friends and family");
        recommendations.add("Stress Management: Mindfulness, meditation, relaxation techniques");

        return recommendations;
    }

    /**
     * Generate preventive action suggestions
     */
    public List<PreventionActionSuggestion> suggestPreventionActions(MedicalRecord record, double riskScore) {
        List<PreventionActionSuggestion> suggestions = new ArrayList<>();

        if (riskScore > 50) {
            suggestions.add(new PreventionActionSuggestion(
                "Cognitive Training",
                "Daily cognitive exercises (20-30 minutes)",
                "Daily",
                "High Priority"
            ));
            suggestions.add(new PreventionActionSuggestion(
                "Medical Follow-up",
                "Schedule neurologist appointment",
                "One-time",
                "High Priority"
            ));
        }

        suggestions.add(new PreventionActionSuggestion(
            "Physical Exercise",
            "Moderate aerobic exercise (walking, swimming, cycling)",
            "5 times per week",
            riskScore > 50 ? "High Priority" : "Medium Priority"
        ));

        suggestions.add(new PreventionActionSuggestion(
            "Diet Modification",
            "Follow Mediterranean or MIND diet",
            "Daily",
            "Medium Priority"
        ));

        suggestions.add(new PreventionActionSuggestion(
            "Social Activity",
            "Engage in group activities or social events",
            "Weekly",
            "Medium Priority"
        ));

        return suggestions;
    }

    // Inner class for action suggestions
    public static class PreventionActionSuggestion {
        private String actionType;
        private String description;
        private String frequency;
        private String priority;

        public PreventionActionSuggestion(String actionType, String description, 
                                         String frequency, String priority) {
            this.actionType = actionType;
            this.description = description;
            this.frequency = frequency;
            this.priority = priority;
        }

        // Getters
        public String getActionType() { return actionType; }
        public String getDescription() { return description; }
        public String getFrequency() { return frequency; }
        public String getPriority() { return priority; }
    }
}
