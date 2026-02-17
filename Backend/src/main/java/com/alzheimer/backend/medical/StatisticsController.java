package com.alzheimer.backend.medical;

import com.alzheimer.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:4200")
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

    // Get user overview stats
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
                completedActions = preventionActionRepository.countByMedicalRecordIdAndStatus(latest.getId(), ActionStatus.COMPLETED);
            }

            double adherenceRate = totalActions > 0
                    ? Math.round((completedActions * 100.0 / totalActions) * 10.0) / 10.0 : 0;

            Map<String, Object> stats = Map.of(
                "totalRecords", totalRecords,
                "currentRiskScore", currentRiskScore,
                "currentRiskLevel", currentRiskLevel,
                "totalRiskFactors", totalRiskFactors,
                "activeRiskFactors", activeRiskFactors,
                "totalPreventionActions", totalActions,
                "completedActions", completedActions,
                "adherenceRate", adherenceRate
            );
            return ResponseEntity.ok(ApiResponse.success("Overview retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve overview", e.getMessage()));
        }
    }

    // Get risk score evolution (from timeline)
    @GetMapping("/medical-record/{medicalRecordId}/risk-evolution")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRiskEvolution(
            @PathVariable Long medicalRecordId) {
        try {
            List<Map<String, Object>> evolution = timelineRepository
                    .findByMedicalRecordIdOrderByEventDateDesc(medicalRecordId)
                    .stream()
                    .filter(t -> t.getEventType() == EventType.RISK_SCORE_UPDATED
                            || t.getEventType() == EventType.RISK_FACTOR_ADDED
                            || t.getEventType() == EventType.RISK_FACTOR_UPDATED)
                    .map(t -> Map.<String, Object>of(
                        "date", t.getEventDate().toString(),
                        "eventType", t.getEventType().name(),
                        "description", t.getDescription() != null ? t.getDescription() : ""
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Risk evolution retrieved", evolution));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve risk evolution", e.getMessage()));
        }
    }

    // Get adherence stats
    @GetMapping("/medical-record/{medicalRecordId}/adherence")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdherenceStats(
            @PathVariable Long medicalRecordId) {
        try {
            Long total = preventionActionRepository.countByMedicalRecordId(medicalRecordId);
            Long completed = preventionActionRepository.countByMedicalRecordIdAndStatus(medicalRecordId, ActionStatus.COMPLETED);
            Long pending = preventionActionRepository.countByMedicalRecordIdAndStatus(medicalRecordId, ActionStatus.PENDING);
            Long cancelled = preventionActionRepository.countByMedicalRecordIdAndStatus(medicalRecordId, ActionStatus.CANCELLED);
            double rate = total > 0 ? Math.round((completed * 100.0 / total) * 10.0) / 10.0 : 0;

            Map<String, Object> stats = Map.of(
                "totalActions", total,
                "completedActions", completed,
                "pendingActions", pending,
                "cancelledActions", cancelled,
                "adherenceRate", rate
            );
            return ResponseEntity.ok(ApiResponse.success("Adherence stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve adherence stats", e.getMessage()));
        }
    }

    // Get risk factors distribution
    @GetMapping("/medical-record/{medicalRecordId}/risk-factors-distribution")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getRiskFactorsDistribution(
            @PathVariable Long medicalRecordId) {
        try {
            List<RiskFactor> factors = riskFactorRepository
                    .findByMedicalRecordIdAndIsActive(medicalRecordId, true);
            Map<String, Long> distribution = factors.stream()
                    .collect(Collectors.groupingBy(
                        rf -> rf.getSeverity() != null ? rf.getSeverity().name() : "UNKNOWN",
                        Collectors.counting()
                    ));
            return ResponseEntity.ok(ApiResponse.success("Distribution retrieved", distribution));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve distribution", e.getMessage()));
        }
    }
}
