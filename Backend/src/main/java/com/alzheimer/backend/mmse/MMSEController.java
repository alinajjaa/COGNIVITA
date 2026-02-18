package com.alzheimer.backend.mmse;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mmse")
@CrossOrigin(origins = "*")
public class MMSEController {

  @Autowired
  private MMSERepository mmseRepository;

  @PostMapping("/submit")
  public ResponseEntity<?> submitMMSETest(@RequestBody MMSETestRequest request) {
    try {
      MMSETest test = new MMSETest();
      test.setPatientName(request.getPatient_name());
      test.setOrientationScore(request.getOrientation_score());
      test.setRegistrationScore(request.getRegistration_score());
      test.setAttentionScore(request.getAttention_score());
      test.setRecallScore(request.getRecall_score());
      test.setLanguageScore(request.getLanguage_score());
      test.setTotalScore(request.getTotal_score());
      test.setInterpretation(request.getInterpretation());
      test.setTestDate(LocalDate.parse(request.getTest_date()));
      test.setNotes(request.getNotes());

      MMSETest saved = mmseRepository.save(test);
      
      return ResponseEntity.ok(new ApiResponse(
        true,
        "MMSE test submitted successfully",
        saved
      ));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse(false, "Error submitting test: " + e.getMessage(), null));
    }
  }

  @GetMapping("/results")
  public ResponseEntity<?> getMMSEResults() {
    try {
      List<MMSETest> tests = mmseRepository.findAll();
      return ResponseEntity.ok(new ApiResponse(true, "MMSE results retrieved", tests));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse(false, "Error retrieving results: " + e.getMessage(), null));
    }
  }

  @GetMapping("/results/{patientName}")
  public ResponseEntity<?> getPatientResults(@PathVariable String patientName) {
    try {
      List<MMSETest> tests = mmseRepository.findByPatientNameIgnoreCase(patientName);
      if (tests.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(false, "No results found for patient: " + patientName, null));
      }
      return ResponseEntity.ok(new ApiResponse(true, "Patient results retrieved", tests));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse(false, "Error retrieving patient results: " + e.getMessage(), null));
    }
  }

  // Request DTO
  public static class MMSETestRequest {
    private String patient_name;
    private int orientation_score;
    private int registration_score;
    private int attention_score;
    private int recall_score;
    private int language_score;
    private int total_score;
    private String interpretation;
    private String test_date;
    private String notes;

    // Getters
    public String getPatient_name() { return patient_name; }
    public int getOrientation_score() { return orientation_score; }
    public int getRegistration_score() { return registration_score; }
    public int getAttention_score() { return attention_score; }
    public int getRecall_score() { return recall_score; }
    public int getLanguage_score() { return language_score; }
    public int getTotal_score() { return total_score; }
    public String getInterpretation() { return interpretation; }
    public String getTest_date() { return test_date; }
    public String getNotes() { return notes; }

    // Setters
    public void setPatient_name(String patient_name) { this.patient_name = patient_name; }
    public void setOrientation_score(int orientation_score) { this.orientation_score = orientation_score; }
    public void setRegistration_score(int registration_score) { this.registration_score = registration_score; }
    public void setAttention_score(int attention_score) { this.attention_score = attention_score; }
    public void setRecall_score(int recall_score) { this.recall_score = recall_score; }
    public void setLanguage_score(int language_score) { this.language_score = language_score; }
    public void setTotal_score(int total_score) { this.total_score = total_score; }
    public void setInterpretation(String interpretation) { this.interpretation = interpretation; }
    public void setTest_date(String test_date) { this.test_date = test_date; }
    public void setNotes(String notes) { this.notes = notes; }
  }

  // Response DTO
  public static class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    public ApiResponse(boolean success, String message, Object data) {
      this.success = success;
      this.message = message;
      this.data = data;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
  }
}
