package com.alzheimer.backend.admin;

import com.alzheimer.backend.mmse.MMSERepository;
import com.alzheimer.backend.mmse.MMSETest;
import com.alzheimer.backend.user.User;
import com.alzheimer.backend.user.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdvancedAnalyticsService {

    private final UserRepository userRepository;
    private final MMSERepository mmseRepository;

    public AdvancedAnalyticsService(UserRepository userRepository, MMSERepository mmseRepository) {
        this.userRepository = userRepository;
        this.mmseRepository = mmseRepository;
    }

    /**
     * Get monthly trend data for MMSE tests
     */
    public Map<String, Object> getMonthlyTrends() {
        List<MMSETest> allTests = mmseRepository.findAll();
        Map<YearMonth, List<MMSETest>> testsByMonth = allTests.stream()
                .filter(t -> t.getTestDate() != null)
                .collect(Collectors.groupingBy(t -> YearMonth.from(t.getTestDate())));

        Map<String, Object> trends = new HashMap<>();
        Map<String, Double> monthlyAverages = new HashMap<>();

        testsByMonth.forEach((month, tests) -> {
            double average = tests.stream()
                    .mapToInt(MMSETest::getTotalScore)
                    .average()
                    .orElse(0.0);
            monthlyAverages.put(month.toString(), average);
        });

        trends.put("monthlyAverages", monthlyAverages);
        trends.put("totalMonths", monthlyAverages.size());
        return trends;
    }

    /**
     * Get user demographic analysis
     */
    public Map<String, Object> getDemographicAnalysis() {
        List<User> allUsers = userRepository.findAll();
        Map<String, Object> demographics = new HashMap<>();

        demographics.put("totalUsers", allUsers.size());
        demographics.put("activeUsers", allUsers.stream().filter(User::getActive).count());
        demographics.put("inactiveUsers", allUsers.stream().filter(u -> !u.getActive()).count());
        demographics.put("adminUsers", allUsers.stream().filter(u -> u.getRole().toString().equals("ADMIN")).count());
        demographics.put("doctorUsers", allUsers.stream().filter(u -> u.getRole().toString().equals("DOCTOR")).count());
        demographics.put("patientUsers", allUsers.stream().filter(u -> u.getRole().toString().equals("USER")).count());

        return demographics;
    }

    /**
     * Get cognitive impairment distribution
     */
    public Map<String, Object> getCognitiveImpairmentDistribution() {
        List<MMSETest> allTests = mmseRepository.findAll();
        Map<String, Long> distribution = new HashMap<>();

        distribution.put("Normal (24-30)", allTests.stream()
                .filter(t -> t.getTotalScore() >= 24)
                .count());

        distribution.put("Mild (18-23)", allTests.stream()
                .filter(t -> t.getTotalScore() >= 18 && t.getTotalScore() < 24)
                .count());

        distribution.put("Moderate (11-17)", allTests.stream()
                .filter(t -> t.getTotalScore() >= 11 && t.getTotalScore() < 18)
                .count());

        distribution.put("Severe (0-10)", allTests.stream()
                .filter(t -> t.getTotalScore() < 11)
                .count());

        Map<String, Object> result = new HashMap<>();
        result.put("distribution", distribution);
        result.put("totalTests", allTests.size());
        return result;
    }

    /**
     * Get MMSE component analysis (average scores for each component)
     */
    public Map<String, Object> getMMSEComponentAnalysis() {
        List<MMSETest> allTests = mmseRepository.findAll();
        Map<String, Object> components = new HashMap<>();

        if (allTests.isEmpty()) {
            return components;
        }

        components.put("orientation", allTests.stream()
                .mapToInt(MMSETest::getOrientationScore)
                .average()
                .orElse(0.0));

        components.put("registration", allTests.stream()
                .mapToInt(MMSETest::getRegistrationScore)
                .average()
                .orElse(0.0));

        components.put("attention", allTests.stream()
                .mapToInt(MMSETest::getAttentionScore)
                .average()
                .orElse(0.0));

        components.put("recall", allTests.stream()
                .mapToInt(MMSETest::getRecallScore)
                .average()
                .orElse(0.0));

        components.put("language", allTests.stream()
                .mapToInt(MMSETest::getLanguageScore)
                .average()
                .orElse(0.0));

        return components;
    }

    /**
     * Get high-risk patients (low MMSE scores)
     */
    public List<Map<String, Object>> getHighRiskPatients(int minScore) {
        return mmseRepository.findAll().stream()
                .filter(t -> t.getTotalScore() <= minScore)
                .map(test -> {
                    Map<String, Object> patient = new HashMap<>();
                    patient.put("patientName", test.getPatientName());
                    patient.put("score", test.getTotalScore());
                    patient.put("interpretation", test.getInterpretation());
                    patient.put("testDate", test.getTestDate());
                    patient.put("riskLevel", test.getTotalScore() < 11 ? "Critical" : "High");
                    return patient;
                })
                .sorted(Comparator.comparingInt((Map<String, Object> m) -> (Integer) m.get("score")))
                .collect(Collectors.toList());
    }

    /**
     * Get user registration trends
     */
    public Map<String, Object> getUserRegistrationTrends() {
        List<User> allUsers = userRepository.findAll();
        Map<LocalDate, Long> registrationsByDate = allUsers.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        Map<String, Object> trends = new HashMap<>();
        trends.put("registrationsByDate", registrationsByDate);
        trends.put("totalDates", registrationsByDate.size());
        return trends;
    }

    /**
     * Get system performance metrics
     */
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        long totalUsers = userRepository.count();
        long totalTests = mmseRepository.count();

        metrics.put("totalUsers", totalUsers);
        metrics.put("totalTests", totalTests);
        metrics.put("avgTestsPerUser", totalUsers > 0 ? (double) totalTests / totalUsers : 0);
        metrics.put("memoryUsage", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024);
        metrics.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024);

        return metrics;
    }
}
