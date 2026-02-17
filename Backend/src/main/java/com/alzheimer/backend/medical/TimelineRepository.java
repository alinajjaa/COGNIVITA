package com.alzheimer.backend.medical;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface TimelineRepository extends JpaRepository<MedicalTimeline, Long> {

    List<MedicalTimeline> findByMedicalRecordIdOrderByEventDateDesc(Long medicalRecordId);

    List<MedicalTimeline> findByMedicalRecordIdAndEventType(Long medicalRecordId, EventType eventType);

    List<MedicalTimeline> findByMedicalRecordIdAndEventDateBetweenOrderByEventDateDesc(
            Long medicalRecordId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM MedicalTimeline t WHERE t.medicalRecord.user.id = :userId ORDER BY t.eventDate DESC")
    List<MedicalTimeline> findByUserId(Long userId);
}
