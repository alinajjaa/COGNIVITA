package tn.esprit.cognivita.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.cognivita.entity.ActivityParticipation;
import tn.esprit.cognivita.entity.ParticipationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour ActivityParticipation
 * @author NeuroTech Innovators
 */
@Repository
public interface ActivityParticipationRepository extends JpaRepository<ActivityParticipation, Long> {

    // Toutes les participations d'un utilisateur
    List<ActivityParticipation> findByUserId(Long userId);

    // Participations par statut d'un utilisateur
    List<ActivityParticipation> findByUserIdAndStatus(Long userId, ParticipationStatus status);

    // Participations à une activité spécifique
    List<ActivityParticipation> findByActivityId(Long activityId);

    // Participations d'un utilisateur à une activité
    List<ActivityParticipation> findByUserIdAndActivityId(Long userId, Long activityId);

    // Nombre de participations par utilisateur
    Long countByUserId(Long userId);

    // Nombre de participations par utilisateur et statut
    Long countByUserIdAndStatus(Long userId, ParticipationStatus status);

    // Dernières participations
    List<ActivityParticipation> findTop10ByUserIdOrderByParticipationDateDesc(Long userId);

    // Participations dans une période
    List<ActivityParticipation> findByUserIdAndParticipationDateBetween(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);

    // Score moyen d'un utilisateur
    @Query("SELECT AVG(p.score) FROM ActivityParticipation p WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    Double calculateAverageScore(@Param("userId") Long userId);

    // Total des points gagnés
    @Query("SELECT COALESCE(SUM(ca.maxScore), 0) FROM ActivityParticipation p " +
            "JOIN CognitiveActivity ca ON p.activityId = ca.id " +
            "WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    Integer calculateTotalPoints(@Param("userId") Long userId);

    // Temps moyen de complétion
    @Query("SELECT AVG(p.completionTimeSeconds) FROM ActivityParticipation p " +
            "WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    Double calculateAverageCompletionTime(@Param("userId") Long userId);

    // Activités complétées (distinctes)
    @Query("SELECT COUNT(DISTINCT p.activityId) FROM ActivityParticipation p " +
            "WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    Long countDistinctCompletedActivities(@Param("userId") Long userId);
}