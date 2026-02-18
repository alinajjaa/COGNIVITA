// Medical Timeline Event Model
export interface TimelineEvent {
  id?: number;
  medicalRecordId: number;
  eventDate: string;
  eventType: EventType;
  description: string;
  changedFields?: string;
  performedBy?: string;
  createdAt?: string;
}

export enum EventType {
  RECORD_CREATED = 'RECORD_CREATED',
  RECORD_UPDATED = 'RECORD_UPDATED',
  RISK_FACTOR_ADDED = 'RISK_FACTOR_ADDED',
  RISK_FACTOR_UPDATED = 'RISK_FACTOR_UPDATED',
  RISK_FACTOR_REMOVED = 'RISK_FACTOR_REMOVED',
  PREVENTION_ACTION_ADDED = 'PREVENTION_ACTION_ADDED',
  PREVENTION_ACTION_COMPLETED = 'PREVENTION_ACTION_COMPLETED',
  PREVENTION_ACTION_CANCELLED = 'PREVENTION_ACTION_CANCELLED',
  RISK_SCORE_UPDATED = 'RISK_SCORE_UPDATED',
  RECOMMENDATION_GENERATED = 'RECOMMENDATION_GENERATED'
}

export const EVENT_TYPE_LABELS: Record<EventType, string> = {
  [EventType.RECORD_CREATED]: 'Record Created',
  [EventType.RECORD_UPDATED]: 'Record Updated',
  [EventType.RISK_FACTOR_ADDED]: 'Risk Factor Added',
  [EventType.RISK_FACTOR_UPDATED]: 'Risk Factor Modified',
  [EventType.RISK_FACTOR_REMOVED]: 'Risk Factor Removed',
  [EventType.PREVENTION_ACTION_ADDED]: 'Prevention Action Added',
  [EventType.PREVENTION_ACTION_COMPLETED]: 'Action Completed',
  [EventType.PREVENTION_ACTION_CANCELLED]: 'Action Cancelled',
  [EventType.RISK_SCORE_UPDATED]: 'Risk Score Updated',
  [EventType.RECOMMENDATION_GENERATED]: 'Recommendations Generated'
};

export const EVENT_TYPE_ICONS: Record<EventType, string> = {
  [EventType.RECORD_CREATED]: '📋',
  [EventType.RECORD_UPDATED]: '✏️',
  [EventType.RISK_FACTOR_ADDED]: '⚠️',
  [EventType.RISK_FACTOR_UPDATED]: '🔄',
  [EventType.RISK_FACTOR_REMOVED]: '❌',
  [EventType.PREVENTION_ACTION_ADDED]: '➕',
  [EventType.PREVENTION_ACTION_COMPLETED]: '✅',
  [EventType.PREVENTION_ACTION_CANCELLED]: '🚫',
  [EventType.RISK_SCORE_UPDATED]: '📊',
  [EventType.RECOMMENDATION_GENERATED]: '💡'
};
