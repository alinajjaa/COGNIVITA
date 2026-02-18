import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { MMSETestComponent } from './mmse/mmse-test.component';
import { CNNPredictionComponent } from './cnn/cnn-prediction.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { MedicalRecordsComponent } from './medical-records/medical-records.component';
import {Rendezvous} from './rendezvous/rendezvous';
import {RendezvousMedecinComponent} from './rendezvous-medecin/rendezvous-medecin';
import {AdminRendezvousComponent} from './rendezvous-admin/rendezvous-admin';


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
    path: 'rendezvous',
    component: Rendezvous
  },
  {
    path: 'rendezvousMedecin',
    component: RendezvousMedecinComponent
  },{
    path: 'rendezvousAdmin',
    component: AdminRendezvousComponent
  }
];
