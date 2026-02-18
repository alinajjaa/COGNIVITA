import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

type RendezVousStatus = 'PLANIFIE' | 'CONFIRME' | 'ANNULE' | 'TERMINE';

type MedecinOption = {
  id: number;
  nomMedecin: string;
};

type ToastType = 'success' | 'error' | 'info' | 'warn';

type Toast = {
  id: number;
  type: ToastType;
  title: string;
  message: string;
  durationMs: number;
  closing?: boolean;
};

@Component({
  selector: 'app-rendezvous',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rendezvous.html',
  styleUrls: ['./rendezvous.css']
})
export class Rendezvous implements OnInit {

  private rdvApiUrl = 'http://localhost:8081/api/rendezvous';
  private medecinApiUrl = 'http://localhost:8081/api/medecins';

  // for now fixed patient id = 1
  patientId: number = 1;

  medecinId: number | null = null;
  dateHeure = '';
  status: RendezVousStatus = 'PLANIFIE';

  successMsg = '';
  errorMsg = '';
  minDateTime: string = '';
  loading = false;

  rendezvousList: any[] = [];
  medecinOptions: MedecinOption[] = [];

  searchTerm: string = '';
  filteredRendezvous: any[] = [];

  pageSize: number = 8;
  currentPage: number = 1;
  totalPages: number = 1;
  pagedRendezvous: any[] = [];

  sortAscending: boolean = true;

  // ✅ Toasts
  toasts: Toast[] = [];
  private toastId = 1;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.setMinDateTime();
    this.loadMedecinOptions();
    this.loadRendezVousForPatient();
  }

  // =========================
  // ✅ TOAST HELPERS
  // =========================
  notify(type: ToastType, title: string, message: string, durationMs: number = 3500): void {
    const id = this.toastId++;
    const toast: Toast = { id, type, title, message, durationMs, closing: false };
    this.toasts = [toast, ...this.toasts].slice(0, 4); // keep max 4 visible

    if (durationMs > 0) {
      setTimeout(() => this.dismissToast(id), durationMs);
    }
  }

  dismissToast(id: number): void {
    const t = this.toasts.find(x => x.id === id);
    if (!t) return;

    t.closing = true; // trigger animation
    setTimeout(() => {
      this.toasts = this.toasts.filter(x => x.id !== id);
    }, 180);
  }

  // =========================
  // =========================
  setMinDateTime(): void {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    this.minDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  loadMedecinOptions(): void {
    this.http.get<MedecinOption[]>(`${this.medecinApiUrl}/options`).subscribe({
      next: (data) => {
        this.medecinOptions = [...data];
        this.applyFilters();
      },
      error: () => {
        this.errorMsg = 'Erreur chargement médecins (options)';
        this.notify('error', 'Erreur', 'Impossible de charger la liste des médecins.');
      }
    });
  }

  loadRendezVousForPatient(): void {
    this.successMsg = '';
    this.errorMsg = '';

    this.http.get<any[]>(`${this.rdvApiUrl}/patient/${this.patientId}`).subscribe({
      next: (data) => {
        this.rendezvousList = [...data];
        this.applyFilters();
      },
      error: () => {
        this.errorMsg = 'Erreur chargement rendez-vous du patient';
        this.notify('error', 'Erreur', 'Impossible de charger vos rendez-vous.');
      }
    });
  }

  applyFilters(): void {
    let list = [...this.rendezvousList];

    const term = this.searchTerm.trim().toLowerCase();
    if (term) {
      list = list.filter(r => {
        const date = new Date(r.dateHeure).toLocaleString().toLowerCase();
        const medecin = this.getMedecinName(r.medecin?.id).toLowerCase();
        const status = (r.status || '').toLowerCase();
        return date.includes(term) || medecin.includes(term) || status.includes(term);
      });
    }

    this.filteredRendezvous = list;
    this.applySort();
    this.currentPage = 1;
    this.updatePagination();
  }

  sortByDate(): void {
    this.sortAscending = !this.sortAscending;
    this.applySort();
    this.updatePagination();
  }

  private applySort(): void {
    this.filteredRendezvous.sort((a, b) => {
      const dateA = new Date(a.dateHeure).getTime();
      const dateB = new Date(b.dateHeure).getTime();
      return this.sortAscending ? (dateA - dateB) : (dateB - dateA);
    });
  }

  private updatePagination(): void {
    this.totalPages = Math.max(1, Math.ceil(this.filteredRendezvous.length / this.pageSize));

    if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
    if (this.currentPage < 1) this.currentPage = 1;

    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;

    this.pagedRendezvous = this.filteredRendezvous.slice(start, end);
  }

  onPageSizeChange(): void {
    this.currentPage = 1;
    this.updatePagination();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  reserver(): void {
    this.successMsg = '';
    this.errorMsg = '';

    if (!this.medecinId || !this.dateHeure) {
      this.errorMsg = 'Veuillez choisir un médecin et remplir Date/Heure.';
      this.notify('warn', 'Champs requis', 'Choisissez un médecin et une date/heure.');
      return;
    }

    const selectedDate = new Date(this.dateHeure);
    const hour = selectedDate.getHours();

    if (hour < 8 || hour >= 17) {
      this.errorMsg = 'Les rendez-vous sont disponibles uniquement entre 8h et 17h.';
      this.notify('warn', 'Horaire invalide', 'Disponibles uniquement entre 8h et 17h.');
      return;
    }

    const payload = {
      dateHeure: this.dateHeure,
      status: 'PLANIFIE',
      medecin: { id: this.medecinId },
      patient: { id: this.patientId }
    };

    this.loading = true;

    this.http.post<any>(this.rdvApiUrl, payload).subscribe({
      next: () => {
        this.loading = false;

        const docName = this.getMedecinName(this.medecinId ?? undefined) || `#${this.medecinId}`;
        this.notify('success', 'Rendez-vous réservé', `Avec ${docName} — ${new Date(this.dateHeure).toLocaleString()}`);

        this.loadRendezVousForPatient();
        this.dateHeure = '';
        this.medecinId = null;
      },
      error: () => {
        this.loading = false;
        this.errorMsg = 'Erreur lors de la réservation.';
        this.notify('error', 'Échec réservation', 'Veuillez réessayer.');
      }
    });
  }

  annulerRendezVous(id: number): void {
    this.successMsg = '';
    this.errorMsg = '';

    const ok = confirm('Voulez-vous annuler ce rendez-vous ?');
    if (!ok) return;

    const payload = { status: 'ANNULE' };

    this.http.put(`${this.rdvApiUrl}/${id}`, payload).subscribe({
      next: () => {
        this.notify('success', 'Rendez-vous annulé', 'Le rendez-vous a été annulé avec succès.');
        this.loadRendezVousForPatient();
      },
      error: () => {
        this.errorMsg = 'Erreur lors de l’annulation.';
        this.notify('error', 'Échec annulation', 'Impossible d’annuler ce rendez-vous.');
      }
    });
  }

  getMedecinName(medecinId?: number): string {
    if (!medecinId) return '';
    const found = this.medecinOptions.find(m => m.id === medecinId);
    return found ? found.nomMedecin : '';
  }
}
