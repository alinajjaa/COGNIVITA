import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { MMSETestComponent } from './mmse/mmse-test.component';
import { CNNPredictionComponent } from './cnn/cnn-prediction.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { MedicalRecordsComponent } from './medical-records/medical-records.component';

// ✅ IMPORTS CORRIGÉS - Les noms des classes sont SANS "Component"
import { CognitiveActivities } from './cognitive-activities/cognitive-activities';
import { ActivityList } from './cognitive-activities/activity-list/activity-list';
import { ActivityForm } from './cognitive-activities/activity-form/activity-form';
import { ActivityDetail } from './cognitive-activities/activity-detail/activity-detail';
import { ActivityPlay } from './cognitive-activities/activity-play/activity-play';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'mmse',
    component: MMSETestComponent
  },
  {
    path: 'cnn',
    component: CNNPredictionComponent
  },
  {
    path: 'admin',
    component: AdminDashboardComponent
  },
  {
    path: 'medical-records',
    component: MedicalRecordsComponent
  },
  {
    path: 'activities',
    component: CognitiveActivities,  // ✅ Plus de "Component"
    children: [
      { path: '', component: ActivityList },
      { path: 'new', component: ActivityForm },
      { path: 'edit/:id', component: ActivityForm },
      { path: ':id', component: ActivityDetail },
      { path: ':id/play', component: ActivityPlay }
    ]
  }
];
