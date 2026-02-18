package tn.esprit.cognivita.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_participations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private CognitiveActivity activity;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "activity_id", insertable = false, updatable = false)
    private Long activityId;

    private Integer score;
    private Integer timeSpent;
    private Integer completionTimeSeconds;

    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;  // ← AJOUTE CE CHAMP

    private Boolean completed = false;
    private Boolean abandoned = false;

    private LocalDateTime participationDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        startTime = LocalDateTime.now();
        participationDate = LocalDateTime.now();
        if (status == null) {
            status = ParticipationStatus.IN_PROGRESS;
        }
    }
}