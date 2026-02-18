// src/app/services/cognitive-activity.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// ============= INTERFACES =============
export interface CognitiveActivity {
  id?: number;
  title: string;
  description?: string;
  type: 'MEMORY' | 'ATTENTION' | 'LOGIC';
  difficulty: 'EASY' | 'MEDIUM' | 'HARD';
  content?: string;
  timeLimit?: number;
  maxScore?: number;
  instructions?: string;
  imageUrl?: string;
  isActive?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface ActivityParticipation {
  id?: number;
  activityId: number;
  userId: number;
  score?: number;
  timeSpent?: number;
  status?: 'IN_PROGRESS' | 'COMPLETED' | 'ABANDONED';
  startTime?: Date;
  completedAt?: Date;
}

@Injectable({
  providedIn: 'root'
})
export class CognitiveActivityService {

  private apiUrl = `${environment.apiUrl}/activities`;

  constructor(private http: HttpClient) { }

  // ============= CRUD OPERATIONS =============

  getAllActivities(): Observable<CognitiveActivity[]> {
    return this.http.get<CognitiveActivity[]>(this.apiUrl);
  }

  getActivityById(id: number): Observable<CognitiveActivity> {
    return this.http.get<CognitiveActivity>(`${this.apiUrl}/${id}`);
  }

  createActivity(activity: CognitiveActivity): Observable<CognitiveActivity> {
    return this.http.post<CognitiveActivity>(this.apiUrl, activity);
  }

  updateActivity(id: number, activity: CognitiveActivity): Observable<CognitiveActivity> {
    return this.http.put<CognitiveActivity>(`${this.apiUrl}/${id}`, activity);
  }

  deleteActivity(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  deactivateActivity(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/deactivate`, {});
  }

  // ============= FILTERS =============

  getActivitiesByType(type: string): Observable<CognitiveActivity[]> {
    return this.http.get<CognitiveActivity[]>(`${this.apiUrl}/type/${type}`);
  }

  getActivitiesByDifficulty(difficulty: string): Observable<CognitiveActivity[]> {
    return this.http.get<CognitiveActivity[]>(`${this.apiUrl}/difficulty/${difficulty}`);
  }

  filterActivities(type?: string, difficulty?: string): Observable<CognitiveActivity[]> {
    let params = new HttpParams();
    if (type) params = params.set('type', type);
    if (difficulty) params = params.set('difficulty', difficulty);
    return this.http.get<CognitiveActivity[]>(`${this.apiUrl}/filter`, { params });
  }

  searchActivities(keyword: string): Observable<CognitiveActivity[]> {
    return this.http.get<CognitiveActivity[]>(`${this.apiUrl}/search?keyword=${keyword}`);
  }

  // ============= PARTICIPATIONS =============

  startActivity(activityId: number, userId: number): Observable<ActivityParticipation> {
    return this.http.post<ActivityParticipation>(
      `${this.apiUrl}/${activityId}/start?userId=${userId}`,
      {}
    );
  }

  completeActivity(participationId: number, score: number, timeSpent: number): Observable<ActivityParticipation> {
    return this.http.put<ActivityParticipation>(
      `${this.apiUrl}/participations/${participationId}/complete?score=${score}&timeSpent=${timeSpent}`,
      {}
    );
  }

  abandonActivity(participationId: number): Observable<ActivityParticipation> {
    return this.http.put<ActivityParticipation>(
      `${this.apiUrl}/participations/${participationId}/abandon`,
      {}
    );
  }

  // ============= USER HISTORY =============

  getUserHistory(userId: number): Observable<ActivityParticipation[]> {
    return this.http.get<ActivityParticipation[]>(`${this.apiUrl}/users/${userId}/history`);
  }

  getUserCompletedActivities(userId: number): Observable<CognitiveActivity[]> {
    return this.http.get<CognitiveActivity[]>(`${this.apiUrl}/users/${userId}/completed`);
  }

  // ============= STATISTICS =============

  getUserStatistics(userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/statistics/user/${userId}`);
  }

  getGlobalStatistics(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/statistics/global`);
  }

  // ============= RECOMMENDATIONS =============

  getRecommendations(userId: number): Observable<CognitiveActivity[]> {
    return this.http.get<CognitiveActivity[]>(`${this.apiUrl}/recommendations/${userId}`);
  }
}
