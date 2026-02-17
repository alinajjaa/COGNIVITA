package com.alzheimer.backend.medical;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface PreventionActionRepository extends JpaRepository<PreventionAction, Long> {

    Page<PreventionAction> findByMedicalRecordId(Long medicalRecordId, Pageable pageable);

    List<PreventionAction> findByMedicalRecordIdAndStatus(Long medicalRecordId, ActionStatus status);

    List<PreventionAction> findByMedicalRecordId(Long medicalRecordId);

    @Query("SELECT COUNT(pa) FROM PreventionAction pa WHERE pa.medicalRecord.id = :medicalRecordId")
    Long countByMedicalRecordId(Long medicalRecordId);

    @Query("SELECT COUNT(pa) FROM PreventionAction pa WHERE pa.medicalRecord.id = :medicalRecordId AND pa.status = :status")
    Long countByMedicalRecordIdAndStatus(Long medicalRecordId, ActionStatus status);

    List<PreventionAction> findByMedicalRecordIdAndActionDateBetween(
        Long medicalRecordId, LocalDateTime startDate, LocalDateTime endDate);
}
