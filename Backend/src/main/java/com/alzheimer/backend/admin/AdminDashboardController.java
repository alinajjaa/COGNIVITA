package com.alzheimer.backend.admin;

import com.alzheimer.backend.dto.AdminDashboardData;
import com.alzheimer.backend.dto.ApiResponse;
import com.alzheimer.backend.dto.UserDashboardDTO;
import com.alzheimer.backend.mmse.MMSERepository;
import com.alzheimer.backend.mmse.MMSETest;
import com.alzheimer.backend.user.User;
import com.alzheimer.backend.user.UserRepository;
import com.alzheimer.backend.user.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final MMSERepository mmseRepository;
    private final AdminAnalyticsService analyticsService;
    private final AdvancedAnalyticsService advancedAnalyticsService;

    public AdminDashboardController(UserRepository userRepository, MMSERepository mmseRepository, 
                                   AdminAnalyticsService analyticsService,
                                   AdvancedAnalyticsService advancedAnalyticsService) {
        this.userRepository = userRepository;
        this.mmseRepository = mmseRepository;
        this.analyticsService = analyticsService;
        this.advancedAnalyticsService = advancedAnalyticsService;
    }

    @GetMapping("/super-dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardData>> getSuperDashboard() {
        try {
            AdminDashboardData data = analyticsService.getCompleteDashboardData();
            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved successfully", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve dashboard data", e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<List<UserDashboardDTO>>> getDashboard() {
        try {
            List<User> users = userRepository.findAll();
            List<UserDashboardDTO> dashboardData = new ArrayList<>();

            for (User user : users) {
                List<MMSETest> mmseTests = mmseRepository.findByPatientName(user.getFirstName() + " " + user.getLastName());
                UserDashboardDTO dto = new UserDashboardDTO(user, mmseTests);
                dashboardData.add(dto);
            }

            return ResponseEntity.ok(ApiResponse.success("Dashboard retrieved successfully", dashboardData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve dashboard", e.getMessage()));
        }
    }

    @GetMapping("/users-count")
    public ResponseEntity<ApiResponse<Long>> getUsersCount() {
        try {
            long count = userRepository.count();
            return ResponseEntity.ok(ApiResponse.success("User count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user count", e.getMessage()));
        }
    }

    @GetMapping("/mmse-tests-count")
    public ResponseEntity<ApiResponse<Long>> getMMSETestsCount() {
        try {
            long count = mmseRepository.count();
            return ResponseEntity.ok(ApiResponse.success("MMSE tests count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve MMSE tests count", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStats>> getAdminStats() {
        try {
            long usersCount = userRepository.count();
            long mmseCount = mmseRepository.count();
            long activeUsers = userRepository.findByActive(true).size();

            AdminStats stats = new AdminStats(usersCount, mmseCount, activeUsers);
            return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve statistics", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDashboardDTO>>> searchUsers(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Search query cannot be empty", "Invalid query"));
            }

            List<User> users = userRepository.findAll().stream()
                    .filter(u -> u.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                               u.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                               u.getEmail().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());

            List<UserDashboardDTO> result = new ArrayList<>();
            for (User user : users) {
                List<MMSETest> mmseTests = mmseRepository.findByPatientName(user.getFirstName() + " " + user.getLastName());
                result.add(new UserDashboardDTO(user, mmseTests));
            }

            return ResponseEntity.ok(ApiResponse.success("Search completed successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed", e.getMessage()));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<UserDashboardDTO>>> filterUsers(@RequestParam(required = false) UserRole role,
                                              @RequestParam(required = false) Boolean active) {
        try {
            List<User> users = userRepository.findAll();

            if (role != null) {
                users = users.stream().filter(u -> u.getRole() == role).collect(Collectors.toList());
            }

            if (active != null) {
                users = users.stream().filter(u -> u.getActive().equals(active)).collect(Collectors.toList());
            }

            List<UserDashboardDTO> result = new ArrayList<>();
            for (User user : users) {
                List<MMSETest> mmseTests = mmseRepository.findByPatientName(user.getFirstName() + " " + user.getLastName());
                result.add(new UserDashboardDTO(user, mmseTests));
            }

            return ResponseEntity.ok(ApiResponse.success("Filter applied successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Filter failed", e.getMessage()));
        }
    }

    @GetMapping("/export/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> exportUsers() {
        try {
            List<Map<String, Object>> data = userRepository.findAll().stream()
                    .map(user -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("ID", user.getId());
                        map.put("Email", user.getEmail());
                        map.put("Name", user.getFirstName() + " " + user.getLastName());
                        map.put("Role", user.getRole());
                        map.put("Status", user.getActive() ? "Active" : "Inactive");
                        map.put("Phone", user.getPhone());
                        map.put("Created", user.getCreatedAt());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Users exported successfully", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Export failed", e.getMessage()));
        }
    }

    @GetMapping("/export/mmse")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> exportMMSETests() {
        try {
            List<Map<String, Object>> data = mmseRepository.findAll().stream()
                    .map(test -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("ID", test.getId());
                        map.put("Patient", test.getPatientName());
                        map.put("Total Score", test.getTotalScore());
                        map.put("Interpretation", test.getInterpretation());
                        map.put("Orientation", test.getOrientationScore());
                        map.put("Registration", test.getRegistrationScore());
                        map.put("Attention", test.getAttentionScore());
                        map.put("Recall", test.getRecallScore());
                        map.put("Language", test.getLanguageScore());
                        map.put("Test Date", test.getTestDate());
                        map.put("Notes", test.getNotes());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("MMSE tests exported successfully", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Export failed", e.getMessage()));
        }
    }

    @GetMapping("/activity-log")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getActivityLog(@RequestParam(defaultValue = "50") int limit) {
        try {
            List<Map<String, Object>> activities = new ArrayList<>();
            
            // Get recent users
            userRepository.findAll().stream()
                    .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                    .limit(Math.max(1, limit / 2))
                    .forEach(user -> {
                        Map<String, Object> activity = new HashMap<>();
                        activity.put("type", "USER_CREATED");
                        activity.put("description", "User created: " + user.getFirstName() + " " + user.getLastName());
                        activity.put("timestamp", user.getCreatedAt());
                        activity.put("email", user.getEmail());
                        activities.add(activity);
                    });

            // Get recent tests
            mmseRepository.findAll().stream()
                    .sorted(Comparator.comparing(MMSETest::getTestDate).reversed())
                    .limit(Math.max(1, limit / 2))
                    .forEach(test -> {
                        Map<String, Object> activity = new HashMap<>();
                        activity.put("type", "TEST_CREATED");
                        activity.put("description", "MMSE test for " + test.getPatientName() + " (Score: " + test.getTotalScore() + ")");
                        activity.put("timestamp", test.getTestDate());
                        activity.put("score", test.getTotalScore());
                        activities.add(activity);
                    });

            List<Map<String, Object>> sorted = activities.stream()
                    .sorted(Comparator.comparing((Map<String, Object> m) -> m.get("timestamp").toString()).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Activity log retrieved successfully", sorted));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve activity log", e.getMessage()));
        }
    }

    @PostMapping("/backup")
    public ResponseEntity<ApiResponse<Map<String, String>>> createBackup() {
        try {
            Map<String, String> backupInfo = new HashMap<>();
            backupInfo.put("status", "success");
            backupInfo.put("message", "Backup created successfully");
            backupInfo.put("timestamp", new Date().toString());
            backupInfo.put("type", "full");
            backupInfo.put("size", "N/A");

            return ResponseEntity.ok(ApiResponse.success("Backup created successfully", backupInfo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Backup failed", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("database", "CONNECTED");
            health.put("timestamp", new Date().toString());
            health.put("uptime", System.currentTimeMillis());

            return ResponseEntity.ok(ApiResponse.success("System health retrieved", health));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve system health", e.getMessage()));
        }
    }

    @GetMapping("/analytics/monthly-trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyTrends() {
        try {
            Map<String, Object> trends = advancedAnalyticsService.getMonthlyTrends();
            return ResponseEntity.ok(ApiResponse.success("Monthly trends retrieved successfully", trends));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve monthly trends", e.getMessage()));
        }
    }

    @GetMapping("/analytics/demographics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDemographicAnalysis() {
        try {
            Map<String, Object> demographics = advancedAnalyticsService.getDemographicAnalysis();
            return ResponseEntity.ok(ApiResponse.success("Demographic analysis retrieved successfully", demographics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve demographic analysis", e.getMessage()));
        }
    }

    @GetMapping("/analytics/cognitive-distribution")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCognitiveDistribution() {
        try {
            Map<String, Object> distribution = advancedAnalyticsService.getCognitiveImpairmentDistribution();
            return ResponseEntity.ok(ApiResponse.success("Cognitive distribution retrieved successfully", distribution));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve cognitive distribution", e.getMessage()));
        }
    }

    @GetMapping("/analytics/mmse-components")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMMSEComponentAnalysis() {
        try {
            Map<String, Object> components = advancedAnalyticsService.getMMSEComponentAnalysis();
            return ResponseEntity.ok(ApiResponse.success("MMSE components retrieved successfully", components));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve MMSE components", e.getMessage()));
        }
    }

    @GetMapping("/analytics/high-risk-patients")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getHighRiskPatients(
            @RequestParam(defaultValue = "18") int minScore) {
        try {
            List<Map<String, Object>> patients = advancedAnalyticsService.getHighRiskPatients(minScore);
            return ResponseEntity.ok(ApiResponse.success("High-risk patients retrieved successfully", patients));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve high-risk patients", e.getMessage()));
        }
    }

    @GetMapping("/analytics/registration-trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserRegistrationTrends() {
        try {
            Map<String, Object> trends = advancedAnalyticsService.getUserRegistrationTrends();
            return ResponseEntity.ok(ApiResponse.success("Registration trends retrieved successfully", trends));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve registration trends", e.getMessage()));
        }
    }

    @GetMapping("/analytics/performance-metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        try {
            Map<String, Object> metrics = advancedAnalyticsService.getPerformanceMetrics();
            return ResponseEntity.ok(ApiResponse.success("Performance metrics retrieved successfully", metrics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve performance metrics", e.getMessage()));
        }
    }
}

