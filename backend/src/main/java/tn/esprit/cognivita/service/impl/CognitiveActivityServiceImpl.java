package tn.esprit.cognivita.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.cognivita.entity.CognitiveActivity;
import tn.esprit.cognivita.entity.ActivityParticipation;
import tn.esprit.cognivita.entity.ParticipationStatus;
import tn.esprit.cognivita.repository.CognitiveActivityRepository;
import tn.esprit.cognivita.repository.ActivityParticipationRepository;
import tn.esprit.cognivita.service.CognitiveActivityService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CognitiveActivityServiceImpl implements CognitiveActivityService {

    private final CognitiveActivityRepository activityRepository;
    private final ActivityParticipationRepository participationRepository;

    // ============= CRUD OPERATIONS =============

    @Override
    public CognitiveActivity createActivity(CognitiveActivity activity) {
        activity.setIsActive(true);
        return activityRepository.save(activity);
    }

    @Override
    public CognitiveActivity updateActivity(Long id, CognitiveActivity activityDetails) {
        CognitiveActivity activity = getActivityById(id);

        activity.setTitle(activityDetails.getTitle());
        activity.setDescription(activityDetails.getDescription());
        activity.setType(activityDetails.getType());
        activity.setDifficulty(activityDetails.getDifficulty());
        activity.setContent(activityDetails.getContent());
        activity.setTimeLimit(activityDetails.getTimeLimit());
        activity.setMaxScore(activityDetails.getMaxScore());
        activity.setInstructions(activityDetails.getInstructions());
        activity.setImageUrl(activityDetails.getImageUrl());

        return activityRepository.save(activity);
    }

    @Override
    public void deleteActivity(Long id) {
        CognitiveActivity activity = getActivityById(id);
        activityRepository.delete(activity);
    }

    @Override
    public CognitiveActivity getActivityById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));
    }

    @Override
    public List<CognitiveActivity> getAllActivities() {
        return activityRepository.findByIsActiveTrue();
    }

    // ============= FILTERS =============

    @Override
    public List<CognitiveActivity> getActivitiesByType(String type) {
        return activityRepository.findByType(type);
    }

    @Override
    public List<CognitiveActivity> getActivitiesByDifficulty(String difficulty) {
        return activityRepository.findByDifficulty(difficulty);
    }

    @Override
    public List<CognitiveActivity> filterActivities(String type, String difficulty) {
        return activityRepository.filterActivities(type, difficulty);
    }

    @Override
    public List<CognitiveActivity> searchActivities(String keyword) {
        return activityRepository.searchByTitleOrDescription(keyword);
    }

    // ============= ACTIVITY STATUS =============

    @Override
    public void deactivateActivity(Long id) {
        CognitiveActivity activity = getActivityById(id);
        activity.setIsActive(false);
        activityRepository.save(activity);
    }

    // ============= PARTICIPATION =============

    @Override
    public ActivityParticipation startActivity(Long activityId, Long userId) {
        CognitiveActivity activity = getActivityById(activityId);

        ActivityParticipation participation = new ActivityParticipation();
        participation.setActivity(activity);
        participation.setUserId(userId);
        participation.setStatus(ParticipationStatus.IN_PROGRESS);
        participation.setStartTime(LocalDateTime.now());

        return participationRepository.save(participation);
    }

    @Override
    public ActivityParticipation completeActivity(Long participationId, Integer score, Integer timeSpent) {
        ActivityParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found with id: " + participationId));

        participation.setScore(score);
        participation.setTimeSpent(timeSpent);
        participation.setCompleted(true);
        participation.setStatus(ParticipationStatus.COMPLETED);
        participation.setCompletedAt(LocalDateTime.now());
        participation.setEndTime(LocalDateTime.now());

        return participationRepository.save(participation);
    }

    @Override
    public ActivityParticipation abandonActivity(Long participationId) {
        ActivityParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found with id: " + participationId));

        participation.setAbandoned(true);
        participation.setStatus(ParticipationStatus.ABANDONED);
        participation.setEndTime(LocalDateTime.now());

        return participationRepository.save(participation);
    }

    // ============= USER HISTORY =============

    @Override
    public List<ActivityParticipation> getUserHistory(Long userId) {
        return participationRepository.findByUserId(userId);
    }

    @Override
    public List<CognitiveActivity> getUserCompletedActivities(Long userId) {
        List<ActivityParticipation> participations =
                participationRepository.findByUserIdAndStatus(userId, ParticipationStatus.COMPLETED);

        return participations.stream()
                .map(ActivityParticipation::getActivity)
                .distinct()
                .collect(Collectors.toList());
    }

    // ============= RECOMMENDATIONS =============

    @Override
    public List<CognitiveActivity> getRecommendationsForUser(Long userId) {
        // Récupérer les activités déjà complétées par l'utilisateur
        List<CognitiveActivity> completedActivities = getUserCompletedActivities(userId);
        Set<Long> completedIds = completedActivities.stream()
                .map(CognitiveActivity::getId)
                .collect(Collectors.toSet());

        // Récupérer toutes les activités actives
        List<CognitiveActivity> allActivities = activityRepository.findByIsActiveTrue();

        // Filtrer les activités non complétées
        return allActivities.stream()
                .filter(activity -> !completedIds.contains(activity.getId()))
                .limit(5)
                .collect(Collectors.toList());
    }

    // ============= STATISTICS =============

    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("userId", userId);
        stats.put("totalActivities", participationRepository.countByUserId(userId));
        stats.put("completedActivities",
                participationRepository.countByUserIdAndStatus(userId, ParticipationStatus.COMPLETED));
        stats.put("abandonedActivities",
                participationRepository.countByUserIdAndStatus(userId, ParticipationStatus.ABANDONED));
        stats.put("averageScore", participationRepository.calculateAverageScore(userId));
        stats.put("totalPoints", participationRepository.calculateTotalPoints(userId));
        stats.put("averageCompletionTime", participationRepository.calculateAverageCompletionTime(userId));
        stats.put("distinctCompletedActivities",
                participationRepository.countDistinctCompletedActivities(userId));

        return stats;
    }

    @Override
    public Map<String, Object> getGlobalStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<CognitiveActivity> allActivities = activityRepository.findAll();
        List<ActivityParticipation> allParticipations = participationRepository.findAll();

        stats.put("totalActivities", allActivities.size());
        stats.put("totalParticipations", allParticipations.size());
        stats.put("activeActivities",
                allActivities.stream().filter(CognitiveActivity::getIsActive).count());

        // Statistiques par type
        Map<String, Long> byType = allActivities.stream()
                .collect(Collectors.groupingBy(CognitiveActivity::getType, Collectors.counting()));
        stats.put("activitiesByType", byType);

        return stats;
    }
}