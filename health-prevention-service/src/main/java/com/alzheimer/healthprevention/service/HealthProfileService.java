package com.alzheimer.healthprevention.service;

import com.alzheimer.healthprevention.dto.HealthProfileDTO;
import com.alzheimer.healthprevention.dto.WellnessDashboardDTO;
import com.alzheimer.healthprevention.entity.*;
import com.alzheimer.healthprevention.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HealthProfileService {

    private final HealthProfileRepository profileRepository;
    private final HealthRecommendationRepository recommendationRepository;
    private final WellnessActivityRepository activityRepository;

    public HealthProfileService(HealthProfileRepository profileRepository,
                                HealthRecommendationRepository recommendationRepository,
                                WellnessActivityRepository activityRepository) {
        this.profileRepository = profileRepository;
        this.recommendationRepository = recommendationRepository;
        this.activityRepository = activityRepository;
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    public HealthProfile createProfile(HealthProfileDTO dto) {
        if (profileRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("Health profile already exists for user " + dto.getUserId());
        }
        HealthProfile profile = mapDtoToEntity(new HealthProfile(), dto);
        profile.setWellnessScore(calculateWellnessScore(profile));
        profile.setLastAssessmentDate(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public Optional<HealthProfile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<HealthProfile> getProfileById(Long id) {
        return profileRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<HealthProfile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public HealthProfile updateProfile(Long id, HealthProfileDTO dto) {
        HealthProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Health profile not found with id: " + id));
        mapDtoToEntity(profile, dto);
        profile.setWellnessScore(calculateWellnessScore(profile));
        profile.setLastAssessmentDate(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    public void deleteProfile(Long id) {
        if (!profileRepository.existsById(id)) {
            throw new NoSuchElementException("Health profile not found with id: " + id);
        }
        profileRepository.deleteById(id);
    }

    // ─── Wellness Dashboard ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public WellnessDashboardDTO getDashboard(Long userId) {
        HealthProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("No health profile for user " + userId));

        List<HealthRecommendation> allRecs = recommendationRepository.findByHealthProfileId(profile.getId());
        List<WellnessActivity> allActivities = activityRepository.findByHealthProfileIdOrderByActivityDateDesc(profile.getId());

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<WellnessActivity> weekActivities = activityRepository
                .findByHealthProfileIdAndActivityDateAfter(profile.getId(), oneWeekAgo);

        Integer totalMinutes = activityRepository.sumDurationByHealthProfileId(profile.getId());

        // Count by type
        Map<String, Integer> byType = allActivities.stream()
                .filter(a -> a.getActivityType() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getActivityType().name(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        // Count recommendations by category
        Map<String, Integer> byCategory = allRecs.stream()
                .filter(r -> r.getCategory() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getCategory().name(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        long activeRecs = allRecs.stream()
                .filter(r -> r.getStatus() == RecommendationStatus.ACTIVE).count();
        long completedRecs = allRecs.stream()
                .filter(r -> r.getStatus() == RecommendationStatus.COMPLETED).count();

        WellnessDashboardDTO dashboard = new WellnessDashboardDTO();
        dashboard.setUserId(userId);
        dashboard.setHealthProfileId(profile.getId());
        dashboard.setWellnessScore(profile.getWellnessScore());
        dashboard.setWellnessLevel(getWellnessLevel(profile.getWellnessScore()));
        dashboard.setTotalRecommendations(allRecs.size());
        dashboard.setActiveRecommendations((int) activeRecs);
        dashboard.setCompletedRecommendations((int) completedRecs);
        dashboard.setTotalActivities(allActivities.size());
        dashboard.setActivitiesThisWeek(weekActivities.size());
        dashboard.setTotalActivityMinutes(totalMinutes != null ? totalMinutes : 0);
        dashboard.setActivitiesByType(byType);
        dashboard.setRecommendationsByCategory(byCategory);
        if (profile.getLastAssessmentDate() != null) {
            dashboard.setLastAssessmentDate(profile.getLastAssessmentDate().toString());
        }
        return dashboard;
    }

    // ─── Auto-generate evidence-based recommendations ─────────────────────────

    public List<HealthRecommendation> generateRecommendations(Long profileId) {
        HealthProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NoSuchElementException("Profile not found: " + profileId));

        List<HealthRecommendation> generated = new ArrayList<>();

        // Physical activity recommendations
        if (profile.getPhysicalActivityLevel() == ActivityLevel.SEDENTARY ||
                profile.getPhysicalActivityLevel() == ActivityLevel.LIGHTLY_ACTIVE) {
            generated.add(buildRec(profile, RecommendationCategory.PHYSICAL_ACTIVITY, Priority.HIGH,
                    "Increase Daily Physical Activity",
                    "Aim for at least 150 minutes of moderate-intensity aerobic activity per week. " +
                    "Studies show physical activity reduces Alzheimer's risk by up to 45%. " +
                    "Start with 10-minute walks and gradually increase duration."));
        }

        // Sleep recommendations
        if (profile.getSleepHoursPerNight() != null &&
                (profile.getSleepHoursPerNight() < 7 || profile.getSleepHoursPerNight() > 9)) {
            generated.add(buildRec(profile, RecommendationCategory.SLEEP, Priority.HIGH,
                    "Optimize Sleep Duration",
                    "Maintain 7-9 hours of quality sleep per night. Poor sleep is linked to increased " +
                    "amyloid-beta accumulation in the brain. Establish a consistent sleep schedule " +
                    "and avoid screens 1 hour before bed."));
        }

        // Diet recommendations
        if (profile.getDietQuality() == DietQuality.POOR || profile.getDietQuality() == DietQuality.BELOW_AVERAGE) {
            generated.add(buildRec(profile, RecommendationCategory.NUTRITION, Priority.HIGH,
                    "Adopt MIND Diet",
                    "The MIND diet (Mediterranean-DASH Intervention for Neurodegenerative Delay) has been " +
                    "shown to reduce Alzheimer's risk by up to 53%. Focus on: leafy greens, berries, " +
                    "fish, olive oil, and whole grains. Reduce red meat, butter, and processed foods."));
        }

        // Stress management
        if (profile.getStressLevel() == StressLevel.HIGH || profile.getStressLevel() == StressLevel.VERY_HIGH) {
            generated.add(buildRec(profile, RecommendationCategory.STRESS_MANAGEMENT, Priority.MEDIUM,
                    "Practice Daily Stress Reduction",
                    "Chronic stress elevates cortisol which damages hippocampal neurons. " +
                    "Practice mindfulness meditation for 20 minutes daily. " +
                    "Consider yoga, tai chi, or guided breathing exercises."));
        }

        // Smoking
        if (Boolean.TRUE.equals(profile.getSmokingStatus())) {
            generated.add(buildRec(profile, RecommendationCategory.LIFESTYLE_CHANGE, Priority.CRITICAL,
                    "Smoking Cessation Program",
                    "Smoking doubles the risk of Alzheimer's disease and dementia. " +
                    "Consult your doctor about cessation programs. " +
                    "Nicotine replacement therapy and counseling have high success rates."));
        }

        // Cognitive training
        if ("Rarely".equalsIgnoreCase(profile.getCognitiveTrainingFrequency()) ||
                "Never".equalsIgnoreCase(profile.getCognitiveTrainingFrequency())) {
            generated.add(buildRec(profile, RecommendationCategory.COGNITIVE_TRAINING, Priority.MEDIUM,
                    "Daily Cognitive Training",
                    "Engage in cognitive exercises at least 3x per week. " +
                    "Activities: puzzles, reading, learning new skills, music, or language learning. " +
                    "Consider apps like BrainHQ or Lumosity for structured training."));
        }

        // Social engagement
        if (profile.getSocialEngagementLevel() == EngagementLevel.ISOLATED ||
                profile.getSocialEngagementLevel() == EngagementLevel.LOW) {
            generated.add(buildRec(profile, RecommendationCategory.SOCIAL_ENGAGEMENT, Priority.MEDIUM,
                    "Increase Social Connections",
                    "Social isolation is a significant risk factor for cognitive decline. " +
                    "Join community groups, volunteer, or attend regular social events. " +
                    "Even brief social interactions provide cognitive benefits."));
        }

        // Regular medical checkups
        generated.add(buildRec(profile, RecommendationCategory.MEDICAL_CHECKUP, Priority.MEDIUM,
                "Annual Cognitive Assessment",
                "Schedule annual cognitive assessments including MMSE testing. " +
                "Monitor blood pressure, cholesterol, and blood sugar — all linked to brain health. " +
                "Regular checkups enable early detection and intervention."));

        List<HealthRecommendation> saved = recommendationRepository.saveAll(generated);

        // Refresh wellness score after generating recs
        profile.setWellnessScore(calculateWellnessScore(profile));
        profile.setLastAssessmentDate(LocalDateTime.now());
        profileRepository.save(profile);

        return saved;
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private HealthRecommendation buildRec(HealthProfile profile, RecommendationCategory category,
                                          Priority priority, String title, String description) {
        HealthRecommendation rec = new HealthRecommendation();
        rec.setHealthProfile(profile);
        rec.setCategory(category);
        rec.setPriority(priority);
        rec.setTitle(title);
        rec.setDescription(description);
        rec.setStatus(RecommendationStatus.ACTIVE);
        rec.setEvidenceBased(true);
        return rec;
    }

    private double calculateWellnessScore(HealthProfile p) {
        double score = 50.0; // baseline

        // Activity level (0-20 points)
        if (p.getPhysicalActivityLevel() != null) {
            score += switch (p.getPhysicalActivityLevel()) {
                case SEDENTARY -> 0;
                case LIGHTLY_ACTIVE -> 5;
                case MODERATELY_ACTIVE -> 10;
                case VERY_ACTIVE -> 15;
                case EXTREMELY_ACTIVE -> 20;
            };
        }

        // Sleep (0-10 points)
        if (p.getSleepHoursPerNight() != null) {
            double sleep = p.getSleepHoursPerNight();
            if (sleep >= 7 && sleep <= 9) score += 10;
            else if (sleep >= 6 && sleep <= 10) score += 5;
        }

        // Diet (0-10 points)
        if (p.getDietQuality() != null) {
            score += switch (p.getDietQuality()) {
                case POOR -> 0;
                case BELOW_AVERAGE -> 2;
                case AVERAGE -> 5;
                case GOOD -> 8;
                case EXCELLENT -> 10;
            };
        }

        // Stress (-10 to 0 points)
        if (p.getStressLevel() != null) {
            score += switch (p.getStressLevel()) {
                case VERY_HIGH -> -10;
                case HIGH -> -6;
                case MODERATE -> -2;
                case LOW -> 0;
                case MINIMAL -> 2;
            };
        }

        // Smoking (-10 points)
        if (Boolean.TRUE.equals(p.getSmokingStatus())) score -= 10;

        // Social engagement (0-5 points)
        if (p.getSocialEngagementLevel() != null) {
            score += switch (p.getSocialEngagementLevel()) {
                case ISOLATED -> 0;
                case LOW -> 1;
                case MODERATE -> 3;
                case HIGH -> 4;
                case VERY_HIGH -> 5;
            };
        }

        return Math.max(0, Math.min(100, score));
    }

    private String getWellnessLevel(Double score) {
        if (score == null) return "UNKNOWN";
        if (score >= 80) return "EXCELLENT";
        if (score >= 60) return "GOOD";
        if (score >= 40) return "FAIR";
        return "POOR";
    }

    private HealthProfile mapDtoToEntity(HealthProfile profile, HealthProfileDTO dto) {
        profile.setUserId(dto.getUserId());
        if (dto.getMedicalRecordId() != null) profile.setMedicalRecordId(dto.getMedicalRecordId());
        if (dto.getPhysicalActivityLevel() != null) profile.setPhysicalActivityLevel(dto.getPhysicalActivityLevel());
        if (dto.getSleepHoursPerNight() != null) profile.setSleepHoursPerNight(dto.getSleepHoursPerNight());
        if (dto.getDietQuality() != null) profile.setDietQuality(dto.getDietQuality());
        if (dto.getStressLevel() != null) profile.setStressLevel(dto.getStressLevel());
        if (dto.getSmokingStatus() != null) profile.setSmokingStatus(dto.getSmokingStatus());
        if (dto.getAlcoholConsumption() != null) profile.setAlcoholConsumption(dto.getAlcoholConsumption());
        if (dto.getSocialEngagementLevel() != null) profile.setSocialEngagementLevel(dto.getSocialEngagementLevel());
        if (dto.getCognitiveTrainingFrequency() != null) profile.setCognitiveTrainingFrequency(dto.getCognitiveTrainingFrequency());
        if (dto.getNotes() != null) profile.setNotes(dto.getNotes());
        return profile;
    }
}
