import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface ChartData {
  labels: string[];
  scores?: number[];
  counts?: number[];
  total?: number;
  dataPoints?: any[];
}

export interface AdherenceStats {
  totalActions: number;
  completedActions: number;
  pendingActions: number;
  cancelledActions: number;
  adherenceRate: number;
}

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private apiUrl = 'http://localhost:8081/api/statistics';

  constructor(private http: HttpClient) {}

  getRiskEvolution(medicalRecordId: number): Observable<ApiResponse<ChartData>> {
    return this.http.get<ApiResponse<ChartData>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/risk-evolution`
    );
  }

  getActionsPerMonth(medicalRecordId: number): Observable<ApiResponse<ChartData>> {
    return this.http.get<ApiResponse<ChartData>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/actions-per-month`
    );
  }

  getRiskFactorsDistribution(medicalRecordId: number): Observable<ApiResponse<ChartData>> {
    return this.http.get<ApiResponse<ChartData>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/risk-factors-distribution`
    );
  }

  getAdherenceStats(medicalRecordId: number): Observable<ApiResponse<AdherenceStats>> {
    return this.http.get<ApiResponse<AdherenceStats>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/adherence`
    );
  }

  getUserOverview(userId: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(
      `${this.apiUrl}/user/${userId}/overview`
    );
  }
}
