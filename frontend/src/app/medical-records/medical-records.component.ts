import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Brain3dComponent } from '../brain3d/brain3d.component';

interface MedicalRecord {
  id: number;
  userId: number;
  userName: string;
  userEmail: string;
  age: number;
  gender: string;
  educationLevel: string;
  familyHistory: string;
  riskFactors: string;
  currentSymptoms: string;
  diagnosisNotes: string;
  createdAt: string;
  updatedAt: string;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Component({
  selector: 'app-medical-records',
  standalone: true,
  imports: [CommonModule, FormsModule, Brain3dComponent],
  templateUrl: './medical-records.component.html',
  styleUrls: ['./medical-records.component.css']
})
export class MedicalRecordsComponent implements OnInit {
  records: MedicalRecord[] = [];
  selectedRecord: MedicalRecord | null = null;
  loading = true;
  error: string | null = null;
  showForm = false;
  isEditing = false;

  formData = {
    userId: null as number | null,
    age: null as number | null,
    gender: '',
    educationLevel: '',
    familyHistory: 'No',
    riskFactors: '',
    currentSymptoms: '',
    diagnosisNotes: ''
  };

  private apiUrl = 'http://localhost:8080/api/medical-records';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadRecords();
  }

  loadRecords(): void {
    this.loading = true;
    this.http.get<ApiResponse<MedicalRecord[]>>(this.apiUrl).subscribe({
      next: (response) => {
        this.records = response.data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading records', err);
        this.error = 'Failed to load medical records';
        this.loading = false;
      }
    });
  }

  viewRecord(record: MedicalRecord): void {
    this.selectedRecord = record;
  }

  closeDetails(): void {
    this.selectedRecord = null;
  }

  openCreateForm(): void {
    this.isEditing = false;
    this.error = null;
    this.formData = {
      userId: null,
      age: null,
      gender: '',
      educationLevel: '',
      familyHistory: 'No',
      riskFactors: '',
      currentSymptoms: '',
      diagnosisNotes: ''
    };
    this.showForm = true;
  }

  openEditForm(record: MedicalRecord): void {
    this.isEditing = true;
    this.error = null;
    this.formData = {
      userId: record.userId,
      age: record.age,
      gender: record.gender,
      educationLevel: record.educationLevel,
      familyHistory: record.familyHistory,
      riskFactors: record.riskFactors,
      currentSymptoms: record.currentSymptoms,
      diagnosisNotes: record.diagnosisNotes
    };
    this.selectedRecord = record;
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.selectedRecord = null;
  }

  submitForm(): void {
    console.log('submitForm called', this.formData);
    
    // Validate required fields
    if (!this.formData.userId || !this.formData.age || !this.formData.gender) {
      console.error('Missing required fields');
      this.error = 'Please fill in all required fields (User ID, Age, Gender)';
      return;
    }
    
    if (this.isEditing && this.selectedRecord) {
      console.log('Updating record', this.selectedRecord.id);
      this.http.put<ApiResponse<MedicalRecord>>(`${this.apiUrl}/${this.selectedRecord.id}`, this.formData).subscribe({
        next: (response) => {
          console.log('Update success', response);
          this.loadRecords();
          this.closeForm();
        },
        error: (err) => {
          console.error('Error updating record', err);
          this.error = 'Failed to update record: ' + (err.error?.message || err.message);
        }
      });
    } else {
      console.log('Creating new record');
      this.http.post<ApiResponse<MedicalRecord>>(this.apiUrl, this.formData).subscribe({
        next: (response) => {
          console.log('Create success', response);
          this.loadRecords();
          this.closeForm();
        },
        error: (err) => {
          console.error('Error creating record', err);
          this.error = 'Failed to create record: ' + (err.error?.message || err.message);
        }
      });
    }
  }

  deleteRecord(id: number): void {
    if (confirm('Are you sure you want to delete this medical record?')) {
      this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`).subscribe({
        next: () => {
          this.loadRecords();
          this.closeDetails();
        },
        error: (err) => {
          console.error('Error deleting record', err);
          this.error = 'Failed to delete record';
        }
      });
    }
  }

  getRiskClass(familyHistory: string): string {
    return familyHistory === 'Yes' ? 'high-risk' : 'low-risk';
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getInitials(name: string): string {
    if (!name) return '?';
    return name.split(' ')
      .map(part => part.charAt(0))
      .join('')
      .substring(0, 2)
      .toUpperCase();
  }

  getHighRiskCount(): number {
    return this.records.filter(r => r.familyHistory === 'Yes').length;
  }

  getAverageAge(): number {
    if (this.records.length === 0) return 0;
    const sum = this.records.reduce((acc, r) => acc + (r.age || 0), 0);
    return Math.round(sum / this.records.length);
  }
}
