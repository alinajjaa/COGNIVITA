import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Brain3dComponent } from '../brain3d/brain3d.component';
import { RiskFactorsComponent } from './components/risk-factors/risk-factors.component';
import { MedicalChartsComponent } from './components/charts/medical-charts.component';

interface MedicalRecord {
  id: number; userId: number; userName: string; userEmail: string;
  age: number; gender: string; educationLevel: string; familyHistory: string;
  riskFactors: string; currentSymptoms: string; diagnosisNotes: string;
  riskScore: number; riskLevel: string; lastRiskCalculation: string;
  recommendations: string[]; createdAt: string; updatedAt: string;
}
interface UserGroup { userId: number; userName: string; userEmail: string; records: MedicalRecord[]; expanded: boolean; }
interface PageResponse<T> { content: T[]; totalElements: number; totalPages: number; size: number; number: number; }
interface ApiResponse<T> { success: boolean; message: string; data: T; }
interface TimelineEvent { id: number; eventDate: string; eventType: string; description: string; }
interface PreventionAction { id: number; actionType: string; description: string; actionDate: string; status: string; frequency: string; }

@Component({
  selector: 'app-medical-records',
  standalone: true,
  imports: [CommonModule, FormsModule, Brain3dComponent, RiskFactorsComponent, MedicalChartsComponent],
  templateUrl: './medical-records.component.html',
  styleUrls: ['./medical-records.component.css']
})
export class MedicalRecordsComponent implements OnInit {
  // State
  allRecords: MedicalRecord[] = [];
  userGroups: UserGroup[] = [];
  selectedRecord: MedicalRecord | null = null;
  loading = true; error: string | null = null;
  showForm = false; isEditing = false;
  activeTab: 'overview' | 'risk' | 'prevention' | 'timeline' | 'stats' = 'overview';
  viewMode: 'grouped' | 'list' = 'grouped';

  // Pagination
  currentPage = 0; pageSize = 9; totalPages = 0; totalElements = 0;

  // Filters
  filterRiskLevel = ''; filterGender = ''; filterFamilyHistory = '';
  sortBy = 'createdAt'; sortDirection = 'DESC';
  searchName = '';

  // Patient data
  patientTimeline: TimelineEvent[] = [];
  patientActions: PreventionAction[] = [];
  patientActionPage = 0; patientActionTotalPages = 0;
  timelineFilter = ''; showActionForm = false;
  actionFormData = { actionType: '', description: '', actionDate: '', frequency: '', status: 'PENDING' };

  // Form
  formData = {
    userId: null as number | null, age: null as number | null, gender: '',
    educationLevel: '', familyHistory: 'No', riskFactors: '',
    currentSymptoms: '', diagnosisNotes: ''
  };

  private apiUrl = 'http://localhost:9090/api/medical-records';
  private timelineApiUrl = 'http://localhost:9090/api/timeline';
  private preventionApiUrl = 'http://localhost:9090/api/prevention-actions';

  constructor(private http: HttpClient) {}
  ngOnInit(): void { this.loadRecords(); }

  loadRecords(): void {
    this.loading = true;
    let params = new HttpParams()
      .set('page', this.currentPage.toString())
      .set('size', this.pageSize.toString())
      .set('sortBy', this.sortBy)
      .set('sortDirection', this.sortDirection);
    if (this.filterRiskLevel) params = params.set('riskLevel', this.filterRiskLevel);
    if (this.filterGender) params = params.set('gender', this.filterGender);
    if (this.filterFamilyHistory) params = params.set('familyHistory', this.filterFamilyHistory);

    this.http.get<ApiResponse<PageResponse<MedicalRecord>>>(this.apiUrl, { params }).subscribe({
      next: (response) => {
        const page = response.data;
        this.allRecords = page.content || [];
        this.totalPages = page.totalPages || 0;
        this.totalElements = page.totalElements || 0;
        this.buildUserGroups();
        this.loading = false;
      },
      error: () => { this.error = 'Failed to load medical records'; this.loading = false; }
    });
  }

  buildUserGroups(): void {
    const map = new Map<number, UserGroup>();
    for (const record of this.allRecords) {
      if (!map.has(record.userId)) {
        map.set(record.userId, {
          userId: record.userId,
          userName: record.userName,
          userEmail: record.userEmail,
          records: [],
          expanded: true
        });
      }
      map.get(record.userId)!.records.push(record);
    }
    this.userGroups = Array.from(map.values());
  }

  get filteredGroups(): UserGroup[] {
    if (!this.searchName) return this.userGroups;
    const q = this.searchName.toLowerCase();
    return this.userGroups
      .map(g => ({ ...g, records: g.records.filter(r => r.userName.toLowerCase().includes(q) || r.userEmail.toLowerCase().includes(q)) }))
      .filter(g => g.records.length > 0);
  }

  toggleGroup(group: UserGroup): void { group.expanded = !group.expanded; }

  // Filters & Pagination
  applyFilters(): void { this.currentPage = 0; this.loadRecords(); }
  clearFilters(): void { this.filterRiskLevel = ''; this.filterGender = ''; this.filterFamilyHistory = ''; this.sortBy = 'createdAt'; this.sortDirection = 'DESC'; this.searchName = ''; this.applyFilters(); }
  changePage(page: number): void { if (page >= 0 && page < this.totalPages) { this.currentPage = page; this.loadRecords(); } }
  get pageNumbers(): number[] { const max = 7; const half = Math.floor(max / 2); let start = Math.max(0, this.currentPage - half); let end = Math.min(this.totalPages, start + max); start = Math.max(0, end - max); return Array.from({ length: end - start }, (_, i) => start + i); }

  // View record
  viewRecord(record: MedicalRecord): void {
    this.selectedRecord = record;
    this.activeTab = 'overview';
    this.loadPatientTimeline(record.id);
    this.loadPatientActions(record.id);
    this.http.get<ApiResponse<MedicalRecord>>(`${this.apiUrl}/${record.id}`).subscribe({
      next: (r) => { this.selectedRecord = r.data; }
    });
  }

  loadPatientTimeline(recordId: number): void {
    let url = `${this.timelineApiUrl}/medical-record/${recordId}`;
    if (this.timelineFilter) url += `/type/${this.timelineFilter}`;
    this.http.get<ApiResponse<TimelineEvent[]>>(url).subscribe({
      next: (r) => { this.patientTimeline = r.data || []; },
      error: () => { this.patientTimeline = []; }
    });
  }

  loadPatientActions(recordId: number, page = 0): void {
    const params = new HttpParams().set('page', page.toString()).set('size', '5').set('sortBy', 'actionDate').set('sortDirection', 'DESC');
    this.http.get<ApiResponse<PageResponse<PreventionAction>>>(`${this.preventionApiUrl}/medical-record/${recordId}`, { params }).subscribe({
      next: (r) => { this.patientActions = r.data.content || []; this.patientActionTotalPages = r.data.totalPages || 0; this.patientActionPage = page; },
      error: () => { this.patientActions = []; }
    });
  }

  setTab(tab: 'overview' | 'risk' | 'prevention' | 'timeline' | 'stats'): void {
    this.activeTab = tab;
    if (!this.selectedRecord) return;
    if (tab === 'timeline') this.loadPatientTimeline(this.selectedRecord.id);
    if (tab === 'prevention') this.loadPatientActions(this.selectedRecord.id);
  }

  filterTimeline(type: string): void { this.timelineFilter = type; if (this.selectedRecord) this.loadPatientTimeline(this.selectedRecord.id); }

  // Prevention Actions
  openActionForm(): void {
    this.actionFormData = { actionType: '', description: '', actionDate: new Date().toISOString().split('T')[0] + 'T00:00', frequency: '', status: 'PENDING' };
    this.showActionForm = true;
  }
  closeActionForm(): void { this.showActionForm = false; }
  submitAction(): void {
    if (!this.selectedRecord || !this.actionFormData.actionType) return;
    this.http.post<ApiResponse<PreventionAction>>(this.preventionApiUrl, { ...this.actionFormData, medicalRecordId: this.selectedRecord.id }).subscribe({
      next: () => { this.loadPatientActions(this.selectedRecord!.id); this.closeActionForm(); },
      error: () => { this.error = 'Failed to create action'; }
    });
  }
  completeAction(actionId: number): void {
    this.http.patch<ApiResponse<PreventionAction>>(`${this.preventionApiUrl}/${actionId}/complete`, {}).subscribe({
      next: () => { if (this.selectedRecord) this.loadPatientActions(this.selectedRecord.id); }
    });
  }
  deleteAction(actionId: number): void {
    if (!confirm('Delete this prevention action?')) return;
    this.http.delete<ApiResponse<void>>(`${this.preventionApiUrl}/${actionId}`).subscribe({
      next: () => { if (this.selectedRecord) this.loadPatientActions(this.selectedRecord.id); }
    });
  }

  // Record Form
  closeDetails(): void { this.selectedRecord = null; this.patientTimeline = []; this.patientActions = []; }
  openCreateForm(): void {
    this.isEditing = false; this.error = null;
    this.formData = { userId: null, age: null, gender: '', educationLevel: '', familyHistory: 'No', riskFactors: '', currentSymptoms: '', diagnosisNotes: '' };
    this.showForm = true;
  }
  openCreateFormForUser(userId: number, userName: string): void {
    this.isEditing = false; this.error = null;
    this.formData = { userId, age: null, gender: '', educationLevel: '', familyHistory: 'No', riskFactors: '', currentSymptoms: '', diagnosisNotes: '' };
    this.showForm = true;
  }
  openEditForm(record: MedicalRecord): void {
    this.isEditing = true; this.error = null;
    this.formData = { userId: record.userId, age: record.age, gender: record.gender, educationLevel: record.educationLevel, familyHistory: record.familyHistory, riskFactors: record.riskFactors, currentSymptoms: record.currentSymptoms, diagnosisNotes: record.diagnosisNotes };
    this.selectedRecord = record; this.showForm = true;
  }
  closeForm(): void { this.showForm = false; }
  submitForm(): void {
    if (!this.formData.userId || !this.formData.age || !this.formData.gender) {
      this.error = 'Please fill in all required fields (User ID, Age, Gender)'; return;
    }
    const req = this.isEditing && this.selectedRecord
      ? this.http.put<ApiResponse<MedicalRecord>>(`${this.apiUrl}/${this.selectedRecord.id}`, this.formData)
      : this.http.post<ApiResponse<MedicalRecord>>(this.apiUrl, this.formData);
    req.subscribe({
      next: (response) => { this.loadRecords(); this.closeForm(); if (response.data) this.viewRecord(response.data); },
      error: (err) => { this.error = 'Failed to save: ' + (err.error?.error || err.message); }
    });
  }
  deleteRecord(id: number): void {
    if (!confirm('Delete this medical record?')) return;
    this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`).subscribe({
      next: () => { this.loadRecords(); this.closeDetails(); },
      error: () => { this.error = 'Failed to delete record'; }
    });
  }

  // Helpers
  getRiskClass(level: string): string {
    const m: Record<string, string> = { CRITICAL: 'risk-critical', HIGH: 'risk-high', MEDIUM: 'risk-medium', LOW: 'risk-low', Yes: 'risk-high', No: 'risk-low' };
    return m[level] || 'risk-low';
  }
  getRiskEmoji(level: string): string {
    const m: Record<string, string> = { CRITICAL: '🔴', HIGH: '🟠', MEDIUM: '🟡', LOW: '🟢' };
    return m[level] || '⚪';
  }
  getEventIcon(type: string): string {
    const m: Record<string, string> = { RISK_FACTOR_ADDED: '⚠️', RISK_FACTOR_UPDATED: '✏️', RISK_FACTOR_REMOVED: '🗑️', PREVENTION_ACTION_ADDED: '✅', PREVENTION_ACTION_UPDATED: '🔄', MEDICAL_RECORD_UPDATED: '📋' };
    return m[type] || '📌';
  }
  getStatusClass(s: string): string {
    const m: Record<string, string> = { COMPLETED: 'status-completed', PENDING: 'status-pending', CANCELLED: 'status-cancelled' };
    return m[s] || 'status-pending';
  }
  getHighestRiskInGroup(group: UserGroup): string {
    const order = ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW'];
    for (const level of order) { if (group.records.some(r => r.riskLevel === level)) return level; }
    return 'LOW';
  }
  getAvgScoreInGroup(group: UserGroup): number {
    if (!group.records.length) return 0;
    return Math.round(group.records.reduce((a, r) => a + (r.riskScore || 0), 0) / group.records.length);
  }
  formatDate(d: string): string { if (!d) return 'N/A'; return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' }); }
  formatDateTime(d: string): string { if (!d) return 'N/A'; return new Date(d).toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }); }
  getInitials(name: string): string { if (!name) return '?'; return name.split(' ').map(p => p[0]).join('').substring(0, 2).toUpperCase(); }
  getHighRiskCount(): number { return this.allRecords.filter(r => r.riskLevel === 'HIGH' || r.riskLevel === 'CRITICAL').length; }
  getAverageScore(): number { if (!this.allRecords.length) return 0; return Math.round(this.allRecords.reduce((a, r) => a + (r.riskScore || 0), 0) / this.allRecords.length); }
  getRiskScoreWidth(score: number): string { return Math.min(100, score || 0) + '%'; }
  getCompletedCount(): number { return this.patientActions.filter(a => a.status === 'COMPLETED').length; }
}
