import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

type MedecinOption = { id: number; nomMedecin: string };

@Component({
  selector: 'app-admin-rendezvous',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rendezvous-admin.html',
  styleUrls: ['./rendezvous-admin.css']
})
export class AdminRendezvousComponent implements OnInit {

  private rdvApiUrl = 'http://localhost:8081/api/rendezvous';
  private medecinApiUrl = 'http://localhost:8081/api/medecins';

  allRendezvous: any[] = [];
  rendezvousList: any[] = [];

  // Pagination
  pageSize: number = 8;        // nombre de lignes par page
  currentPage: number = 1;
  totalPages: number = 1;
  pagedRendezvous: any[] = [];

  // Search
  searchTerm: string = '';



  medecinOptions: MedecinOption[] = [];

  // Filters
  selectedStatus: string = 'ALL';
  selectedMedecinId: number | 'ALL' = 'ALL';
  onlyToday: boolean = false;

  // Sorting
  sortAscending: boolean = true;

  // UI
  successMsg = '';
  errorMsg = '';
  loading = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadMedecinOptions();
    this.loadAllRendezVous();
  }

  private updatePagination(): void {
    this.totalPages = Math.max(1, Math.ceil(this.rendezvousList.length / this.pageSize));

    // sécurité si page dépasse après filtre/suppression
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }
    if (this.currentPage < 1) {
      this.currentPage = 1;
    }

    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;

    this.pagedRendezvous = this.rendezvousList.slice(start, end);
  }

  goToPage(page: number): void {
    this.currentPage = page;
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

  onPageSizeChange(): void {
    this.currentPage = 1;
    this.updatePagination();
  }

  loadMedecinOptions(): void {
    this.http.get<MedecinOption[]>(`${this.medecinApiUrl}/options`).subscribe({
      next: (data) => {
        this.medecinOptions = [...data];
      },
      error: () => {
        // pas bloquant
        this.medecinOptions = [];
      }
    });
  }

  loadAllRendezVous(): void {
    this.successMsg = '';
    this.errorMsg = '';
    this.loading = true;

    this.http.get<any[]>(this.rdvApiUrl).subscribe({
      next: (data) => {
        this.loading = false;
        this.allRendezvous = [...data];
        this.applyFilters();
      },
      error: () => {
        this.loading = false;
        this.errorMsg = 'Erreur chargement rendez-vous';
      }
    });
  }

  // apply filters + keep sorting
  applyFilters(): void {
    let list = [...this.allRendezvous];

    // filter status
    if (this.selectedStatus !== 'ALL') {
      list = list.filter(r => r.status === this.selectedStatus);
    }

    // filter medecin
    if (this.selectedMedecinId !== 'ALL') {
      list = list.filter(r => r.medecin?.id === this.selectedMedecinId);
    }

    // filter today
    if (this.onlyToday) {
      const today = new Date().toDateString();
      list = list.filter(r => new Date(r.dateHeure).toDateString() === today);
    }

    // 🔎 GLOBAL SEARCH
    if (this.searchTerm.trim() !== '') {
      const term = this.searchTerm.toLowerCase();

      list = list.filter(r => {
        const date = new Date(r.dateHeure).toLocaleString().toLowerCase();
        const medecin = this.getMedecinName(r.medecin?.id)?.toLowerCase() || '';
        const patient = r.patient?.nomPatient?.toLowerCase() || '';
        const status = r.status?.toLowerCase() || '';

        return (
          date.includes(term) ||
          medecin.includes(term) ||
          patient.includes(term) ||
          status.includes(term)
        );
      });
    }

    this.rendezvousList = list;
    this.applySort();
    this.currentPage = 1;
    this.updatePagination();
  }


  //  sort by date (click)
  sortByDate(): void {
    this.sortAscending = !this.sortAscending;
    this.applySort();
  }

  private applySort(): void {
    this.rendezvousList.sort((a, b) => {
      const dateA = new Date(a.dateHeure).getTime();
      const dateB = new Date(b.dateHeure).getTime();
      return this.sortAscending ? (dateA - dateB) : (dateB - dateA);
    });

    this.updatePagination();
  }


  // helper doctor name
  getMedecinName(medecinId?: number): string {
    if (!medecinId) return '';
    const found = this.medecinOptions.find(m => m.id === medecinId);
    return found ? found.nomMedecin : '';
  }

  // ✅ admin: cancel RDV
  annulerRendezVous(rdv: any): void {
    this.successMsg = '';
    this.errorMsg = '';

    const ok = confirm('Annuler ce rendez-vous ?');
    if (!ok) return;

    this.http.put(`${this.rdvApiUrl}/${rdv.id}`, { status: 'ANNULE' }).subscribe({
      next: () => {
        this.successMsg = 'Rendez-vous annulé ✅';
        this.loadAllRendezVous();
      },
      error: () => {
        this.errorMsg = 'Erreur lors de l’annulation';
      }
    });
  }

  // ✅ admin: delete RDV
  deleteRendezVous(rdv: any): void {
    this.successMsg = '';
    this.errorMsg = '';

    const ok = confirm('Supprimer définitivement ce rendez-vous ?');
    if (!ok) return;

    this.http.delete(`${this.rdvApiUrl}/${rdv.id}`).subscribe({
      next: () => {
        this.successMsg = 'Rendez-vous supprimé s';
        this.loadAllRendezVous();
      },
      error: () => {
        this.errorMsg = 'Erreur lors de la suppression';
      }
    });
  }

  // small reset filters
  resetFilters(): void {
    this.selectedStatus = 'ALL';
    this.selectedMedecinId = 'ALL';
    this.onlyToday = false;
    this.applyFilters();
  }
}
