import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { MMSETestComponent } from './mmse/mmse-test.component';
import { CNNPredictionComponent } from './cnn/cnn-prediction.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { MedicalRecordsComponent } from './medical-records/medical-records.component';
import { HealthPreventionComponent } from './health-prevention/health-prevention.component';

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
    path: 'health-prevention',
    component: HealthPreventionComponent
  }
];
