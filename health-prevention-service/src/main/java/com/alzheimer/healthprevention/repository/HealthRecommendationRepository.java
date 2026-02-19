package com.alzheimer.healthprevention.repository;

import com.alzheimer.healthprevention.entity.HealthRecommendation;
import com.alzheimer.healthprevention.entity.RecommendationCategory;
import com.alzheimer.healthprevention.entity.RecommendationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthRecommendationRepository extends JpaRepository<HealthRecommendation, Long> {
    List<HealthRecommendation> findByHealthProfileId(Long healthProfileId);
    List<HealthRecommendation> findByHealthProfileIdAndStatus(Long healthProfileId, RecommendationStatus status);
    List<HealthRecommendation> findByHealthProfileIdAndCategory(Long healthProfileId, RecommendationCategory category);
    long countByHealthProfileIdAndStatus(Long healthProfileId, RecommendationStatus status);
}
