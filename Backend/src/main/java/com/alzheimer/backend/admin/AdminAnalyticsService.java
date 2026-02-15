package com.alzheimer.backend.admin;

import com.alzheimer.backend.dto.AdminDashboardData;
import com.alzheimer.backend.mmse.MMSERepository;
import com.alzheimer.backend.mmse.MMSETest;
import com.alzheimer.backend.user.User;
import com.alzheimer.backend.user.UserRepository;
import com.alzheimer.backend.user.UserRole;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsService {

    private final UserRepository userRepository;
    private final MMSERepository mmseRepository;
    private static final long START_TIME = System.currentTimeMillis();

    public AdminAnalyticsService(UserRepository userRepository, MMSERepository mmseRepository) {
        this.userRepository = userRepository;
        this.mmseRepository = mmseRepository;
    }

    public AdminDashboardData getCompleteDashboardData() {
        DashboardStats stats = calculateDashboardStats();
        List<ChartData> mmseDistribution = calculateMMSEScoreDistribution();
        List<ChartData> roleDistribution = calculateUserRoleDistribution();
        List<RecentActivity> recentActivities = generateRecentActivities();
        List<MMSETestAnalytics> testAnalytics = calculateMMSETestAnalytics();
        SystemHealth systemHealth = getSystemHealth();

        return new AdminDashboardData(stats, mmseDistribution, roleDistribution, 
                                    recentActivities, testAnalytics, systemHealth);
    }

    private DashboardStats calculateDashboardStats() {
        List<User> allUsers = userRepository.findAll();
        List<MMSETest> allTests = mmseRepository.findAll();

        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().filter(User::getActive).count();
        long inactiveUsers = totalUsers - activeUsers;
        long totalAdmins = allUsers.stream().filter(u -> u.getRole() == UserRole.ADMIN).count();
        long totalDoctors = allUsers.stream().filter(u -> u.getRole() == UserRole.DOCTOR).count();
        long totalPatients = allUsers.stream().filter(u -> u.getRole() == UserRole.USER).count();

        long totalTests = allTests.size();
        double averageScore = allTests.stream()
                .mapToInt(MMSETest::getTotalScore)
                .average()
                .orElse(0.0);

        long normalCount = allTests.stream()
                .filter(t -> t.getInterpretation() != null && t.getInterpretation().contains("Normal"))
                .count();
        long mildCount = allTests.stream()
                .filter(t -> t.getInterpretation() != null && t.getInterpretation().contains("Mild"))
                .count();
        long moderateCount = allTests.stream()
                .filter(t -> t.getInterpretation() != null && t.getInterpretation().contains("Moderate"))
                .count();
        long severeCount = allTests.stream()
                .filter(t -> t.getInterpretation() != null && t.getInterpretation().contains("Severe"))
                .count();

        long testsThisMonth = allTests.stream()
                .filter(t -> t.getTestDate() != null && t.getTestDate().getYear() == LocalDate.now().getYear()
                        && t.getTestDate().getMonth() == LocalDate.now().getMonth())
                .count();

        return new DashboardStats(totalUsers, activeUsers, inactiveUsers, totalAdmins, totalDoctors,
                                totalPatients, totalTests, averageScore, normalCount, mildCount,
                                moderateCount, severeCount, testsThisMonth);
    }

    private List<ChartData> calculateMMSEScoreDistribution() {
        List<MMSETest> allTests = mmseRepository.findAll();
        Map<String, Integer> distribution = new HashMap<>();
        
        for (MMSETest test : allTests) {
            int score = test.getTotalScore();
            String range;
            if (score >= 24) range = "24-30 (Normal)";
            else if (score >= 18) range = "18-23 (Mild)";
            else if (score >= 10) range = "10-17 (Moderate)";
            else range = "0-9 (Severe)";
            
            distribution.put(range, distribution.getOrDefault(range, 0) + 1);
        }

        return distribution.entrySet().stream()
                .map(e -> {
                    String color = e.getKey().startsWith("24") ? "#10b981" :
                                   e.getKey().startsWith("18") ? "#f59e0b" :
                                   e.getKey().startsWith("10") ? "#ef4444" : "#dc2626";
                    return new ChartData(e.getKey(), e.getValue(), color);
                })
                .collect(Collectors.toList());
    }

    private List<ChartData> calculateUserRoleDistribution() {
        List<User> allUsers = userRepository.findAll();
        Map<String, Integer> distribution = new HashMap<>();

        for (User user : allUsers) {
            String role = user.getRole().toString();
            distribution.put(role, distribution.getOrDefault(role, 0) + 1);
        }

        Map<String, String> colors = Map.of(
                "ADMIN", "#8b5cf6",
                "DOCTOR", "#3b82f6",
                "USER", "#06b6d4"
        );

        return distribution.entrySet().stream()
                .map(e -> new ChartData(e.getKey(), e.getValue(), colors.getOrDefault(e.getKey(), "#6366f1")))
                .collect(Collectors.toList());
    }

    private List<RecentActivity> generateRecentActivities() {
        List<RecentActivity> activities = new ArrayList<>();
        
        // Get recent users
        List<User> recentUsers = userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .limit(3)
                .toList();

        for (User user : recentUsers) {
            activities.add(new RecentActivity("USER_CREATED", 
                    "User '" + user.getFirstName() + " " + user.getLastName() + "' created",
                    user.getCreatedAt().toString(), "👤"));
        }

        // Get recent tests
        List<MMSETest> recentTests = mmseRepository.findAll().stream()
                .sorted(Comparator.comparing(MMSETest::getTestDate).reversed())
                .limit(2)
                .toList();

        for (MMSETest test : recentTests) {
            activities.add(new RecentActivity("TEST_CREATED",
                    "MMSE test created for '" + test.getPatientName() + "' with score " + test.getTotalScore(),
                    test.getTestDate() != null ? test.getTestDate().toString() : "Unknown", "📋"));
        }

        return activities;
    }

    private List<MMSETestAnalytics> calculateMMSETestAnalytics() {
        List<MMSETest> allTests = mmseRepository.findAll();
        
        Map<String, List<MMSETest>> testsByPatient = allTests.stream()
                .collect(Collectors.groupingBy(MMSETest::getPatientName));

        return testsByPatient.entrySet().stream()
                .map(entry -> {
                    String patientName = entry.getKey();
                    List<MMSETest> tests = entry.getValue();
                    
                    long testCount = tests.size();
                    double averageScore = tests.stream()
                            .mapToInt(MMSETest::getTotalScore)
                            .average()
                            .orElse(0.0);
                    int highestScore = tests.stream()
                            .mapToInt(MMSETest::getTotalScore)
                            .max()
                            .orElse(0);
                    int lowestScore = tests.stream()
                            .mapToInt(MMSETest::getTotalScore)
                            .min()
                            .orElse(0);
                    
                    String trend = "STABLE";
                    if (tests.size() > 1) {
                        int latest = tests.get(tests.size() - 1).getTotalScore();
                        int previous = tests.get(tests.size() - 2).getTotalScore();
                        if (latest > previous) trend = "UP";
                        else if (latest < previous) trend = "DOWN";
                    }

                    return new MMSETestAnalytics(patientName, testCount, averageScore, highestScore, lowestScore, trend);
                })
                .collect(Collectors.toList());
    }

    private SystemHealth getSystemHealth() {
        try {
            long uptime = (System.currentTimeMillis() - START_TIME) / 1000;
            double cpuUsage = calculateCPUUsage();
            double memoryUsage = calculateMemoryUsage();
            String status = memoryUsage > 80 ? "WARNING" : memoryUsage > 90 ? "CRITICAL" : "HEALTHY";

            return new SystemHealth(status, uptime, cpuUsage, memoryUsage, "CONNECTED", LocalDate.now().toString());
        } catch (Exception e) {
            return new SystemHealth("UNKNOWN", 0, 0, 0, "ERROR", "Unknown");
        }
    }

    private double calculateCPUUsage() {
        return Math.random() * 50 + 10; // Simulated CPU usage
    }

    private double calculateMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        return (double) usedMemory / maxMemory * 100;
    }
}
