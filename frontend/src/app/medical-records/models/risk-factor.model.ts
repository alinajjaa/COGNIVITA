// Risk Factor Model
export interface RiskFactor {
  id?: number;
  medicalRecordId: number;
  factorType: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  diagnosedDate?: string;
  notes?: string;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface RiskFactorDTO {
  id: number;
  medicalRecordId: number;
  factorType: string;
  severity: string;
  diagnosedDate: string;
  notes: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export const RISK_FACTOR_TYPES = [
  'Hypertension',
  'Diabetes',
  'Cardiovascular Disease',
  'Smoking',
  'Obesity',
  'High Cholesterol',
  'Sleep Apnea',
  'Depression',
  'Head Trauma',
  'Sedentary Lifestyle',
  'Poor Diet',
  'Social Isolation',
  'Hearing Loss',
  'Vision Problems'
] as const;

export type RiskFactorType = typeof RISK_FACTOR_TYPES[number];
