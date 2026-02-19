import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Chart, registerables } from 'chart.js';

// Register Chart.js components
Chart.register(...registerables);

interface ChartData {
  labels: string[];
  scores?: number[];
  counts?: number[];
}

@Component({
  selector: 'app-medical-charts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="charts-container">
      <!-- Risk Score Evolution Line Chart -->
      <div class="chart-card">
        <h4>📈 Risk Score Evolution</h4>
        <canvas #riskChart id="riskChart-{{medicalRecordId}}"></canvas>
      </div>

      <!-- Actions Per Month Bar Chart -->
      <div class="chart-card">
        <h4>📊 Prevention Actions Per Month</h4>
        <canvas #actionsChart id="actionsChart-{{medicalRecordId}}"></canvas>
      </div>

      <!-- Risk Factors Distribution Pie Chart -->
      <div class="chart-card">
        <h4>🎯 Risk Factors by Severity</h4>
        <canvas #distributionChart id="distributionChart-{{medicalRecordId}}"></canvas>
      </div>
    </div>
  `,
  styles: [`
    .charts-container {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      gap: 20px;
      margin-top: 20px;
    }
    .chart-card {
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 12px;
      padding: 20px;
    }
    .chart-card h4 {
      margin: 0 0 15px 0;
      color: #fff;
      font-size: 0.95rem;
      font-weight: 600;
    }
    canvas {
      max-height: 300px;
    }
  `]
})
export class MedicalChartsComponent implements OnChanges {
  @Input() medicalRecordId!: number;

  private riskChart?: Chart;
  private actionsChart?: Chart;
  private distributionChart?: Chart;

  constructor(private http: HttpClient) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['medicalRecordId'] && this.medicalRecordId) {
      setTimeout(() => this.loadAllCharts(), 100);
    }
  }

  ngOnDestroy(): void {
    this.destroyCharts();
  }

  private destroyCharts(): void {
    if (this.riskChart) this.riskChart.destroy();
    if (this.actionsChart) this.actionsChart.destroy();
    if (this.distributionChart) this.distributionChart.destroy();
  }

  private loadAllCharts(): void {
    this.loadRiskEvolution();
    this.loadActionsPerMonth();
    this.loadRiskDistribution();
  }

  private loadRiskEvolution(): void {
    this.http.get<any>(`http://localhost:9090/api/statistics/medical-record/${this.medicalRecordId}/risk-evolution`)
      .subscribe({
        next: (response) => {
          const data: ChartData = response.data;
          const canvas = document.getElementById(`riskChart-${this.medicalRecordId}`) as HTMLCanvasElement;
          if (!canvas) return;

          if (this.riskChart) this.riskChart.destroy();

          this.riskChart = new Chart(canvas, {
            type: 'line',
            data: {
              labels: data.labels || [],
              datasets: [{
                label: 'Risk Score',
                data: data.scores || [],
                borderColor: '#06b6d4',
                backgroundColor: 'rgba(6, 182, 212, 0.1)',
                tension: 0.4,
                fill: true
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: { display: false },
                tooltip: {
                  backgroundColor: 'rgba(0, 0, 0, 0.8)',
                  titleColor: '#fff',
                  bodyColor: '#fff'
                }
              },
              scales: {
                y: { 
                  beginAtZero: true, 
                  max: 100,
                  ticks: { color: '#94a3b8' },
                  grid: { color: 'rgba(255, 255, 255, 0.05)' }
                },
                x: { 
                  ticks: { color: '#94a3b8' },
                  grid: { color: 'rgba(255, 255, 255, 0.05)' }
                }
              }
            }
          });
        },
        error: (err) => console.error('Error loading risk evolution:', err)
      });
  }

  private loadActionsPerMonth(): void {
    this.http.get<any>(`http://localhost:9090/api/statistics/medical-record/${this.medicalRecordId}/actions-per-month`)
      .subscribe({
        next: (response) => {
          const data: ChartData = response.data;
          const canvas = document.getElementById(`actionsChart-${this.medicalRecordId}`) as HTMLCanvasElement;
          if (!canvas) return;

          if (this.actionsChart) this.actionsChart.destroy();

          this.actionsChart = new Chart(canvas, {
            type: 'bar',
            data: {
              labels: data.labels || [],
              datasets: [{
                label: 'Actions',
                data: data.counts || [],
                backgroundColor: '#22c55e',
                borderColor: '#16a34a',
                borderWidth: 1
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: { display: false },
                tooltip: {
                  backgroundColor: 'rgba(0, 0, 0, 0.8)',
                  titleColor: '#fff',
                  bodyColor: '#fff'
                }
              },
              scales: {
                y: { 
                  beginAtZero: true,
                  ticks: { color: '#94a3b8', stepSize: 1 },
                  grid: { color: 'rgba(255, 255, 255, 0.05)' }
                },
                x: { 
                  ticks: { color: '#94a3b8' },
                  grid: { display: false }
                }
              }
            }
          });
        },
        error: (err) => console.error('Error loading actions per month:', err)
      });
  }

  private loadRiskDistribution(): void {
    this.http.get<any>(`http://localhost:9090/api/statistics/medical-record/${this.medicalRecordId}/risk-factors-distribution`)
      .subscribe({
        next: (response) => {
          const data: ChartData = response.data;
          const canvas = document.getElementById(`distributionChart-${this.medicalRecordId}`) as HTMLCanvasElement;
          if (!canvas) return;

          if (this.distributionChart) this.distributionChart.destroy();

          this.distributionChart = new Chart(canvas, {
            type: 'doughnut',
            data: {
              labels: data.labels || [],
              datasets: [{
                data: data.counts || [],
                backgroundColor: ['#ef4444', '#f97316', '#eab308', '#22c55e'],
                borderWidth: 2,
                borderColor: '#1e293b'
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: { 
                  position: 'bottom',
                  labels: { color: '#94a3b8', padding: 10 }
                },
                tooltip: {
                  backgroundColor: 'rgba(0, 0, 0, 0.8)',
                  titleColor: '#fff',
                  bodyColor: '#fff'
                }
              }
            }
          });
        },
        error: (err) => console.error('Error loading risk distribution:', err)
      });
  }
}
