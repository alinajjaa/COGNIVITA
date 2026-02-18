package com.alzheimer.backend.cnn;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.cors.CorsUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/cnn")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class CNNPredictionController {

    @PostMapping("/predict")
    public ResponseEntity<?> predictBrainScan(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("=== CNN PREDICTION REQUEST ===");
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File is empty"
                ));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && 
                !file.getOriginalFilename().endsWith(".dcm"))) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid file type. Please upload an image (JPG, PNG) or DICOM file."
                ));
            }

            // Validate file size (max 50MB)
            if (file.getSize() > 50_000_000) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File is too large. Maximum size is 50MB."
                ));
            }

            // Get file bytes
            byte[] fileBytes = file.getBytes();

            // Call AI model for prediction (mock implementation)
            Map<String, Object> predictionResult = performPrediction(fileBytes, file.getOriginalFilename());
            
            System.out.println("Prediction completed: " + predictionResult.get("diagnosis"));

            // Return successful response
            Map<String, Object> response = Map.of(
                "success", true,
                "data", predictionResult
            );
            
            System.out.println("Returning response: " + response);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error processing file: " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Prediction error: " + e.getMessage()
            ));
        }
    }

    private Map<String, Object> performPrediction(byte[] imageBytes, String filename) {
        // Generate prediction based on image analysis
        // This is a mock implementation - replace with actual CNN model
        
        Map<String, Object> result = new HashMap<>();
        
        // Simulated diagnosis based on image properties
        String diagnosis = determineDiagnosis(imageBytes, filename);
        double confidence = generateConfidence();
        
        result.put("diagnosis", diagnosis);
        result.put("confidence", confidence);
        result.put("model", "CNN 3-Class (Alzheimer Detection)");
        result.put("processingTime", String.format("%.2f", Math.random() * 3 + 0.5) + "s");
        result.put("imageQuality", determineImageQuality(imageBytes));
        result.put("timestamp", LocalDateTime.now().toString());
        
        // Add recommendations
        result.put("recommendations", getRecommendations(diagnosis));
        
        // Add score breakdown
        result.put("scores", generateScoreBreakdown(diagnosis, confidence));
        
        return result;
    }

    private String determineDiagnosis(byte[] imageBytes, String filename) {
        // Mock: analyze image bytes to determine diagnosis
        // In production, this would call actual CNN model
        
        int fileSize = imageBytes.length;
        
        if (fileSize < 500_000) {
            return "Normal Cognition";
        } else if (fileSize < 1_500_000) {
            return "Mild Cognitive Impairment";
        } else if (fileSize < 3_000_000) {
            return "Moderate Dementia";
        } else {
            return "Severe Alzheimer's Disease";
        }
    }

    private double generateConfidence() {
        // Generate realistic confidence scores (0.8 - 0.98)
        return 0.80 + (Math.random() * 0.18);
    }

    private String determineImageQuality(byte[] imageBytes) {
        int qualityScore = 50 + (int)(Math.random() * 50);
        
        if (qualityScore >= 80) {
            return "Excellent";
        } else if (qualityScore >= 60) {
            return "Good";
        } else if (qualityScore >= 40) {
            return "Fair";
        } else {
            return "Poor";
        }
    }

    private List<String> getRecommendations(String diagnosis) {
        List<String> recommendations = new ArrayList<>();
        
        switch (diagnosis.toLowerCase()) {
            case "normal cognition":
                recommendations.add("No immediate intervention required");
                recommendations.add("Continue regular cognitive exercises");
                recommendations.add("Follow-up assessment in 12 months");
                recommendations.add("Maintain healthy lifestyle and diet");
                break;
                
            case "mild cognitive impairment":
                recommendations.add("Schedule follow-up assessment in 3-6 months");
                recommendations.add("Consult with neurologist for detailed evaluation");
                recommendations.add("Increase cognitive activities");
                recommendations.add("Consider memory training programs");
                recommendations.add("Monitor cognitive decline closely");
                break;
                
            case "moderate dementia":
                recommendations.add("Immediate consultation with specialist required");
                recommendations.add("Consider medication therapy");
                recommendations.add("Family support and caregiver training recommended");
                recommendations.add("Regular cognitive and physical rehabilitation");
                recommendations.add("Follow-up MRI in 6 months");
                break;
                
            case "severe alzheimer's disease":
                recommendations.add("Urgent specialist consultation required");
                recommendations.add("Comprehensive treatment plan needed");
                recommendations.add("Full-time care support recommended");
                recommendations.add("Regular monitoring and medication adjustment");
                recommendations.add("Palliative care consultation");
                break;
        }
        
        return recommendations;
    }

    private List<Map<String, Object>> generateScoreBreakdown(String diagnosis, double confidence) {
        List<Map<String, Object>> scores = new ArrayList<>();
        
        double normalScore = 0;
        double mildScore = 0;
        double moderateScore = 0;
        double severeScore = 0;
        
        // Distribute scores based on diagnosis
        switch (diagnosis.toLowerCase()) {
            case "normal cognition":
                normalScore = confidence;
                mildScore = (1 - confidence) * 0.6;
                moderateScore = (1 - confidence) * 0.3;
                severeScore = (1 - confidence) * 0.1;
                break;
            case "mild cognitive impairment":
                normalScore = (1 - confidence) * 0.2;
                mildScore = confidence;
                moderateScore = (1 - confidence) * 0.5;
                severeScore = (1 - confidence) * 0.3;
                break;
            case "moderate dementia":
                normalScore = (1 - confidence) * 0.1;
                mildScore = (1 - confidence) * 0.3;
                moderateScore = confidence;
                severeScore = (1 - confidence) * 0.6;
                break;
            case "severe alzheimer's disease":
                normalScore = (1 - confidence) * 0.05;
                mildScore = (1 - confidence) * 0.15;
                moderateScore = (1 - confidence) * 0.3;
                severeScore = confidence;
                break;
        }
        
        scores.add(createScoreEntry("Normal Cognition", normalScore, "normal"));
        scores.add(createScoreEntry("Mild Cognitive Impairment", mildScore, "mild"));
        scores.add(createScoreEntry("Moderate Dementia", moderateScore, "moderate"));
        scores.add(createScoreEntry("Severe Alzheimer's", severeScore, "severe"));
        
        return scores;
    }

    private Map<String, Object> createScoreEntry(String label, double score, String level) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("label", label);
        entry.put("value", Math.max(0, Math.min(1, score))); // Clamp between 0 and 1
        entry.put("level", level);
        return entry;
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "CNN Prediction Service is healthy",
            "timestamp", LocalDateTime.now()
        ));
    }
}
