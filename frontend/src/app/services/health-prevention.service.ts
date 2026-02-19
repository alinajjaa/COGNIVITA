import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// ─── Enums (mirror Java enums) ────────────────────────────────────────────────
export type ActivityLevel = 'SEDENTARY' | 'LIGHTLY_ACTIVE' | 'MODERATELY_ACTIVE' | 'VERY_ACTIVE' | 'EXTREMELY_ACTIVE';
export type DietQuality = 'POOR' | 'BELOW_AVERAGE' | 'AVERAGE' | 'GOOD' | 'EXCELLENT';
export type StressLevel = 'MINIMAL' | 'LOW' | 'MODERATE' | 'HIGH' | 'VERY_HIGH';
export type AlcoholConsumption = 'NONE' | 'OCCASIONAL' | 'MODERATE' | 'HEAVY';
export type EngagementLevel = 'ISOLATED' | 'LOW' | 'MODERATE' | 'HIGH' | 'VERY_HIGH';
export type RecommendationCategory = 'PHYSICAL_ACTIVITY' | 'NUTRITION' | 'SLEEP' | 'COGNITIVE_TRAINING'
  | 'STRESS_MANAGEMENT' | 'SOCIAL_ENGAGEMENT' | 'MEDICATION' | 'MEDICAL_CHECKUP' | 'LIFESTYLE_CHANGE';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type RecommendationStatus = 'ACTIVE' | 'COMPLETED' | 'DISMISSED' | 'PENDING_REVIEW';
export type ActivityType = 'AEROBIC' | 'STRENGTH_TRAINING' | 'FLEXIBILITY' | 'COGNITIVE'
  | 'SOCIAL' | 'MEDITATION' | 'WALKING' | 'SWIMMING' | 'YOGA' | 'OTHER';
export type IntensityLevel = 'LIGHT' | 'MODERATE' | 'VIGOROUS' | 'MAXIMUM';

// ─── Models ───────────────────────────────────────────────────────────────────
export interface HealthProfile {
  id?: number;
  userId: number;
  medicalRecordId?: number;
  physicalActivityLevel?: ActivityLevel;
  sleepHoursPerNight?: number;
  dietQuality?: DietQuality;
  stressLevel?: StressLevel;
  smokingStatus?: boolean;
  alcoholConsumption?: AlcoholConsumption;
  socialEngagementLevel?: EngagementLevel;
  cognitiveTrainingFrequency?: string;
  wellnessScore?: number;
  lastAssessmentDate?: string;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface HealthRecommendation {
  id?: number;
  healthProfileId: number;
  category: RecommendationCategory;
  title: string;
  description?: string;
  priority?: Priority;
  status?: RecommendationStatus;
  evidenceBased?: boolean;
  targetDate?: string;
  completedDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface WellnessActivity {
  id?: number;
  healthProfileId: number;
  activityName: string;
  activityType?: ActivityType;
  durationMinutes?: number;
  activityDate: string;
  intensityLevel?: IntensityLevel;
  moodBefore?: string;
  moodAfter?: string;
  notes?: string;
  caloriesBurned?: number;
  createdAt?: string;
}

export interface WellnessDashboard {
  userId: number;
  healthProfileId: number;
  wellnessScore: number;
  wellnessLevel: string;
  totalRecommendations: number;
  activeRecommendations: number;
  completedRecommendations: number;
  totalActivities: number;
  activitiesThisWeek: number;
  totalActivityMinutes: number;
  activitiesByType: Record<string, number>;
  recommendationsByCategory: Record<string, number>;
  lastAssessmentDate?: string;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class HealthPreventionService {
  private readonly base = 'http://localhost:9090/api/health-prevention';

  constructor(private http: HttpClient) {}

  // ─── Health Profiles ──────────────────────────────────────────────────────
  createProfile(profile: HealthProfile): Observable<ApiResponse<HealthProfile>> {
    return this.http.post<ApiResponse<HealthProfile>>(`${this.base}/profiles`, profile);
  }

  getProfileByUserId(userId: number): Observable<ApiResponse<HealthProfile>> {
    return this.http.get<ApiResponse<HealthProfile>>(`${this.base}/profiles/user/${userId}`);
  }

  getProfileById(id: number): Observable<ApiResponse<HealthProfile>> {
    return this.http.get<ApiResponse<HealthProfile>>(`${this.base}/profiles/${id}`);
  }

  updateProfile(id: number, profile: HealthProfile): Observable<ApiResponse<HealthProfile>> {
    return this.http.put<ApiResponse<HealthProfile>>(`${this.base}/profiles/${id}`, profile);
  }

  deleteProfile(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/profiles/${id}`);
  }

  getDashboard(userId: number): Observable<ApiResponse<WellnessDashboard>> {
    return this.http.get<ApiResponse<WellnessDashboard>>(`${this.base}/profiles/user/${userId}/dashboard`);
  }

  generateRecommendations(profileId: number): Observable<ApiResponse<HealthRecommendation[]>> {
    return this.http.post<ApiResponse<HealthRecommendation[]>>(
      `${this.base}/profiles/${profileId}/generate-recommendations`, {}
    );
  }

  // ─── Recommendations ──────────────────────────────────────────────────────
  createRecommendation(rec: HealthRecommendation): Observable<ApiResponse<HealthRecommendation>> {
    return this.http.post<ApiResponse<HealthRecommendation>>(`${this.base}/recommendations`, rec);
  }

  getRecommendationsByProfile(profileId: number): Observable<ApiResponse<HealthRecommendation[]>> {
    return this.http.get<ApiResponse<HealthRecommendation[]>>(
      `${this.base}/recommendations/profile/${profileId}`
    );
  }

  getRecommendationsByStatus(profileId: number, status: RecommendationStatus): Observable<ApiResponse<HealthRecommendation[]>> {
    return this.http.get<ApiResponse<HealthRecommendation[]>>(
      `${this.base}/recommendations/profile/${profileId}/status/${status}`
    );
  }

  completeRecommendation(id: number): Observable<ApiResponse<HealthRecommendation>> {
    return this.http.patch<ApiResponse<HealthRecommendation>>(
      `${this.base}/recommendations/${id}/complete`, {}
    );
  }

  deleteRecommendation(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/recommendations/${id}`);
  }

  // ─── Wellness Activities ──────────────────────────────────────────────────
  logActivity(activity: WellnessActivity): Observable<ApiResponse<WellnessActivity>> {
    return this.http.post<ApiResponse<WellnessActivity>>(`${this.base}/activities`, activity);
  }

  getActivitiesByProfile(profileId: number): Observable<ApiResponse<WellnessActivity[]>> {
    return this.http.get<ApiResponse<WellnessActivity[]>>(
      `${this.base}/activities/profile/${profileId}`
    );
  }

  deleteActivity(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/activities/${id}`);
  }
}
