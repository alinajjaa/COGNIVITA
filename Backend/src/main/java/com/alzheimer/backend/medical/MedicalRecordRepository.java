package com.alzheimer.backend.medical;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    @Query("SELECT m FROM MedicalRecord m JOIN FETCH m.user")
    List<MedicalRecord> findAllWithUser();
    
    @Query("SELECT m FROM MedicalRecord m JOIN FETCH m.user WHERE m.id = :id")
    Optional<MedicalRecord> findByIdWithUser(Long id);
    
    @Query("SELECT m FROM MedicalRecord m JOIN FETCH m.user WHERE m.user.id = :userId")
    List<MedicalRecord> findByUserIdWithUser(Long userId);
    
    @Query("SELECT m FROM MedicalRecord m JOIN FETCH m.user WHERE m.user.id = :userId ORDER BY m.createdAt DESC")
    Optional<MedicalRecord> findFirstByUserIdOrderByCreatedAtDescWithUser(Long userId);
    
    List<MedicalRecord> findByUserId(Long userId);
    
    Optional<MedicalRecord> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<MedicalRecord> findByGender(Gender gender);
    
    List<MedicalRecord> findByFamilyHistory(FamilyHistory familyHistory);
    
    List<MedicalRecord> findByAgeBetween(Integer minAge, Integer maxAge);
    
    List<MedicalRecord> findByEducationLevel(String educationLevel);
}
