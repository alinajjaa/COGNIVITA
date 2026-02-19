package com.alzheimer.healthprevention.service;

import com.alzheimer.healthprevention.dto.WellnessActivityDTO;
import com.alzheimer.healthprevention.entity.HealthProfile;
import com.alzheimer.healthprevention.entity.WellnessActivity;
import com.alzheimer.healthprevention.repository.HealthProfileRepository;
import com.alzheimer.healthprevention.repository.WellnessActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class WellnessActivityService {

    private final WellnessActivityRepository activityRepository;
    private final HealthProfileRepository profileRepository;

    public WellnessActivityService(WellnessActivityRepository activityRepository,
                                   HealthProfileRepository profileRepository) {
        this.activityRepository = activityRepository;
        this.profileRepository = profileRepository;
    }

    public WellnessActivity logActivity(WellnessActivityDTO dto) {
        HealthProfile profile = profileRepository.findById(dto.getHealthProfileId())
                .orElseThrow(() -> new NoSuchElementException("Health profile not found: " + dto.getHealthProfileId()));
        WellnessActivity activity = mapDtoToEntity(new WellnessActivity(), dto);
        activity.setHealthProfile(profile);
        return activityRepository.save(activity);
    }

    @Transactional(readOnly = true)
    public List<WellnessActivity> getActivitiesByProfileId(Long profileId) {
        return activityRepository.findByHealthProfileIdOrderByActivityDateDesc(profileId);
    }

    @Transactional(readOnly = true)
    public Optional<WellnessActivity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public WellnessActivity updateActivity(Long id, WellnessActivityDTO dto) {
        WellnessActivity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity not found: " + id));
        return activityRepository.save(mapDtoToEntity(activity, dto));
    }

    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new NoSuchElementException("Activity not found: " + id);
        }
        activityRepository.deleteById(id);
    }

    private WellnessActivity mapDtoToEntity(WellnessActivity activity, WellnessActivityDTO dto) {
        if (dto.getActivityName() != null) activity.setActivityName(dto.getActivityName());
        if (dto.getActivityType() != null) activity.setActivityType(dto.getActivityType());
        if (dto.getDurationMinutes() != null) activity.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getActivityDate() != null) activity.setActivityDate(LocalDateTime.parse(dto.getActivityDate()));
        if (dto.getIntensityLevel() != null) activity.setIntensityLevel(dto.getIntensityLevel());
        if (dto.getMoodBefore() != null) activity.setMoodBefore(dto.getMoodBefore());
        if (dto.getMoodAfter() != null) activity.setMoodAfter(dto.getMoodAfter());
        if (dto.getNotes() != null) activity.setNotes(dto.getNotes());
        if (dto.getCaloriesBurned() != null) activity.setCaloriesBurned(dto.getCaloriesBurned());
        return activity;
    }
}
