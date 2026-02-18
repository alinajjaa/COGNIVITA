// Prevention Action Model
export interface PreventionAction {
  id?: number;
  medicalRecordId: number;
  actionType: string;
  description: string;
  actionDate: string;
  status: ActionStatus;
  result?: string;
  frequency?: string;
  completedDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export enum ActionStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  SKIPPED = 'SKIPPED'
}

export const ACTION_TYPES = [
  'Physical Exercise',
  'Medication',
  'Diet Modification',
  'Cognitive Training',
  'Social Activity',
  'Stress Management',
  'Sleep Hygiene',
  'Medical Consultation',
  'Health Screening',
  'Therapy Session'
] as const;

export const FREQUENCY_OPTIONS = [
  'Daily',
  'Every Other Day',
  'Weekly',
  'Bi-Weekly',
  'Monthly',
  'As Needed',
  'One-time'
] as const;

export type ActionType = typeof ACTION_TYPES[number];
export type Frequency = typeof FREQUENCY_OPTIONS[number];
