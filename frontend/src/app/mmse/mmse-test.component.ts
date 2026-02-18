import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AdminService } from '../services/admin.service';

@Component({
  selector: 'app-mmse-test',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './mmse-test.component.html',
  styleUrl: './mmse-test.component.css'
})
export class MMSETestComponent implements OnInit {
  patientName: string = '';
  testDate: string = new Date().toISOString().split('T')[0];
  notes: string = '';

  // Orientation to Time (5 points)
  orientationTime = {
    year: false,
    season: false,
    month: false,
    date: false,
    day: false
  };

  // Orientation to Place (5 points)
  orientationPlace = {
    state: false,
    county: false,
    city: false,
    hospital: false,
    floor: false
  };

  // Registration (3 points)
  registration = {
    apple: false,
    penny: false,
    table: false
  };

  // Attention and Calculation (5 points)
  attentionCalculation = {
    serial7_1: false,
    serial7_2: false,
    serial7_3: false,
    serial7_4: false,
    serial7_5: false
  };

  // Recall (3 points)
  recall = {
    apple: false,
    penny: false,
    table: false
  };

  // Language (8 points)
  language = {
    naming_watch: false,
    naming_pencil: false,
    repetition: false,
    command_1: false,
    command_2: false,
    command_3: false,
    reading: false,
    writing: false
  };

  // Visual-Spatial (1 point)
  visualSpatial = {
    copy: false
  };

  currentSection: string = 'intro';
  loading: boolean = false;
  error: string | null = null;
  success: string | null = null;
  totalScore: number = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.currentSection = 'intro';
  }

  startTest(): void {
    if (!this.patientName.trim()) {
      this.error = 'Please enter patient name';
      return;
    }
    this.error = null;
    this.currentSection = 'orientation-time';
  }

  nextSection(nextSection: string): void {
    this.error = null;
    this.currentSection = nextSection;
    window.scrollTo(0, 0);
  }

  calculateScore(): number {
    let score = 0;

    // Count orientation to time (5)
    score += Object.values(this.orientationTime).filter(v => v).length;

    // Count orientation to place (5)
    score += Object.values(this.orientationPlace).filter(v => v).length;

    // Count registration (3)
    score += Object.values(this.registration).filter(v => v).length;

    // Count attention and calculation (5)
    score += Object.values(this.attentionCalculation).filter(v => v).length;

    // Count recall (3)
    score += Object.values(this.recall).filter(v => v).length;

    // Count language (8)
    score += Object.values(this.language).filter(v => v).length;

    // Count visual-spatial (1)
    score += Object.values(this.visualSpatial).filter(v => v).length;

    return score;
  }

  getInterpretation(score: number): string {
    if (score >= 24) return 'Normal cognition';
    if (score >= 18) return 'Mild cognitive impairment';
    if (score >= 12) return 'Moderate cognitive impairment';
    return 'Severe cognitive impairment';
  }

  submitTest(): void {
    this.totalScore = this.calculateScore();
    this.loading = true;
    this.error = null;

    const testData = {
      patient_name: this.patientName,
      orientation_score: Object.values(this.orientationTime).filter(v => v).length +
                        Object.values(this.orientationPlace).filter(v => v).length,
      registration_score: Object.values(this.registration).filter(v => v).length,
      attention_score: Object.values(this.attentionCalculation).filter(v => v).length,
      recall_score: Object.values(this.recall).filter(v => v).length,
      language_score: Object.values(this.language).filter(v => v).length,
      total_score: this.totalScore,
      interpretation: this.getInterpretation(this.totalScore),
      test_date: this.testDate,
      notes: this.notes
    };

    // Submit to backend
    this.adminService.submitMMSETest(testData).subscribe({
      next: (response: any) => {
        this.loading = false;
        this.success = `Test submitted successfully! Score: ${this.totalScore}/30`;
        this.currentSection = 'result';
        setTimeout(() => {
          this.resetTest();
        }, 5000);
      },
      error: (err: any) => {
        this.loading = false;
        this.error = 'Failed to submit test. ' + (err.error?.message || err.message);
      }
    });
  }

  resetTest(): void {
    this.patientName = '';
    this.testDate = new Date().toISOString().split('T')[0];
    this.notes = '';
    this.totalScore = 0;
    this.success = null;
    this.error = null;
    this.currentSection = 'intro';

    // Reset all sections
    Object.keys(this.orientationTime).forEach(key => this.orientationTime[key as keyof typeof this.orientationTime] = false);
    Object.keys(this.orientationPlace).forEach(key => this.orientationPlace[key as keyof typeof this.orientationPlace] = false);
    Object.keys(this.registration).forEach(key => this.registration[key as keyof typeof this.registration] = false);
    Object.keys(this.attentionCalculation).forEach(key => this.attentionCalculation[key as keyof typeof this.attentionCalculation] = false);
    Object.keys(this.recall).forEach(key => this.recall[key as keyof typeof this.recall] = false);
    Object.keys(this.language).forEach(key => this.language[key as keyof typeof this.language] = false);
    Object.keys(this.visualSpatial).forEach(key => this.visualSpatial[key as keyof typeof this.visualSpatial] = false);
  }

  goHome(): void {
    window.location.href = '/';
  }

  // Helper methods for template
  getOrientationScore(): number {
    return Object.values(this.orientationTime).filter(v => v).length +
           Object.values(this.orientationPlace).filter(v => v).length;
  }

  getRegistrationScore(): number {
    return Object.values(this.registration).filter(v => v).length;
  }

  getAttentionScore(): number {
    return Object.values(this.attentionCalculation).filter(v => v).length;
  }

  getRecallScore(): number {
    return Object.values(this.recall).filter(v => v).length;
  }

  getLanguageScore(): number {
    return Object.values(this.language).filter(v => v).length;
  }

  getVisualScore(): number {
    return Object.values(this.visualSpatial).filter(v => v).length;
  }
}
