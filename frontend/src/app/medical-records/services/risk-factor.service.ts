import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RiskFactor } from '../models/risk-factor.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class RiskFactorService {
  private apiUrl = 'http://localhost:9090/api/risk-factors';

  constructor(private http: HttpClient) {}

  // Get all risk factors for a medical record (paginated)
  getRiskFactorsByMedicalRecord(
    medicalRecordId: number,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'createdAt',
    sortDirection: string = 'DESC'
  ): Observable<ApiResponse<PageResponse<RiskFactor>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    return this.http.get<ApiResponse<PageResponse<RiskFactor>>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}`,
      { params }
    );
  }

  // Get only active risk factors
  getActiveRiskFactors(medicalRecordId: number): Observable<ApiResponse<RiskFactor[]>> {
    return this.http.get<ApiResponse<RiskFactor[]>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/active`
    );
  }

  // Get single risk factor by ID
  getRiskFactorById(id: number): Observable<ApiResponse<RiskFactor>> {
    return this.http.get<ApiResponse<RiskFactor>>(`${this.apiUrl}/${id}`);
  }

  // Create new risk factor
  createRiskFactor(riskFactor: Partial<RiskFactor>): Observable<ApiResponse<RiskFactor>> {
    return this.http.post<ApiResponse<RiskFactor>>(this.apiUrl, riskFactor);
  }

  // Update risk factor
  updateRiskFactor(id: number, riskFactor: Partial<RiskFactor>): Observable<ApiResponse<RiskFactor>> {
    return this.http.put<ApiResponse<RiskFactor>>(`${this.apiUrl}/${id}`, riskFactor);
  }

  // Delete (deactivate) risk factor
  deleteRiskFactor(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  // Get statistics
  getRiskFactorStats(medicalRecordId: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/stats`
    );
  }
}
