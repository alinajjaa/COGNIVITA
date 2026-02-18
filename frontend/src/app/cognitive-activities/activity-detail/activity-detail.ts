// src/app/cognitive-activities/activity-detail/activity-detail.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { CognitiveActivityService, CognitiveActivity } from '../../services/cognitive-activity.service';

@Component({
  selector: 'app-activity-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './activity-detail.html',
  styleUrls: ['./activity-detail.css']
})
export class ActivityDetail implements OnInit {
  activity: CognitiveActivity | null = null;
  loading = true;
  error = '';

  // Pour afficher le contenu JSON de façon lisible
  formattedContent: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private activityService: CognitiveActivityService
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadActivity(id);
  }

  loadActivity(id: number) {
    this.loading = true;
    this.activityService.getActivityById(id).subscribe({
      next: (data) => {
        this.activity = data;
        this.loading = false;
        this.formatContent();
      },
      error: (err) => {
        this.error = 'Failed to load activity details';
        this.loading = false;
        console.error(err);
      }
    });
  }

  formatContent() {
    if (this.activity?.content) {
      try {
        const contentObj = JSON.parse(this.activity.content);
        this.formattedContent = JSON.stringify(contentObj, null, 2);
      } catch (e) {
        this.formattedContent = this.activity.content;
      }
    }
  }

  getTypeIcon(type: string): string {
    const icons: Record<string, string> = {
      'MEMORY': '🧠',
      'ATTENTION': '👀',
      'LOGIC': '🔢'
    };
    return icons[type] || '📝';
  }

  getDifficultyColor(difficulty: string): string {
    const colors: Record<string, string> = {
      'EASY': '#48bb78',
      'MEDIUM': '#ed8936',
      'HARD': '#f56565'
    };
    return colors[difficulty] || '#718096';
  }

  getDifficultyText(difficulty: string): string {
    const texts: Record<string, string> = {
      'EASY': 'Facile',
      'MEDIUM': 'Moyen',
      'HARD': 'Difficile'
    };
    return texts[difficulty] || difficulty;
  }

  getTypeText(type: string): string {
    const texts: Record<string, string> = {
      'MEMORY': 'Mémoire',
      'ATTENTION': 'Attention',
      'LOGIC': 'Logique'
    };
    return texts[type] || type;
  }

  formatDate(date: Date | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  playActivity() {
    if (this.activity?.id) {
      this.router.navigate(['/activities', this.activity.id, 'play']);
    }
  }

  editActivity() {
    if (this.activity?.id) {
      this.router.navigate(['/activities/edit', this.activity.id]);
    }
  }

  goBack() {
    this.router.navigate(['/activities']);
  }
}
