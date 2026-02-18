import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-rendezvous-medecin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rendezvous-medecin.html',
  styleUrls: ['./rendezvous-medecin.css']
})
export class RendezvousMedecinComponent implements OnInit {

  private apiUrl = 'http://localhost:8081/api/rendezvous';

  medecinId: number = 1;

  allRendezvous: any[] = [];
  rendezvousList: any[] = [];       // list after filter/search/sort
  pagedRendezvous: any[] = [];
  baseRendezvous: any[] = [];        //  liste de base (après filtres)
  activeStatus: string = 'ALL';      // filtre status actuel
  onlyToday: boolean = false;        // filtre today actif
// list displayed in table

  successMsg = '';
  errorMsg = '';

  // 🔽/🔼 tri date
  sortAscending: boolean = true;

  // 🔎 Search
  searchTerm: string = '';

  // Pagination
  pageSize: number = 8;
  currentPage: number = 1;
  totalPages: number = 1;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadRendezVousForMedecin();
  }

  loadRendezVousForMedecin(): void {
    this.successMsg = '';
    this.errorMsg = '';

    this.http.get<any[]>(`${this.apiUrl}/medecin/${this.medecinId}`)
      .subscribe({
          next: (data) => {
            this.allRendezvous = [...data];

            this.sortAscending = true;

            // ✅ base par défaut = tous
            this.activeStatus = 'ALL';
            this.onlyToday = false;
            this.baseRendezvous = [...this.allRendezvous];

            this.applySearchSortAndPaginate();
          },
        error: () => {
          this.errorMsg = 'Erreur chargement rendez-vous';
        }
      });
  }

  //  Filter buttons
  filterStatus(status: string): void {
    this.activeStatus = status;
    this.onlyToday = false; // si tu cliques status, on enlève today
    this.rebuildBaseAndApply();
  }


  showToday(): void {
    this.onlyToday = true;
    this.activeStatus = 'ALL'; // today + all (ou garde status si tu veux)
    this.rebuildBaseAndApply();
  }
  private rebuildBaseAndApply(): void {
    let base = [...this.allRendezvous];

    // filtre status
    if (this.activeStatus !== 'ALL') {
      base = base.filter(r => r.status === this.activeStatus);
    }

    // filtre today
    if (this.onlyToday) {
      const today = new Date().toDateString();
      base = base.filter(r => new Date(r.dateHeure).toDateString() === today);
    }

    this.baseRendezvous = base;
    this.applySearchSortAndPaginate();
  }

  applySearchSortAndPaginate(): void {
    let list = [...this.baseRendezvous];

    // 🔎 search
    const term = this.searchTerm.trim().toLowerCase();
    if (term) {
      list = list.filter(r => {
        const date = new Date(r.dateHeure).toLocaleString().toLowerCase();
        const patient = (r.patient?.nomPatient || '').toLowerCase();
        const status = (r.status || '').toLowerCase();
        return date.includes(term) || patient.includes(term) || status.includes(term);
      });
    }

    // sort
    list.sort((a, b) => {
      const dateA = new Date(a.dateHeure).getTime();
      const dateB = new Date(b.dateHeure).getTime();
      return this.sortAscending ? (dateA - dateB) : (dateB - dateA);
    });

    this.rendezvousList = list;

    // pagination reset
    this.currentPage = 1;
    this.updatePagination();
  }


  // 🔎 Search + sort + pagination in one place
  applyFiltersAndSearch(): void {
    let list = [...this.rendezvousList];

    const term = this.searchTerm.trim().toLowerCase();
    if (term) {
      list = list.filter(r => {
        const date = new Date(r.dateHeure).toLocaleString().toLowerCase();
        const patient = (r.patient?.nomPatient || '').toLowerCase();
        const status = (r.status || '').toLowerCase();
        return date.includes(term) || patient.includes(term) || status.includes(term);
      });
    }

    // apply sort on list
    list.sort((a, b) => {
      const dateA = new Date(a.dateHeure).getTime();
      const dateB = new Date(b.dateHeure).getTime();
      return this.sortAscending ? (dateA - dateB) : (dateB - dateA);
    });

    // replace and paginate
    this.rendezvousList = list;
    this.currentPage = 1;
    this.updatePagination();
  }

  // sorting click
  sortByDate(): void {
    this.sortAscending = !this.sortAscending;
    this.applySearchSortAndPaginate();
  }


  // Pagination
  private updatePagination(): void {
    this.totalPages = Math.max(1, Math.ceil(this.rendezvousList.length / this.pageSize));

    if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
    if (this.currentPage < 1) this.currentPage = 1;

    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;

    this.pagedRendezvous = this.rendezvousList.slice(start, end);
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

  // RDV proche (moins d’1h)
  isSoon(dateHeure: string): boolean {
    const now = new Date().getTime();
    const rdvTime = new Date(dateHeure).getTime();
    const diff = rdvTime - now;
    return diff > 0 && diff < 3600000;
  }

  confirmerRendezVous(rdv: any): void {
    this.successMsg = '';
    this.errorMsg = '';

    this.http.put(`${this.apiUrl}/${rdv.id}`, { status: 'CONFIRME' })
      .subscribe({
        next: () => {
          this.successMsg = 'Rendez-vous confirmé ';
          this.loadRendezVousForMedecin();
        },
        error: () => {
          this.errorMsg = 'Erreur confirmation';
        }
      });
  }

  terminerRendezVous(rdv: any): void {
    this.successMsg = '';
    this.errorMsg = '';

    this.http.put(`${this.apiUrl}/${rdv.id}`, { status: 'TERMINE' })
      .subscribe({
        next: () => {
          this.successMsg = 'Rendez-vous terminé ';
          this.loadRendezVousForMedecin();
        },
        error: () => {
          this.errorMsg = 'Erreur terminaison';
        }
      });
  }
}
