import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TimelineEvent } from '../models/timeline-event.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class TimelineService {
  private apiUrl = 'http://localhost:9090/api/timeline';

  constructor(private http: HttpClient) {}

  // Get all timeline events for a medical record
  getTimelineEvents(medicalRecordId: number): Observable<ApiResponse<TimelineEvent[]>> {
    return this.http.get<ApiResponse<TimelineEvent[]>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}`
    );
  }

  // Get timeline events filtered by date range
  getTimelineEventsByDateRange(
    medicalRecordId: number,
    startDate: string,
    endDate: string
  ): Observable<ApiResponse<TimelineEvent[]>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<ApiResponse<TimelineEvent[]>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/filter`,
      { params }
    );
  }

  // Get timeline events by event type
  getTimelineEventsByType(
    medicalRecordId: number,
    eventType: string
  ): Observable<ApiResponse<TimelineEvent[]>> {
    return this.http.get<ApiResponse<TimelineEvent[]>>(
      `${this.apiUrl}/medical-record/${medicalRecordId}/type/${eventType}`
    );
  }

  // Get timeline events for a user (all records)
  getTimelineEventsByUser(userId: number): Observable<ApiResponse<TimelineEvent[]>> {
    return this.http.get<ApiResponse<TimelineEvent[]>>(
      `${this.apiUrl}/user/${userId}`
    );
  }
}
