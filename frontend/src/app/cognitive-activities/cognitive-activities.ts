// src/app/cognitive-activities/cognitive-activities.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-cognitive-activities',
  standalone: true,  // ← IMPORTANT !
  imports: [CommonModule, RouterModule],  // ← AJOUTE CES IMPORTS
  templateUrl: './cognitive-activities.html',
  styleUrls: ['./cognitive-activities.css']
})
export class CognitiveActivities {
  // Pas de logique pour l'instant
}
