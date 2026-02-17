package com.alzheimer.backend.medical;

import com.alzheimer.backend.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prevention-actions")
@CrossOrigin(origins = "http://localhost:4200")
public class PreventionActionController {

    private final PreventionActionRepository preventionActionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final TimelineService timelineService;

    public PreventionActionController(PreventionActionRepository preventionActionRepository,
                                      MedicalRecordRepository medicalRecordRepository,
                                      TimelineService timelineService) {
        this.preventionActionRepository = preventionActionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.timelineService = timelineService;
    }

    // Get all prevention actions for a medical record (paginated)
    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getActionsByMedicalRecord(
            @PathVariable Long medicalRecordId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "actionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            List<PreventionAction> all = preventionActionRepository
                    .findByMedicalRecordId(medicalRecordId);
            int total = all.size();
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, total);
            List<PreventionActionDTO> content = (fromIndex >= total)
                    ? Collections.emptyList()
                    : all.subList(fromIndex, toIndex).stream()
                         .map(PreventionActionDTO::new).collect(Collectors.toList());
            int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / size);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("content", content);
            result.put("totalElements", total);
            result.put("totalPages", totalPages);
            result.put("size", size);
            result.put("number", page);
            return ResponseEntity.ok(ApiResponse.success("Prevention actions retrieved", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve prevention actions", e.getMessage()));
        }
    }

    // Get actions by status
    @GetMapping("/medical-record/{medicalRecordId}/status/{status}")
    public ResponseEntity<ApiResponse<List<PreventionActionDTO>>> getActionsByStatus(
            @PathVariable Long medicalRecordId, @PathVariable String status) {
        try {
            ActionStatus actionStatus = ActionStatus.valueOf(status.toUpperCase());
            List<PreventionActionDTO> dtos = preventionActionRepository
                    .findByMedicalRecordIdAndStatus(medicalRecordId, actionStatus)
                    .stream().map(PreventionActionDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Actions retrieved", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve actions", e.getMessage()));
        }
    }

    // Get single action by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PreventionActionDTO>> getActionById(@PathVariable Long id) {
        return preventionActionRepository.findById(id)
                .map(a -> ResponseEntity.ok(ApiResponse.success("Action found", new PreventionActionDTO(a))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Action not found", "No action with ID: " + id)));
    }

    // Create new prevention action
    @PostMapping
    public ResponseEntity<ApiResponse<PreventionActionDTO>> createAction(
            @RequestBody Map<String, Object> request) {
        try {
            Long medicalRecordId = Long.valueOf(request.get("medicalRecordId").toString());
            return medicalRecordRepository.findById(medicalRecordId)
                    .map(record -> {
                        PreventionAction action = new PreventionAction();
                        action.setMedicalRecord(record);
                        if (request.containsKey("actionType"))
                            action.setActionType(request.get("actionType").toString());
                        if (request.containsKey("description"))
                            action.setDescription(request.get("description").toString());
                        if (request.containsKey("actionDate"))
                            action.setActionDate(LocalDateTime.parse(request.get("actionDate").toString()));
                        else
                            action.setActionDate(LocalDateTime.now());
                        if (request.containsKey("status"))
                            action.setStatus(ActionStatus.valueOf(request.get("status").toString()));
                        if (request.containsKey("result"))
                            action.setResult(request.get("result").toString());
                        if (request.containsKey("frequency"))
                            action.setFrequency(request.get("frequency").toString());

                        PreventionAction saved = preventionActionRepository.save(action);
                        timelineService.logPreventionActionAdded(record, saved);
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Prevention action created", new PreventionActionDTO(saved)));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Medical record not found", "No record with ID: " + medicalRecordId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create action", e.getMessage()));
        }
    }

    // Update prevention action
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PreventionActionDTO>> updateAction(
            @PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            return preventionActionRepository.findById(id)
                    .map(action -> {
                        if (request.containsKey("actionType"))
                            action.setActionType(request.get("actionType").toString());
                        if (request.containsKey("description"))
                            action.setDescription(request.get("description").toString());
                        if (request.containsKey("actionDate"))
                            action.setActionDate(LocalDateTime.parse(request.get("actionDate").toString()));
                        if (request.containsKey("status"))
                            action.setStatus(ActionStatus.valueOf(request.get("status").toString()));
                        if (request.containsKey("result"))
                            action.setResult(request.get("result").toString());
                        if (request.containsKey("frequency"))
                            action.setFrequency(request.get("frequency").toString());
                        PreventionAction updated = preventionActionRepository.save(action);
                        timelineService.logPreventionActionUpdated(action.getMedicalRecord(), updated);
                        return ResponseEntity.ok(ApiResponse.success("Action updated", new PreventionActionDTO(updated)));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Action not found", "No action with ID: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update action", e.getMessage()));
        }
    }

    // Delete prevention action
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAction(@PathVariable Long id) {
        try {
            if (!preventionActionRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Action not found", "No action with ID: " + id));
            }
            preventionActionRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Action deleted", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete action", e.getMessage()));
        }
    }

    // Mark action as completed
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<PreventionActionDTO>> completeAction(@PathVariable Long id) {
        return preventionActionRepository.findById(id)
                .map(action -> {
                    action.setStatus(ActionStatus.COMPLETED);
                    action.setCompletedDate(LocalDateTime.now());
                    PreventionAction updated = preventionActionRepository.save(action);
                    return ResponseEntity.ok(ApiResponse.success("Action completed", new PreventionActionDTO(updated)));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Action not found", "No action with ID: " + id)));
    }

    // Get prevention action stats
    @GetMapping("/medical-record/{medicalRecordId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getActionStats(@PathVariable Long medicalRecordId) {
        try {
            Long total = preventionActionRepository.countByMedicalRecordId(medicalRecordId);
            Long completed = preventionActionRepository.countByMedicalRecordIdAndStatus(medicalRecordId, ActionStatus.COMPLETED);
            Long pending = preventionActionRepository.countByMedicalRecordIdAndStatus(medicalRecordId, ActionStatus.PENDING);
            Long cancelled = preventionActionRepository.countByMedicalRecordIdAndStatus(medicalRecordId, ActionStatus.CANCELLED);
            double adherenceRate = total > 0 ? Math.round((completed * 100.0 / total) * 10.0) / 10.0 : 0;

            Map<String, Object> stats = Map.of(
                "totalActions", total,
                "completedActions", completed,
                "pendingActions", pending,
                "cancelledActions", cancelled,
                "adherenceRate", adherenceRate
            );
            return ResponseEntity.ok(ApiResponse.success("Stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve stats", e.getMessage()));
        }
    }

    // Filter by date range
    @GetMapping("/medical-record/{medicalRecordId}/filter")
    public ResponseEntity<ApiResponse<List<PreventionActionDTO>>> filterByDateRange(
            @PathVariable Long medicalRecordId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            List<PreventionActionDTO> dtos = preventionActionRepository
                    .findByMedicalRecordIdAndActionDateBetween(medicalRecordId, start, end)
                    .stream().map(PreventionActionDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Actions filtered", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to filter actions", e.getMessage()));
        }
    }
}
