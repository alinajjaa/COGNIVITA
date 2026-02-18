import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PreventionAction } from '../models/prevention-action.model';

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
export class PreventionActionService {
  private apiUrl = 'http://localhost:8081/api/prevention-actions';

  constructor(private http: HttpClient) {}

  // Get all prevention actions for a medical record (paginated)
  getPreventionActionsByMedicalRecord(
    medicalRecordId: number,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'actionDate',
    sortDirection: string = 'DESC'
  ): Observable<ApiResponse<PageResponse<PreventionAction>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    return this.http.get<ApiResponse<PageResponse<PreventionAction>>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}`,
      { params }
    );
  }

  // Get actions by status
  getActionsByStatus(
    medicalRecordId: number,
    status: string
  ): Observable<ApiResponse<PreventionAction[]>> {
    return this.http.get<ApiResponse<PreventionAction[]>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/status/${status}`
    );
  }

  // Get single action by ID
  getPreventionActionById(id: number): Observable<ApiResponse<PreventionAction>> {
    return this.http.get<ApiResponse<PreventionAction>>(`${this.apiUrl}/${id}`);
  }

  // Create new prevention action
  createPreventionAction(action: Partial<PreventionAction>): Observable<ApiResponse<PreventionAction>> {
    return this.http.post<ApiResponse<PreventionAction>>(this.apiUrl, action);
  }

  // Update prevention action
  updatePreventionAction(
    id: number,
    action: Partial<PreventionAction>
  ): Observable<ApiResponse<PreventionAction>> {
    return this.http.put<ApiResponse<PreventionAction>>(`${this.apiUrl}/${id}`, action);
  }

  // Delete prevention action
  deletePreventionAction(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  // Mark action as completed
  completeAction(id: number): Observable<ApiResponse<PreventionAction>> {
    return this.http.patch<ApiResponse<PreventionAction>>(
      `${this.apiUrl}/${id}/complete`,
      {}
    );
  }

  // Get statistics
  getPreventionActionStats(medicalRecordId: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/stats`
    );
  }

  // Filter actions by date range
  filterActionsByDateRange(
    medicalRecordId: number,
    startDate: string,
    endDate: string
  ): Observable<ApiResponse<PreventionAction[]>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<ApiResponse<PreventionAction[]>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/filter`,
      { params }
    );
  }
}
