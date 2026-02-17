package com.alzheimer.backend.medical;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RiskFactorRepository extends JpaRepository<RiskFactor, Long> {

    Page<RiskFactor> findByMedicalRecordId(Long medicalRecordId, Pageable pageable);

    List<RiskFactor> findByMedicalRecordIdAndIsActive(Long medicalRecordId, Boolean isActive);

    @Query("SELECT COUNT(rf) FROM RiskFactor rf WHERE rf.medicalRecord.id = :medicalRecordId")
    Long countByMedicalRecordId(Long medicalRecordId);

    @Query("SELECT COUNT(rf) FROM RiskFactor rf WHERE rf.medicalRecord.id = :medicalRecordId AND rf.isActive = :isActive")
    Long countByMedicalRecordIdAndIsActive(Long medicalRecordId, Boolean isActive);

    List<RiskFactor> findByMedicalRecordId(Long medicalRecordId);
}
