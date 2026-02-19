// Medical Record Model with Risk Assessment
export interface MedicalRecord {
  id?: number;
  userId: number;
  userName?: string;
  userEmail?: string;
  age: number;
  gender: 'Male' | 'Female' | 'Other';
  educationLevel: string;
  familyHistory: 'Yes' | 'No';
  riskFactors: string;
  currentSymptoms: string;
  diagnosisNotes: string;
  riskScore?: number;
  riskLevel?: RiskLevel;
  lastRiskCalculation?: string;
  createdAt?: string;
  updatedAt?: string;
}

export enum RiskLevel {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export interface MedicalRecordWithDetails extends MedicalRecord {
  riskFactorsList?: any[];
  preventionActions?: any[];
  timeline?: any[];
  recommendations?: any[];
}

export const EDUCATION_LEVELS = [
  'No formal education',
  'Primary school',
  'High school',
  'Some college',
  'Bachelor\'s degree',
  'Master\'s degree',
  'Doctorate/PhD',
  'Other'
] as const;

export type EducationLevel = typeof EDUCATION_LEVELS[number];
