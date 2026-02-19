package com.alzheimer.healthprevention.repository;

import com.alzheimer.healthprevention.entity.WellnessActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WellnessActivityRepository extends JpaRepository<WellnessActivity, Long> {
    List<WellnessActivity> findByHealthProfileIdOrderByActivityDateDesc(Long healthProfileId);

    @Query("SELECT w FROM WellnessActivity w WHERE w.healthProfile.id = :profileId AND w.activityDate >= :startDate")
    List<WellnessActivity> findByHealthProfileIdAndActivityDateAfter(
            @Param("profileId") Long profileId,
            @Param("startDate") LocalDateTime startDate
    );

    @Query("SELECT COALESCE(SUM(w.durationMinutes), 0) FROM WellnessActivity w WHERE w.healthProfile.id = :profileId")
    Integer sumDurationByHealthProfileId(@Param("profileId") Long profileId);
}
