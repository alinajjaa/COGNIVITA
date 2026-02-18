// src/app/cognitive-activities/activity-form/activity-form.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CognitiveActivityService, CognitiveActivity } from '../../services/cognitive-activity.service';

@Component({
  selector: 'app-activity-form',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './activity-form.html',
  styleUrls: ['./activity-form.css']
})
export class ActivityForm implements OnInit {
  // Mode édition ou création
  isEditMode = false;
  activityId: number | null = null;

  // Données du formulaire
  activity: CognitiveActivity = {
    title: '',
    description: '',
    type: 'MEMORY',
    difficulty: 'EASY',
    content: '',
    timeLimit: 120,
    maxScore: 10,
    instructions: '',
    imageUrl: '',
    isActive: true
  };

  // États
  loading = false;
  submitting = false;
  error = '';
  success = '';

  // Options pour les sélecteurs
  types = [
    { value: 'MEMORY', label: '🧠 Mémoire', icon: '🧠' },
    { value: 'ATTENTION', label: '👀 Attention', icon: '👀' },
    { value: 'LOGIC', label: '🔢 Logique', icon: '🔢' }
  ];

  difficulties = [
    { value: 'EASY', label: '🟢 Facile', color: '#48bb78' },
    { value: 'MEDIUM', label: '🟡 Moyen', color: '#ed8936' },
    { value: 'HARD', label: '🔴 Difficile', color: '#f56565' }
  ];

  // Templates d'exemples pour aider l'utilisateur
  contentTemplates = {
    MEMORY: JSON.stringify({
      words: ['mot1', 'mot2', 'mot3', 'mot4', 'mot5']
    }, null, 2),
    ATTENTION: JSON.stringify({
      items: [
        { word: 'ROUGE', color: 'blue', correct: 'blue' },
        { word: 'VERT', color: 'red', correct: 'red' }
      ]
    }, null, 2),
    LOGIC: JSON.stringify({
      sequences: [[2, 4, 6, 8], [5, 10, 15, 20]]
    }, null, 2)
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private activityService: CognitiveActivityService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.activityId = Number(id);
      this.loadActivity(this.activityId);
    }
  }

  loadActivity(id: number) {
    this.loading = true;
    this.activityService.getActivityById(id).subscribe({
      next: (data) => {
        this.activity = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de l\'activité';
        this.loading = false;
        console.error(err);
      }
    });
  }

  onSubmit() {
    if (!this.validateForm()) {
      return;
    }

    this.submitting = true;
    this.error = '';
    this.success = '';

    if (this.isEditMode && this.activityId) {
      // Mise à jour
      this.activityService.updateActivity(this.activityId, this.activity).subscribe({
        next: (updated) => {
          this.success = 'Activité mise à jour avec succès !';
          this.submitting = false;
          setTimeout(() => this.router.navigate(['/activities', updated.id]), 1500);
        },
        error: (err) => {
          this.error = 'Erreur lors de la mise à jour';
          this.submitting = false;
          console.error(err);
        }
      });
    } else {
      // Création
      this.activityService.createActivity(this.activity).subscribe({
        next: (created) => {
          this.success = 'Activité créée avec succès !';
          this.submitting = false;
          setTimeout(() => this.router.navigate(['/activities', created.id]), 1500);
        },
        error: (err) => {
          this.error = 'Erreur lors de la création';
          this.submitting = false;
          console.error(err);
        }
      });
    }
  }

  validateForm(): boolean {
    if (!this.activity.title?.trim()) {
      this.error = 'Le titre est requis';
      return false;
    }
    if (!this.activity.type) {
      this.error = 'Le type est requis';
      return false;
    }
    if (!this.activity.difficulty) {
      this.error = 'La difficulté est requise';
      return false;
    }
    return true;
  }

  loadTemplate() {
    this.activity.content = this.contentTemplates[this.activity.type];
  }

  formatContent() {
    try {
      const contentObj = JSON.parse(this.activity.content || '{}');
      this.activity.content = JSON.stringify(contentObj, null, 2);
    } catch (e) {
      // Si ce n'est pas du JSON valide, on laisse tel quel
    }
  }

  cancel() {
    if (this.isEditMode && this.activityId) {
      this.router.navigate(['/activities', this.activityId]);
    } else {
      this.router.navigate(['/activities']);
    }
  }

  getTypeIcon(type: string): string {
    const found = this.types.find(t => t.value === type);
    return found?.icon || '📝';
  }
}
