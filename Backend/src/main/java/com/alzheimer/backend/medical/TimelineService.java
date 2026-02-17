package com.alzheimer.backend.medical;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimelineService {

    private final TimelineRepository timelineRepository;

    public TimelineService(TimelineRepository timelineRepository) {
        this.timelineRepository = timelineRepository;
    }

    public void logRiskFactorAdded(MedicalRecord record, RiskFactor riskFactor) {
        log(record, EventType.RISK_FACTOR_ADDED,
            "Risk factor added: " + riskFactor.getFactorType() + " (Severity: " + riskFactor.getSeverity() + ")");
    }

    public void logRiskFactorUpdated(MedicalRecord record, RiskFactor riskFactor) {
        log(record, EventType.RISK_FACTOR_UPDATED,
            "Risk factor updated: " + riskFactor.getFactorType() + " (Severity: " + riskFactor.getSeverity() + ")");
    }

    public void logRiskFactorRemoved(MedicalRecord record, RiskFactor riskFactor) {
        log(record, EventType.RISK_FACTOR_REMOVED,
            "Risk factor deactivated: " + riskFactor.getFactorType());
    }

    public void logPreventionActionAdded(MedicalRecord record, PreventionAction action) {
        log(record, EventType.PREVENTION_ACTION_ADDED,
            "Prevention action recorded: " + action.getActionType());
    }

    public void logPreventionActionUpdated(MedicalRecord record, PreventionAction action) {
        log(record, EventType.PREVENTION_ACTION_UPDATED,
            "Prevention action updated: " + action.getActionType() + " -> " + action.getStatus());
    }

    public void logMedicalRecordUpdated(MedicalRecord record) {
        log(record, EventType.MEDICAL_RECORD_UPDATED, "Medical record information was updated");
    }

    private void log(MedicalRecord record, EventType eventType, String description) {
        MedicalTimeline timeline = new MedicalTimeline();
        timeline.setMedicalRecord(record);
        timeline.setEventType(eventType);
        timeline.setDescription(description);
        timeline.setEventDate(LocalDateTime.now());
        timelineRepository.save(timeline);
    }
}
