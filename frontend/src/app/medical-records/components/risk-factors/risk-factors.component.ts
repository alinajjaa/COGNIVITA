import { Component, Input, OnInit, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RiskFactorService } from '../../services/risk-factor.service';
import { RiskFactor, RISK_FACTOR_TYPES } from '../../models/risk-factor.model';

@Component({
  selector: 'app-risk-factors',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './risk-factors.component.html',
  styleUrls: ['./risk-factors.component.css']
})
export class RiskFactorsComponent implements OnInit, OnChanges {
  @Input() medicalRecordId!: number;

  riskFactors: RiskFactor[] = [];
  loading = false;
  error: string | null = null;
  showForm = false;
  isEditing = false;

  formData: Partial<RiskFactor> = {
    factorType: '', severity: 'MEDIUM', notes: '', isActive: true, diagnosedDate: ''
  };

  currentPage = 0; pageSize = 10; totalPages = 0; totalElements = 0;
  riskFactorTypes = RISK_FACTOR_TYPES;
  severityLevels = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  todayDate = new Date().toISOString().split('T')[0];

  stats = { totalCount: 0, activeCount: 0, inactiveCount: 0, criticalCount: 0, highCount: 0 };

  constructor(private riskFactorService: RiskFactorService) {}

  ngOnInit(): void { this.load(); }
  ngOnChanges(): void { if (this.medicalRecordId) this.load(); }

  load(): void {
    this.loadRiskFactors();
    this.loadStats();
  }

  loadRiskFactors(): void {
    if (!this.medicalRecordId) return;
    this.loading = true;
    this.riskFactorService.getRiskFactorsByMedicalRecord(
      this.medicalRecordId, this.currentPage, this.pageSize
    ).subscribe({
      next: (r) => {
        this.riskFactors = r.data.content || [];
        this.totalPages = r.data.totalPages || 0;
        this.totalElements = r.data.totalElements || 0;
        this.loading = false;
      },
      error: () => { this.loading = false; this.error = 'Failed to load risk factors'; }
    });
  }

  loadStats(): void {
    if (!this.medicalRecordId) return;
    this.riskFactorService.getRiskFactorStats(this.medicalRecordId).subscribe({
      next: (r) => { this.stats = r.data; },
      error: () => {}
    });
  }

  openCreateForm(): void {
    this.isEditing = false; this.error = null;
    this.formData = { medicalRecordId: this.medicalRecordId, factorType: '', severity: 'MEDIUM', notes: '', isActive: true, diagnosedDate: '' };
    this.showForm = true;
  }

  openEditForm(riskFactor: RiskFactor): void {
    this.isEditing = true; this.error = null;
    // Convert diagnosedDate to date-input format if it exists
    let diagDate = '';
    if (riskFactor.diagnosedDate) {
      diagDate = riskFactor.diagnosedDate.split('T')[0];
    }
    this.formData = { ...riskFactor, diagnosedDate: diagDate };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false; this.error = null;
    this.formData = { factorType: '', severity: 'MEDIUM', notes: '', isActive: true, diagnosedDate: '' };
  }

  submitForm(): void {
    if (!this.formData.factorType) {
      this.error = 'Please select a risk factor type'; return;
    }
    this.error = null;
    this.formData.medicalRecordId = this.medicalRecordId;

    // Convert date to ISO datetime if set
    const payload = { ...this.formData };
    if (payload.diagnosedDate && payload.diagnosedDate.trim() !== '') {
      payload.diagnosedDate = payload.diagnosedDate + 'T00:00:00';
    } else {
      delete payload.diagnosedDate;
    }

    const req = this.isEditing && this.formData.id
      ? this.riskFactorService.updateRiskFactor(this.formData.id, payload)
      : this.riskFactorService.createRiskFactor(payload);

    req.subscribe({
      next: () => { this.load(); this.closeForm(); },
      error: (err) => {
        console.error('Risk factor error:', err);
        this.error = 'Failed to ' + (this.isEditing ? 'update' : 'create') + ' risk factor: ' + (err.error?.error || err.message || 'Unknown error');
      }
    });
  }

  deleteRiskFactor(id: number): void {
    if (!confirm('Remove this risk factor?')) return;
    this.riskFactorService.deleteRiskFactor(id).subscribe({
      next: () => this.load(),
      error: () => { this.error = 'Failed to delete risk factor'; }
    });
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadRiskFactors();
  }

  getSeverityIcon(severity: string): string {
    const map: Record<string, string> = { LOW: '🟢', MEDIUM: '🟡', HIGH: '🟠', CRITICAL: '🔴' };
    return map[severity] || '⚪';
  }

  getSeverityClass(severity: string): string {
    const map: Record<string, string> = { LOW: 'badge-low', MEDIUM: 'badge-medium', HIGH: 'badge-high', CRITICAL: 'badge-critical' };
    return map[severity] || '';
  }

  formatDate(date: string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
