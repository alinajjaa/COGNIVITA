package com.alzheimer.backend.medical;

import com.alzheimer.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/risk-factors")
public class RiskFactorController {

    private final RiskFactorRepository riskFactorRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final RiskScoreService riskScoreService;
    private final TimelineService timelineService;

    public RiskFactorController(RiskFactorRepository riskFactorRepository,
                                MedicalRecordRepository medicalRecordRepository,
                                RiskScoreService riskScoreService,
                                TimelineService timelineService) {
        this.riskFactorRepository = riskFactorRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.riskScoreService = riskScoreService;
        this.timelineService = timelineService;
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRiskFactorsByMedicalRecord(
            @PathVariable Long medicalRecordId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            List<RiskFactor> all = riskFactorRepository.findByMedicalRecordId(medicalRecordId);
            int total = all.size();
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, total);
            List<RiskFactorDTO> content = (fromIndex >= total)
                    ? Collections.emptyList()
                    : all.subList(fromIndex, toIndex).stream()
                         .map(RiskFactorDTO::new).collect(Collectors.toList());
            int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / size);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("content", content);
            result.put("totalElements", total);
            result.put("totalPages", totalPages);
            result.put("size", size);
            result.put("number", page);
            return ResponseEntity.ok(ApiResponse.success("Risk factors retrieved", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve risk factors", e.getMessage()));
        }
    }

    @GetMapping("/medical-record/{medicalRecordId}/active")
    public ResponseEntity<ApiResponse<List<RiskFactorDTO>>> getActiveRiskFactors(
            @PathVariable Long medicalRecordId) {
        try {
            List<RiskFactorDTO> factors = riskFactorRepository
                    .findByMedicalRecordIdAndIsActive(medicalRecordId, true)
                    .stream().map(RiskFactorDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Active risk factors retrieved", factors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve risk factors", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RiskFactorDTO>> getRiskFactorById(@PathVariable Long id) {
        return riskFactorRepository.findById(id)
                .map(rf -> ResponseEntity.ok(ApiResponse.success("Risk factor found", new RiskFactorDTO(rf))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Not found", "No factor with ID: " + id)));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<RiskFactorDTO>> createRiskFactor(
            @RequestBody Map<String, Object> request) {
        try {
            if (request.get("medicalRecordId") == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Missing field", "medicalRecordId is required"));
            }
            Long recordId = Long.valueOf(request.get("medicalRecordId").toString());
            Optional<MedicalRecord> opt = medicalRecordRepository.findById(recordId);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Not found", "No medical record with ID: " + recordId));
            }
            MedicalRecord record = opt.get();
            RiskFactor rf = new RiskFactor();
            rf.setMedicalRecord(record);
            applyRequest(request, rf);
            rf.setIsActive(true);
            RiskFactor saved = riskFactorRepository.save(rf);
            medicalRecordRepository.findById(recordId).ifPresent(r -> {
                riskScoreService.updateRiskScore(r);
                medicalRecordRepository.save(r);
            });
            timelineService.logRiskFactorAdded(record, saved);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Risk factor created", new RiskFactorDTO(saved)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create risk factor", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<RiskFactorDTO>> updateRiskFactor(
            @PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<RiskFactor> rfOpt = riskFactorRepository.findById(id);
            if (rfOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Not found", "No factor with ID: " + id));
            }
            RiskFactor rf = rfOpt.get();
            applyRequest(request, rf);
            if (request.get("isActive") != null)
                rf.setIsActive(Boolean.valueOf(request.get("isActive").toString()));
            RiskFactor updated = riskFactorRepository.save(rf);
            Long recordId = rf.getMedicalRecord().getId();
            medicalRecordRepository.findById(recordId).ifPresent(r -> {
                riskScoreService.updateRiskScore(r);
                medicalRecordRepository.save(r);
            });
            timelineService.logRiskFactorUpdated(rf.getMedicalRecord(), updated);
            return ResponseEntity.ok(ApiResponse.success("Risk factor updated", new RiskFactorDTO(updated)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update risk factor", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteRiskFactor(@PathVariable Long id) {
        try {
            Optional<RiskFactor> rfOpt = riskFactorRepository.findById(id);
            if (rfOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Not found", "No factor with ID: " + id));
            }
            RiskFactor rf = rfOpt.get();
            rf.setIsActive(false);
            riskFactorRepository.save(rf);
            Long recordId = rf.getMedicalRecord().getId();
            medicalRecordRepository.findById(recordId).ifPresent(r -> {
                riskScoreService.updateRiskScore(r);
                medicalRecordRepository.save(r);
            });
            timelineService.logRiskFactorRemoved(rf.getMedicalRecord(), rf);
            return ResponseEntity.ok(ApiResponse.<Void>success("Risk factor removed", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete risk factor", e.getMessage()));
        }
    }

    @GetMapping("/medical-record/{medicalRecordId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRiskFactorStats(
            @PathVariable Long medicalRecordId) {
        try {
            Long total = riskFactorRepository.countByMedicalRecordId(medicalRecordId);
            Long active = riskFactorRepository.countByMedicalRecordIdAndIsActive(medicalRecordId, true);
            List<RiskFactor> activeList = riskFactorRepository
                    .findByMedicalRecordIdAndIsActive(medicalRecordId, true);
            long critical = activeList.stream().filter(r -> r.getSeverity() == Severity.CRITICAL).count();
            long high = activeList.stream().filter(r -> r.getSeverity() == Severity.HIGH).count();
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalCount", total);
            stats.put("activeCount", active);
            stats.put("inactiveCount", total - active);
            stats.put("criticalCount", critical);
            stats.put("highCount", high);
            return ResponseEntity.ok(ApiResponse.success("Statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve statistics", e.getMessage()));
        }
    }

    private void applyRequest(Map<String, Object> req, RiskFactor rf) {
        if (req.get("factorType") != null)
            rf.setFactorType(req.get("factorType").toString());
        if (req.get("severity") != null)
            rf.setSeverity(Severity.valueOf(req.get("severity").toString().toUpperCase()));
        if (req.get("notes") != null)
            rf.setNotes(req.get("notes").toString());
        if (req.get("diagnosedDate") != null) {
            String d = req.get("diagnosedDate").toString().trim();
            if (!d.isEmpty()) {
                try {
                    rf.setDiagnosedDate(LocalDateTime.parse(d, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (DateTimeParseException e1) {
                    try { rf.setDiagnosedDate(LocalDateTime.parse(d + "T00:00:00")); }
                    catch (DateTimeParseException e2) { /* ignore */ }
                }
            }
        }
    }
}
