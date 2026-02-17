package com.alzheimer.backend.medical;

import com.alzheimer.backend.dto.ApiResponse;
import com.alzheimer.backend.user.User;
import com.alzheimer.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "http://localhost:4200")
public class MedicalRecordController {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final RiskScoreService riskScoreService;
    private final RecommendationService recommendationService;
    private final TimelineService timelineService;
    private final RiskFactorRepository riskFactorRepository;
    private final PreventionActionRepository preventionActionRepository;
    private final TimelineRepository timelineRepository;

    public MedicalRecordController(MedicalRecordRepository medicalRecordRepository,
                                   UserRepository userRepository,
                                   RiskScoreService riskScoreService,
                                   RecommendationService recommendationService,
                                   TimelineService timelineService,
                                   RiskFactorRepository riskFactorRepository,
                                   PreventionActionRepository preventionActionRepository,
                                   TimelineRepository timelineRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.userRepository = userRepository;
        this.riskScoreService = riskScoreService;
        this.recommendationService = recommendationService;
        this.timelineService = timelineService;
        this.riskFactorRepository = riskFactorRepository;
        this.preventionActionRepository = preventionActionRepository;
        this.timelineRepository = timelineRepository;
    }

    // ── GET ALL (paginated + filtered) ─────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String familyHistory) {
        try {
            List<MedicalRecord> all = medicalRecordRepository.findAllWithUser();

            // Apply filters
            if (riskLevel != null && !riskLevel.isEmpty()) {
                RiskLevel rl = RiskLevel.valueOf(riskLevel.toUpperCase());
                all = all.stream().filter(r -> rl.equals(r.getRiskLevel())).collect(Collectors.toList());
            }
            if (gender != null && !gender.isEmpty()) {
                Gender g = Gender.valueOf(gender);
                all = all.stream().filter(r -> g.equals(r.getGender())).collect(Collectors.toList());
            }
            if (familyHistory != null && !familyHistory.isEmpty()) {
                FamilyHistory fh = FamilyHistory.valueOf(familyHistory);
                all = all.stream().filter(r -> fh.equals(r.getFamilyHistory())).collect(Collectors.toList());
            }

            // Sort
            all.sort((a, b) -> {
                int cmp = 0;
                switch (sortBy) {
                    case "age": cmp = Comparator.comparingInt((MedicalRecord r) -> r.getAge() == null ? 0 : r.getAge()).compare(a, b); break;
                    case "riskScore": cmp = Comparator.comparingDouble((MedicalRecord r) -> r.getRiskScore() == null ? 0 : r.getRiskScore()).compare(a, b); break;
                    default: cmp = a.getCreatedAt().compareTo(b.getCreatedAt()); break;
                }
                return sortDirection.equalsIgnoreCase("ASC") ? cmp : -cmp;
            });

            // Paginate manually
            int total = all.size();
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, total);
            List<MedicalRecordDTO> pageContent = (fromIndex >= total)
                ? Collections.emptyList()
                : all.subList(fromIndex, toIndex).stream().map(MedicalRecordDTO::new).collect(Collectors.toList());

            int totalPages = (int) Math.ceil((double) total / size);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("content", pageContent);
            result.put("totalElements", total);
            result.put("totalPages", totalPages);
            result.put("size", size);
            result.put("number", page);

            return ResponseEntity.ok(ApiResponse.success("Medical records retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve medical records", e.getMessage()));
        }
    }

    // ── GET BY ID ───────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> getRecordById(@PathVariable Long id) {
        try {
            return medicalRecordRepository.findByIdWithUser(id)
                    .map(r -> {
                        List<String> recs = recommendationService.generateRecommendations(r, r.getRiskScore() != null ? r.getRiskScore() : 0);
                        return ResponseEntity.ok(ApiResponse.success("Medical record found", new MedicalRecordDTO(r, recs)));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Medical record not found", "No record with ID: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve medical record", e.getMessage()));
        }
    }

    // ── GET BY USER ─────────────────────────────────────────────────────────────
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<MedicalRecordDTO>>> getRecordsByUser(@PathVariable Long userId) {
        try {
            List<MedicalRecordDTO> records = medicalRecordRepository.findByUserIdWithUser(userId)
                    .stream().map(MedicalRecordDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Records for user retrieved", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve records", e.getMessage()));
        }
    }

    // ── PATIENT DASHBOARD ───────────────────────────────────────────────────────
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPatientDashboard(@PathVariable Long id) {
        try {
            Optional<MedicalRecord> opt = medicalRecordRepository.findByIdWithUser(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Record not found", "No record with ID: " + id));
            }
            MedicalRecord record = opt.get();
            double riskScore = record.getRiskScore() != null ? record.getRiskScore() : 0;

            Long totalFactors = riskFactorRepository.countByMedicalRecordId(id);
            Long activeFactors = riskFactorRepository.countByMedicalRecordIdAndIsActive(id, true);
            List<RiskFactor> activeList = riskFactorRepository.findByMedicalRecordIdAndIsActive(id, true);
            long criticalCount = activeList.stream().filter(rf -> rf.getSeverity() == Severity.CRITICAL).count();
            long highCount = activeList.stream().filter(rf -> rf.getSeverity() == Severity.HIGH).count();

            Long totalActions = preventionActionRepository.countByMedicalRecordId(id);
            Long completedActions = preventionActionRepository.countByMedicalRecordIdAndStatus(id, ActionStatus.COMPLETED);
            Long pendingActions = preventionActionRepository.countByMedicalRecordIdAndStatus(id, ActionStatus.PENDING);
            double adherenceRate = totalActions > 0 ? Math.round((completedActions * 100.0 / totalActions) * 10.0) / 10.0 : 0;
            long timelineCount = timelineRepository.findByMedicalRecordIdOrderByEventDateDesc(id).size();

            List<String> recommendations = recommendationService.generateRecommendations(record, riskScore);
            List<RecommendationService.PreventionActionSuggestion> suggestions =
                    recommendationService.suggestPreventionActions(record, riskScore);

            Map<String, Object> riskStats = new LinkedHashMap<>();
            riskStats.put("score", riskScore);
            riskStats.put("level", record.getRiskLevel() != null ? record.getRiskLevel().name() : "LOW");
            riskStats.put("totalFactors", totalFactors);
            riskStats.put("activeFactors", activeFactors);
            riskStats.put("criticalCount", criticalCount);
            riskStats.put("highCount", highCount);

            Map<String, Object> preventionStats = new LinkedHashMap<>();
            preventionStats.put("totalActions", totalActions);
            preventionStats.put("completedActions", completedActions);
            preventionStats.put("pendingActions", pendingActions);
            preventionStats.put("adherenceRate", adherenceRate);

            Map<String, Object> dashboard = new LinkedHashMap<>();
            dashboard.put("record", new MedicalRecordDTO(record, recommendations));
            dashboard.put("riskStats", riskStats);
            dashboard.put("preventionStats", preventionStats);
            dashboard.put("timelineCount", timelineCount);
            dashboard.put("recommendations", recommendations);
            dashboard.put("suggestedActions", suggestions);

            return ResponseEntity.ok(ApiResponse.success("Patient dashboard retrieved", dashboard));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve dashboard", e.getMessage()));
        }
    }

    // ── CREATE ──────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> createRecord(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("userId") == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Missing userId", "userId is required"));
            }
            Long userId = Long.valueOf(request.get("userId").toString());
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found", "No user with ID: " + userId));
            }
            MedicalRecord record = new MedicalRecord();
            record.setUser(userOpt.get());
            applyRequestToRecord(request, record);
            riskScoreService.updateRiskScore(record);

            MedicalRecord saved = medicalRecordRepository.save(record);
            timelineService.logMedicalRecordUpdated(saved);

            List<String> recs = recommendationService.generateRecommendations(saved, saved.getRiskScore() != null ? saved.getRiskScore() : 0);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Medical record created successfully", new MedicalRecordDTO(saved, recs)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create medical record", e.getMessage()));
        }
    }

    // ── UPDATE ──────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> updateRecord(
            @PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<MedicalRecord> existingOpt = medicalRecordRepository.findByIdWithUser(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Medical record not found", "No record with ID: " + id));
            }
            MedicalRecord record = existingOpt.get();
            applyRequestToRecord(request, record);
            riskScoreService.updateRiskScore(record);

            MedicalRecord updated = medicalRecordRepository.save(record);
            timelineService.logMedicalRecordUpdated(updated);

            List<String> recs = recommendationService.generateRecommendations(updated, updated.getRiskScore() != null ? updated.getRiskScore() : 0);
            return ResponseEntity.ok(ApiResponse.success("Medical record updated successfully", new MedicalRecordDTO(updated, recs)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update medical record", e.getMessage()));
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        try {
            if (!medicalRecordRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Medical record not found", "No record with ID: " + id));
            }
            medicalRecordRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Medical record deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete medical record", e.getMessage()));
        }
    }

    // ── STATS ───────────────────────────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        try {
            List<MedicalRecord> all = medicalRecordRepository.findAllWithUser();
            long total = all.size();
            long withFamilyHistory = all.stream().filter(r -> r.getFamilyHistory() == FamilyHistory.Yes).count();
            long male = all.stream().filter(r -> r.getGender() == Gender.Male).count();
            long female = all.stream().filter(r -> r.getGender() == Gender.Female).count();
            long critical = all.stream().filter(r -> r.getRiskLevel() == RiskLevel.CRITICAL).count();
            long high = all.stream().filter(r -> r.getRiskLevel() == RiskLevel.HIGH).count();
            long medium = all.stream().filter(r -> r.getRiskLevel() == RiskLevel.MEDIUM).count();
            long low = all.stream().filter(r -> r.getRiskLevel() == RiskLevel.LOW).count();

            Map<String, Object> dist = new LinkedHashMap<>();
            dist.put("critical", critical); dist.put("high", high);
            dist.put("medium", medium); dist.put("low", low);

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalRecords", total);
            stats.put("withFamilyHistory", withFamilyHistory);
            stats.put("malePatients", male);
            stats.put("femalePatients", female);
            stats.put("riskDistribution", dist);

            return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve statistics", e.getMessage()));
        }
    }

    // ── HELPER ──────────────────────────────────────────────────────────────────
    private void applyRequestToRecord(Map<String, Object> request, MedicalRecord record) {
        if (request.get("age") != null)
            record.setAge(Integer.valueOf(request.get("age").toString()));
        if (request.get("gender") != null)
            record.setGender(Gender.valueOf(request.get("gender").toString()));
        if (request.get("educationLevel") != null)
            record.setEducationLevel(request.get("educationLevel").toString());
        if (request.get("familyHistory") != null)
            record.setFamilyHistory(FamilyHistory.valueOf(request.get("familyHistory").toString()));
        if (request.get("riskFactors") != null)
            record.setRiskFactors(request.get("riskFactors").toString());
        if (request.get("currentSymptoms") != null)
            record.setCurrentSymptoms(request.get("currentSymptoms").toString());
        if (request.get("diagnosisNotes") != null)
            record.setDiagnosisNotes(request.get("diagnosisNotes").toString());
    }
}
