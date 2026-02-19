package com.alzheimer.healthprevention.service;

import com.alzheimer.healthprevention.dto.HealthRecommendationDTO;
import com.alzheimer.healthprevention.entity.*;
import com.alzheimer.healthprevention.repository.HealthProfileRepository;
import com.alzheimer.healthprevention.repository.HealthRecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class HealthRecommendationService {

    private final HealthRecommendationRepository recommendationRepository;
    private final HealthProfileRepository profileRepository;

    public HealthRecommendationService(HealthRecommendationRepository recommendationRepository,
                                       HealthProfileRepository profileRepository) {
        this.recommendationRepository = recommendationRepository;
        this.profileRepository = profileRepository;
    }

    public HealthRecommendation createRecommendation(HealthRecommendationDTO dto) {
        HealthProfile profile = profileRepository.findById(dto.getHealthProfileId())
                .orElseThrow(() -> new NoSuchElementException("Health profile not found: " + dto.getHealthProfileId()));
        HealthRecommendation rec = mapDtoToEntity(new HealthRecommendation(), dto);
        rec.setHealthProfile(profile);
        return recommendationRepository.save(rec);
    }

    @Transactional(readOnly = true)
    public List<HealthRecommendation> getByProfileId(Long profileId) {
        return recommendationRepository.findByHealthProfileId(profileId);
    }

    @Transactional(readOnly = true)
    public List<HealthRecommendation> getByProfileIdAndStatus(Long profileId, RecommendationStatus status) {
        return recommendationRepository.findByHealthProfileIdAndStatus(profileId, status);
    }

    @Transactional(readOnly = true)
    public List<HealthRecommendation> getByProfileIdAndCategory(Long profileId, RecommendationCategory category) {
        return recommendationRepository.findByHealthProfileIdAndCategory(profileId, category);
    }

    @Transactional(readOnly = true)
    public Optional<HealthRecommendation> getById(Long id) {
        return recommendationRepository.findById(id);
    }

    public HealthRecommendation updateRecommendation(Long id, HealthRecommendationDTO dto) {
        HealthRecommendation rec = recommendationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recommendation not found: " + id));
        return recommendationRepository.save(mapDtoToEntity(rec, dto));
    }

    public HealthRecommendation completeRecommendation(Long id) {
        HealthRecommendation rec = recommendationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recommendation not found: " + id));
        rec.setStatus(RecommendationStatus.COMPLETED);
        rec.setCompletedDate(LocalDateTime.now());
        return recommendationRepository.save(rec);
    }

    public void deleteRecommendation(Long id) {
        if (!recommendationRepository.existsById(id)) {
            throw new NoSuchElementException("Recommendation not found: " + id);
        }
        recommendationRepository.deleteById(id);
    }

    private HealthRecommendation mapDtoToEntity(HealthRecommendation rec, HealthRecommendationDTO dto) {
        if (dto.getCategory() != null) rec.setCategory(dto.getCategory());
        if (dto.getTitle() != null) rec.setTitle(dto.getTitle());
        if (dto.getDescription() != null) rec.setDescription(dto.getDescription());
        if (dto.getPriority() != null) rec.setPriority(dto.getPriority());
        if (dto.getStatus() != null) rec.setStatus(dto.getStatus());
        if (dto.getEvidenceBased() != null) rec.setEvidenceBased(dto.getEvidenceBased());
        if (dto.getTargetDate() != null) rec.setTargetDate(LocalDateTime.parse(dto.getTargetDate()));
        return rec;
    }
}
