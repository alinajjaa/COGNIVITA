import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  users: any[] = [];
  stats: any = {
    usersCount: 0,
    mmseTestsCount: 0,
    activeUsersCount: 0
  };
  loading = true;
  error: string | null = null;
  today = new Date();

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadDashboard();
    this.loadStats();
  }

  loadDashboard(): void {
    this.loading = true;
    this.error = null;
    this.adminService.getDashboard().subscribe({
      next: (data: any) => {
        this.users = data || [];
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading dashboard', err);
        this.error = 'Failed to load dashboard data';
        this.loading = false;
      }
    });
  }

  loadStats(): void {
    this.adminService.getStats().subscribe({
      next: (stats: any) => {
        this.stats = stats || {
          usersCount: 0,
          mmseTestsCount: 0,
          activeUsersCount: 0
        };
      },
      error: (err: any) => {
        console.error('Error loading stats', err);
      }
    });
  }

  deleteUser(userId: number): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.adminService.deleteUser(userId).subscribe({
        next: () => {
          this.loadDashboard();
          this.loadStats();
        },
        error: (err: any) => {
          console.error('Error deleting user', err);
          this.error = 'Failed to delete user';
        }
      });
    }
  }

  getMMSEInterpretation(score: number): string {
    if (score >= 24) return 'Normal';
    if (score >= 18) return 'Mild Impairment';
    if (score >= 10) return 'Moderate Impairment';
    return 'Severe Impairment';
  }

  getInterpretationClass(score: number): string {
    if (score >= 24) return 'normal';
    if (score >= 18) return 'mild';
    if (score >= 10) return 'moderate';
    return 'severe';
  }
}
