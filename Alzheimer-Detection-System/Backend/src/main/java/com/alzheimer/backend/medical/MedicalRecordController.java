package com.alzheimer.backend.medical;

import com.alzheimer.backend.dto.ApiResponse;
import com.alzheimer.backend.user.User;
import com.alzheimer.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "http://localhost:4200")
public class MedicalRecordController {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    public MedicalRecordController(MedicalRecordRepository medicalRecordRepository, UserRepository userRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.userRepository = userRepository;
    }

    // Get all medical records
    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicalRecordDTO>>> getAllRecords() {
        try {
            List<MedicalRecordDTO> records = medicalRecordRepository.findAllWithUser()
                    .stream()
                    .map(MedicalRecordDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve medical records", e.getMessage()));
        }
    }

    // Get medical record by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> getRecordById(@PathVariable Long id) {
        try {
            Optional<MedicalRecord> record = medicalRecordRepository.findByIdWithUser(id);
            if (record.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Medical record found", new MedicalRecordDTO(record.get())));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Medical record not found", "No record with ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve medical record", e.getMessage()));
        }
    }

    // Get medical records by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<MedicalRecordDTO>>> getRecordsByUser(@PathVariable Long userId) {
        try {
            List<MedicalRecordDTO> records = medicalRecordRepository.findByUserIdWithUser(userId)
                    .stream()
                    .map(MedicalRecordDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Medical records for user retrieved", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve medical records", e.getMessage()));
        }
    }

    // Get latest medical record for a user
    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> getLatestRecordByUser(@PathVariable Long userId) {
        try {
            Optional<MedicalRecord> record = medicalRecordRepository.findFirstByUserIdOrderByCreatedAtDescWithUser(userId);
            if (record.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Latest medical record found", new MedicalRecordDTO(record.get())));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No medical record found", "No records for user ID: " + userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve medical record", e.getMessage()));
        }
    }

    // Create new medical record
    @PostMapping
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> createRecord(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found", "No user with ID: " + userId));
            }

            MedicalRecord record = new MedicalRecord();
            record.setUser(userOpt.get());
            
            if (request.containsKey("age")) {
                record.setAge(Integer.valueOf(request.get("age").toString()));
            }
            if (request.containsKey("gender")) {
                record.setGender(Gender.valueOf(request.get("gender").toString()));
            }
            if (request.containsKey("educationLevel")) {
                record.setEducationLevel(request.get("educationLevel").toString());
            }
            if (request.containsKey("familyHistory")) {
                record.setFamilyHistory(FamilyHistory.valueOf(request.get("familyHistory").toString()));
            }
            if (request.containsKey("riskFactors")) {
                record.setRiskFactors(request.get("riskFactors").toString());
            }
            if (request.containsKey("currentSymptoms")) {
                record.setCurrentSymptoms(request.get("currentSymptoms").toString());
            }
            if (request.containsKey("diagnosisNotes")) {
                record.setDiagnosisNotes(request.get("diagnosisNotes").toString());
            }

            MedicalRecord saved = medicalRecordRepository.save(record);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Medical record created successfully", new MedicalRecordDTO(saved)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create medical record", e.getMessage()));
        }
    }

    // Update medical record
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> updateRecord(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<MedicalRecord> existingOpt = medicalRecordRepository.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Medical record not found", "No record with ID: " + id));
            }

            MedicalRecord record = existingOpt.get();
            
            if (request.containsKey("age")) {
                record.setAge(Integer.valueOf(request.get("age").toString()));
            }
            if (request.containsKey("gender")) {
                record.setGender(Gender.valueOf(request.get("gender").toString()));
            }
            if (request.containsKey("educationLevel")) {
                record.setEducationLevel(request.get("educationLevel").toString());
            }
            if (request.containsKey("familyHistory")) {
                record.setFamilyHistory(FamilyHistory.valueOf(request.get("familyHistory").toString()));
            }
            if (request.containsKey("riskFactors")) {
                record.setRiskFactors(request.get("riskFactors").toString());
            }
            if (request.containsKey("currentSymptoms")) {
                record.setCurrentSymptoms(request.get("currentSymptoms").toString());
            }
            if (request.containsKey("diagnosisNotes")) {
                record.setDiagnosisNotes(request.get("diagnosisNotes").toString());
            }

            MedicalRecord updated = medicalRecordRepository.save(record);
            return ResponseEntity.ok(ApiResponse.success("Medical record updated successfully", new MedicalRecordDTO(updated)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update medical record", e.getMessage()));
        }
    }

    // Delete medical record
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

    // Get statistics
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        try {
            long totalRecords = medicalRecordRepository.count();
            long withFamilyHistory = medicalRecordRepository.findByFamilyHistory(FamilyHistory.Yes).size();
            long malePatients = medicalRecordRepository.findByGender(Gender.Male).size();
            long femalePatients = medicalRecordRepository.findByGender(Gender.Female).size();

            Map<String, Object> stats = Map.of(
                "totalRecords", totalRecords,
                "withFamilyHistory", withFamilyHistory,
                "malePatients", malePatients,
                "femalePatients", femalePatients
            );
            
            return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve statistics", e.getMessage()));
        }
    }
}
