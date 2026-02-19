package com.alzheimer.backend.medical;

import com.alzheimer.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final MedicalRecordRepository medicalRecordRepository;
    private final RiskFactorRepository riskFactorRepository;
    private final PreventionActionRepository preventionActionRepository;
    private final TimelineRepository timelineRepository;

    public StatisticsController(MedicalRecordRepository medicalRecordRepository,
                                RiskFactorRepository riskFactorRepository,
                                PreventionActionRepository preventionActionRepository,
                                TimelineRepository timelineRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.riskFactorRepository = riskFactorRepository;
        this.preventionActionRepository = preventionActionRepository;
        this.timelineRepository = timelineRepository;
    }

    @GetMapping("/medical-record/{medicalRecordId}/risk-evolution")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRiskEvolution(
            @PathVariable Long medicalRecordId) {
        try {
            Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(medicalRecordId);
            if (recordOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Record not found", "No record with ID: " + medicalRecordId));
            }
            
            MedicalRecord record = recordOpt.get();
            List<MedicalTimeline> events = timelineRepository
                    .findByMedicalRecordIdOrderByEventDateDesc(medicalRecordId);
            
            List<Map<String, Object>> dataPoints = new ArrayList<>();
            double currentScore = record.getRiskScore() != null ? record.getRiskScore() : 0;
            
            dataPoints.add(Map.of(
                "date", record.getLastRiskCalculation() != null 
                    ? record.getLastRiskCalculation().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "score", currentScore,
                "label", "Current"
            ));
            
            Collections.reverse(events);
            double simulatedScore = currentScore > 20 ? currentScore - 10 : currentScore;
            for (int i = 0; i < Math.min(events.size(), 10); i++) {
                MedicalTimeline event = events.get(i);
                dataPoints.add(0, Map.of(
                    "date", event.getEventDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "score", simulatedScore,
                    "label", event.getEventType().name()
                ));
                simulatedScore = Math.max(0, simulatedScore - 5);
            }
            
            List<String> labels = dataPoints.stream()
                .map(p -> LocalDateTime.parse(p.get("date").toString())
                    .format(DateTimeFormatter.ofPattern("MMM dd")))
                .collect(Collectors.toList());
            List<Double> scores = dataPoints.stream()
                .map(p -> (Double) p.get("score"))
                .collect(Collectors.toList());
            
            Map<String, Object> chartData = new LinkedHashMap<>();
            chartData.put("labels", labels);
            chartData.put("scores", scores);
            chartData.put("dataPoints", dataPoints);
            
            return ResponseEntity.ok(ApiResponse.success("Risk evolution retrieved", chartData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve risk evolution", e.getMessage()));
        }
    }

    @GetMapping("/medical-record/{medicalRecordId}/actions-per-month")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getActionsPerMonth(
            @PathVariable Long medicalRecordId) {
        try {
            List<PreventionAction> actions = preventionActionRepository
                    .findByMedicalRecordId(medicalRecordId);
            
            Map<String, Long> monthCounts = actions.stream()
                .collect(Collectors.groupingBy(
                    action -> action.getActionDate() != null 
                        ? action.getActionDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                        : "Unknown",
                    Collectors.counting()
                ));
            
            List<String> months = monthCounts.keySet().stream()
                .sorted()
                .map(m -> {
                    try {
                        return LocalDateTime.parse(m + "-01T00:00:00")
                            .format(DateTimeFormatter.ofPattern("MMM yyyy"));
                    } catch (Exception e) {
                        return m;
                    }
                })
                .collect(Collectors.toList());
            
            List<Long> counts = monthCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
            
            Map<String, Object> chartData = new LinkedHashMap<>();
            chartData.put("labels", months);
            chartData.put("counts", counts);
            chartData.put("total", actions.size());
            
            return ResponseEntity.ok(ApiResponse.success("Actions per month retrieved", chartData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve actions per month", e.getMessage()));
        }
    }

    @GetMapping("/medical-record/{medicalRecordId}/risk-factors-distribution")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRiskFactorsDistribution(
            @PathVariable Long medicalRecordId) {
        try {
            List<RiskFactor> factors = riskFactorRepository
                    .findByMedicalRecordIdAndIsActive(medicalRecordId, true);
            
            Map<String, Long> distribution = factors.stream()
                    .collect(Collectors.groupingBy(
                        rf -> rf.getSeverity() != null ? rf.getSeverity().name() : "UNKNOWN",
                        Collectors.counting()
                    ));
            
            List<String> labels = Arrays.asList("CRITICAL", "HIGH", "MEDIUM", "LOW");
            List<Long> counts = labels.stream()
                .map(label -> distribution.getOrDefault(label, 0L))
                .collect(Collectors.toList());
            
            Map<String, Object> chartData = new LinkedHashMap<>();
            chartData.put("labels", labels);
            chartData.put("counts", counts);
            chartData.put("total", factors.size());
            
            return ResponseEntity.ok(ApiResponse.success("Distribution retrieved", chartData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve distribution", e.getMessage()));
        }
    }

    @GetMapping("/medical-record/{medicalRecordId}/adherence")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdherenceStats(
            @PathVariable Long medicalRecordId) {
        try {
            Long total = preventionActionRepository.countByMedicalRecordId(medicalRecordId);
            Long completed = preventionActionRepository.countByMedicalRecordIdAndStatus(
                medicalRecordId, ActionStatus.COMPLETED);
            Long pending = preventionActionRepository.countByMedicalRecordIdAndStatus(
                medicalRecordId, ActionStatus.PENDING);
            Long cancelled = preventionActionRepository.countByMedicalRecordIdAndStatus(
                medicalRecordId, ActionStatus.CANCELLED);
            double rate = total > 0 ? Math.round((completed * 100.0 / total) * 10.0) / 10.0 : 0;

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalActions", total);
            stats.put("completedActions", completed);
            stats.put("pendingActions", pending);
            stats.put("cancelledActions", cancelled);
            stats.put("adherenceRate", rate);
            
            return ResponseEntity.ok(ApiResponse.success("Adherence stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve adherence stats", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserOverview(@PathVariable Long userId) {
        try {
            List<MedicalRecord> records = medicalRecordRepository.findByUserId(userId);
            long totalRecords = records.size();
            double currentRiskScore = 0;
            String currentRiskLevel = "LOW";
            long totalRiskFactors = 0;
            long activeRiskFactors = 0;
            long totalActions = 0;
            long completedActions = 0;

            if (!records.isEmpty()) {
                MedicalRecord latest = records.get(0);
                currentRiskScore = latest.getRiskScore() != null ? latest.getRiskScore() : 0;
                currentRiskLevel = latest.getRiskLevel() != null ? latest.getRiskLevel().name() : "LOW";
                totalRiskFactors = riskFactorRepository.countByMedicalRecordId(latest.getId());
                activeRiskFactors = riskFactorRepository.countByMedicalRecordIdAndIsActive(latest.getId(), true);
                totalActions = preventionActionRepository.countByMedicalRecordId(latest.getId());
                completedActions = preventionActionRepository.countByMedicalRecordIdAndStatus(
                    latest.getId(), ActionStatus.COMPLETED);
            }

            double adherenceRate = totalActions > 0
                    ? Math.round((completedActions * 100.0 / totalActions) * 10.0) / 10.0 : 0;

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalRecords", totalRecords);
            stats.put("currentRiskScore", currentRiskScore);
            stats.put("currentRiskLevel", currentRiskLevel);
            stats.put("totalRiskFactors", totalRiskFactors);
            stats.put("activeRiskFactors", activeRiskFactors);
            stats.put("totalPreventionActions", totalActions);
            stats.put("completedActions", completedActions);
            stats.put("adherenceRate", adherenceRate);
            
            return ResponseEntity.ok(ApiResponse.success("Overview retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve overview", e.getMessage()));
        }
    }
}
