import { Component, HostListener, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar">
      <div class="navbar-container">
        <a routerLink="/" class="navbar-logo">
          <span class="logo-icon">🧠</span>
          <span class="logo-text">COGNIVITA</span>
        </a>

        <button class="menu-toggle" (click)="toggleMenu()" [class.active]="menuOpen">
          <span></span><span></span><span></span>
        </button>

        <ul class="nav-menu" [class.active]="menuOpen">

          <!-- Cognitive Tests -->
          <li class="nav-item dropdown"
              (mouseenter)="openDropdown('cognitive')"
              (mouseleave)="scheduleClose('cognitive')">
            <a href="#" (click)="toggleDropdown($event, 'cognitive')"
               class="nav-link dropdown-toggle" [class.active]="activeDropdown === 'cognitive'">
              <span class="nav-icon">🧠</span>
              <span class="nav-text">Cognitive Tests</span>
              <svg class="dropdown-arrow" [class.rotated]="activeDropdown === 'cognitive'"
                   width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 4.5L6 7.5L9 4.5"/>
              </svg>
            </a>
            <ul class="dropdown-menu" [class.show]="activeDropdown === 'cognitive'"
                (mouseenter)="cancelClose('cognitive')"
                (mouseleave)="scheduleClose('cognitive')">
              <li>
                <a routerLink="/mmse" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">📋</span>
                  <div><div class="dropdown-title">MMSE Test</div><div class="dropdown-desc">Mini-Mental State Examination</div></div>
                </a>
              </li>
              <li>
                <a routerLink="/cnn" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">🤖</span>
                  <div><div class="dropdown-title">AI Scanner</div><div class="dropdown-desc">Brain Scan Analysis</div></div>
                </a>
              </li>
            </ul>
          </li>

          <!-- Medical Data — all pointing to /medical-records -->
          <li class="nav-item dropdown"
              (mouseenter)="openDropdown('medical')"
              (mouseleave)="scheduleClose('medical')">
            <a href="#" (click)="toggleDropdown($event, 'medical')"
               class="nav-link dropdown-toggle" [class.active]="activeDropdown === 'medical'">
              <span class="nav-icon">🏥</span>
              <span class="nav-text">Medical Data</span>
              <svg class="dropdown-arrow" [class.rotated]="activeDropdown === 'medical'"
                   width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 4.5L6 7.5L9 4.5"/>
              </svg>
            </a>
            <ul class="dropdown-menu" [class.show]="activeDropdown === 'medical'"
                (mouseenter)="cancelClose('medical')"
                (mouseleave)="scheduleClose('medical')">
              <li>
                <a routerLink="/medical-records" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">📁</span>
                  <div><div class="dropdown-title">Medical Records</div><div class="dropdown-desc">Patient Clinical Files</div></div>
                </a>
              </li>
              <li>
                <a routerLink="/medical-records" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">📊</span>
                  <div><div class="dropdown-title">Medical History</div><div class="dropdown-desc">View health records</div></div>
                </a>
              </li>
              <li>
                <a routerLink="/medical-records" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">⚠️</span>
                  <div><div class="dropdown-title">Risk Factors</div><div class="dropdown-desc">Track risk indicators</div></div>
                </a>
              </li>
              <li>
                <a routerLink="/health-prevention" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">🛡️</span>
                  <div><div class="dropdown-title">Health Prevention</div><div class="dropdown-desc">Wellness & prevention plans</div></div>
                </a>
              </li>
            </ul>
          </li>

          <!-- User Management -->
          <li class="nav-item dropdown"
              (mouseenter)="openDropdown('user')"
              (mouseleave)="scheduleClose('user')">
            <a href="#" (click)="toggleDropdown($event, 'user')"
               class="nav-link dropdown-toggle" [class.active]="activeDropdown === 'user'">
              <span class="nav-icon">👤</span>
              <span class="nav-text">User Management</span>
              <svg class="dropdown-arrow" [class.rotated]="activeDropdown === 'user'"
                   width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 4.5L6 7.5L9 4.5"/>
              </svg>
            </a>
            <ul class="dropdown-menu" [class.show]="activeDropdown === 'user'"
                (mouseenter)="cancelClose('user')"
                (mouseleave)="scheduleClose('user')">
              <li>
                <a routerLink="/register" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">📝</span>
                  <div><div class="dropdown-title">Registration</div><div class="dropdown-desc">Create new account</div></div>
                </a>
              </li>
              <li>
                <a routerLink="/login" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">🔐</span>
                  <div><div class="dropdown-title">Login</div><div class="dropdown-desc">Authentication portal</div></div>
                </a>
              </li>
              <li>
                <a routerLink="/admin" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">⚙️</span>
                  <div><div class="dropdown-title">Admin Dashboard</div><div class="dropdown-desc">System administration</div></div>
                </a>
              </li>
            </ul>
          </li>

          <!-- Support -->
          <li class="nav-item dropdown"
              (mouseenter)="openDropdown('support')"
              (mouseleave)="scheduleClose('support')">
            <a href="#" (click)="toggleDropdown($event, 'support')"
               class="nav-link dropdown-toggle" [class.active]="activeDropdown === 'support'">
              <span class="nav-icon">💬</span>
              <span class="nav-text">Support</span>
              <svg class="dropdown-arrow" [class.rotated]="activeDropdown === 'support'"
                   width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 4.5L6 7.5L9 4.5"/>
              </svg>
            </a>
            <ul class="dropdown-menu" [class.show]="activeDropdown === 'support'"
                (mouseenter)="cancelClose('support')"
                (mouseleave)="scheduleClose('support')">
              <li>
                <a routerLink="/feedback" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">📨</span>
                  <div><div class="dropdown-title">Feedback</div><div class="dropdown-desc">Share your experience</div></div>
                </a>
              </li>
              <li>
                <a routerLink="/support" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">🎧</span>
                  <div><div class="dropdown-title">Help Center</div><div class="dropdown-desc">Get assistance</div></div>
                </a>
              </li>
            </ul>
          </li>

          <!-- Community -->
          <li class="nav-item dropdown"
              (mouseenter)="openDropdown('community')"
              (mouseleave)="scheduleClose('community')">
            <a href="#" (click)="toggleDropdown($event, 'community')"
               class="nav-link dropdown-toggle" [class.active]="activeDropdown === 'community'">
              <span class="nav-icon">👥</span>
              <span class="nav-text">Community</span>
              <svg class="dropdown-arrow" [class.rotated]="activeDropdown === 'community'"
                   width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 4.5L6 7.5L9 4.5"/>
              </svg>
            </a>
            <ul class="dropdown-menu" [class.show]="activeDropdown === 'community'"
                (mouseenter)="cancelClose('community')"
                (mouseleave)="scheduleClose('community')">
              <li>
                <a routerLink="/forum" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">💭</span>
                  <div><div class="dropdown-title">Forum</div><div class="dropdown-desc">Discussions & questions</div></div>
                </a>
              </li>
              <li>
                <a routerLink="/community" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">🤝</span>
                  <div><div class="dropdown-title">Community Groups</div><div class="dropdown-desc">Connect with others</div></div>
                </a>
              </li>
            </ul>
          </li>

          <!-- Wellness -->
          <li class="nav-item dropdown"
              (mouseenter)="openDropdown('wellness')"
              (mouseleave)="scheduleClose('wellness')">
            <a href="#" (click)="toggleDropdown($event, 'wellness')"
               class="nav-link dropdown-toggle" [class.active]="activeDropdown === 'wellness'">
              <span class="nav-icon">🧘</span>
              <span class="nav-text">Wellness</span>
              <svg class="dropdown-arrow" [class.rotated]="activeDropdown === 'wellness'"
                   width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 4.5L6 7.5L9 4.5"/>
              </svg>
            </a>
            <ul class="dropdown-menu" [class.show]="activeDropdown === 'wellness'"
                (mouseenter)="cancelClose('wellness')"
                (mouseleave)="scheduleClose('wellness')">
              <li>
                <a routerLink="/exercises" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">🎯</span>
                  <div><div class="dropdown-title">Cognitive Exercises</div><div class="dropdown-desc">Brain training games</div></div>
                </a>
              </li>
              <li>
                <a routerLink="/mood-journal" routerLinkActive="active" (click)="closeAll()" class="dropdown-item">
                  <span class="dropdown-icon">📔</span>
                  <div><div class="dropdown-title">Mood Journal</div><div class="dropdown-desc">Track your emotions</div></div>
                </a>
              </li>
            </ul>
          </li>

        </ul>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      position: fixed; top: 0; width: 100%;
      background: rgba(10, 10, 26, 0.85);
      backdrop-filter: blur(20px);
      box-shadow: 0 0 30px rgba(0,255,255,0.1), 0 2px 15px rgba(0,0,0,0.3);
      border-bottom: 1px solid rgba(0,255,255,0.2);
      z-index: 1000; padding: 0;
    }
    .navbar-container {
      max-width: 1400px; margin: 0 auto; padding: 0 20px;
      display: flex; justify-content: space-between; align-items: center; height: 70px;
    }
    .navbar-logo {
      display: flex; align-items: center; gap: 10px;
      font-weight: 800; font-size: 1.4rem;
      font-family: 'Orbitron','Inter',sans-serif;
      background: linear-gradient(135deg,#00ffff 0%,#ff00ff 50%,#00ccff 100%);
      background-size: 200% 200%;
      -webkit-background-clip: text; -webkit-text-fill-color: transparent;
      background-clip: text; cursor: pointer;
      transition: transform 0.3s ease;
      animation: textShimmer 3s ease-in-out infinite;
      text-decoration: none; flex-shrink: 0;
    }
    @keyframes textShimmer {
      0%,100% { background-position: 0% 50%; }
      50% { background-position: 100% 50%; }
    }
    .navbar-logo:hover { transform: scale(1.05); }
    .logo-icon { font-size: 1.6rem; }

    .nav-menu {
      display: flex; list-style: none; gap: 2px; margin: 0; padding: 0;
    }

    .nav-item { position: relative; }

    .nav-link {
      display: flex; align-items: center; gap: 6px;
      padding: 10px 14px; color: #b0e0ff; text-decoration: none;
      font-weight: 600; font-family: 'Inter',sans-serif;
      border-radius: 10px; transition: all 0.3s ease;
      font-size: 0.85rem; white-space: nowrap;
    }
    .nav-icon { font-size: 1.1rem; filter: drop-shadow(0 0 4px rgba(0,255,255,0.5)); }
    .dropdown-arrow { margin-left: 4px; transition: transform 0.3s ease; }
    .dropdown-arrow.rotated { transform: rotate(180deg); }

    .nav-link:hover {
      background: rgba(0,255,255,0.1); color: #00ffff;
      transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,255,255,0.2);
    }
    .nav-link.active {
      background: linear-gradient(135deg,rgba(0,255,255,0.2) 0%,rgba(255,0,255,0.2) 100%);
      color: #fff;
      box-shadow: 0 0 20px rgba(0,255,255,0.3), 0 5px 15px rgba(0,0,0,0.2);
      border: 1px solid rgba(0,255,255,0.3);
    }

    /* ─── DROPDOWN FIX: flush to nav-link, bridge element fills any visual gap ─── */
    .dropdown { position: relative; }

    .dropdown-menu {
      position: absolute;
      top: 100%;   /* flush — no gap causes premature mouseleave */
      left: 0;
      min-width: 280px;
      background: rgba(10,10,26,0.97);
      backdrop-filter: blur(30px);
      border: 1px solid rgba(0,255,255,0.3);
      border-top: none;
      border-radius: 0 0 16px 16px;
      box-shadow: 0 0 40px rgba(0,255,255,0.2), 0 10px 40px rgba(0,0,0,0.4);
      list-style: none; padding: 8px; margin: 0;
      opacity: 0; visibility: hidden;
      transform: translateY(-4px);
      transition: opacity 0.2s ease, transform 0.2s ease, visibility 0.2s;
      z-index: 1001;
    }
    /* invisible bridge so mouse can glide from link to menu without gap triggering mouseleave */
    .dropdown-menu::before {
      content: ''; position: absolute;
      top: -12px; left: 0; right: 0; height: 12px;
      background: transparent;
    }
    .dropdown-menu.show { opacity: 1; visibility: visible; transform: translateY(0); }

    .dropdown-item {
      display: flex; align-items: center; gap: 12px;
      padding: 14px 16px; color: #b0e0ff; text-decoration: none;
      border-radius: 12px; transition: all 0.25s ease;
      font-family: 'Inter',sans-serif;
    }
    .dropdown-item:hover {
      background: rgba(0,255,255,0.15); color: #fff; transform: translateX(4px);
    }
    .dropdown-item.active {
      background: linear-gradient(135deg,rgba(0,255,255,0.2) 0%,rgba(255,0,255,0.2) 100%);
      color: #fff; border: 1px solid rgba(0,255,255,0.4);
    }
    .dropdown-icon { font-size: 1.5rem; filter: drop-shadow(0 0 4px rgba(0,255,255,0.6)); flex-shrink: 0; }
    .dropdown-title { font-weight: 700; font-size: 0.95rem; margin-bottom: 2px; }
    .dropdown-desc { font-size: 0.75rem; opacity: 0.7; color: #93c5fd; }

    .menu-toggle {
      display: none; flex-direction: column;
      background: none; border: none; cursor: pointer; gap: 6px;
    }
    .menu-toggle span {
      width: 25px; height: 3px; background: #00ffff;
      border-radius: 2px; transition: all 0.3s ease;
      box-shadow: 0 0 4px rgba(0,255,255,0.5);
    }
    .menu-toggle.active span:nth-child(1) { transform: rotate(45deg) translate(8px,8px); }
    .menu-toggle.active span:nth-child(2) { opacity: 0; }
    .menu-toggle.active span:nth-child(3) { transform: rotate(-45deg) translate(7px,-7px); }

    @media (max-width: 1200px) {
      .nav-link { padding: 8px 10px; font-size: 0.8rem; gap: 4px; }
    }
    @media (max-width: 1024px) {
      .menu-toggle { display: flex; }
      .nav-menu {
        position: absolute; top: 70px; left: 0; right: 0;
        flex-direction: column; background: rgba(10,10,26,0.98);
        backdrop-filter: blur(20px);
        border-bottom: 1px solid rgba(0,255,255,0.2);
        max-height: 0; overflow: hidden;
        transition: max-height 0.3s ease;
      }
      .nav-menu.active { max-height: 90vh; overflow-y: auto; }
      .nav-item { width: 100%; }
      .nav-link { width: 100%; padding: 15px 20px; border-radius: 0; font-size: 0.9rem; }
      .nav-link.active { border-left: 4px solid #00ffff; padding-left: 16px; background: rgba(0,255,255,0.1); }
      .dropdown-menu {
        position: static; min-width: 100%; margin-top: 0;
        border: none; border-top: 1px solid rgba(0,255,255,0.2);
        border-radius: 0; box-shadow: none;
      }
      .dropdown-menu::before { display: none; }
      .dropdown-item { padding: 12px 20px; }
    }
    @media (max-width: 768px) {
      .navbar-container { padding: 0 15px; }
    }
  `]
})
export class NavigationComponent implements OnDestroy {
  menuOpen = false;
  activeDropdown: string | null = null;

  // Delayed-close timers prevent dropdown from vanishing during mouse travel
  private closeTimers: Map<string, ReturnType<typeof setTimeout>> = new Map();
  private readonly CLOSE_DELAY = 150;

  toggleMenu(): void { this.menuOpen = !this.menuOpen; }

  closeAll(): void {
    this.menuOpen = false;
    this.activeDropdown = null;
    this.closeTimers.forEach(t => clearTimeout(t));
    this.closeTimers.clear();
  }

  toggleDropdown(event: Event, dropdown: string): void {
    event.preventDefault();
    event.stopPropagation();
    this.cancelClose(dropdown);
    this.activeDropdown = this.activeDropdown === dropdown ? null : dropdown;
  }

  openDropdown(dropdown: string): void {
    this.cancelClose(dropdown);
    this.activeDropdown = dropdown;
  }

  scheduleClose(dropdown: string): void {
    const timer = setTimeout(() => {
      if (this.activeDropdown === dropdown) {
        this.activeDropdown = null;
      }
    }, this.CLOSE_DELAY);
    this.closeTimers.set(dropdown, timer);
  }

  cancelClose(dropdown: string): void {
    const timer = this.closeTimers.get(dropdown);
    if (timer) { clearTimeout(timer); this.closeTimers.delete(dropdown); }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.dropdown')) { this.activeDropdown = null; }
  }

  ngOnDestroy(): void {
    this.closeTimers.forEach(t => clearTimeout(t));
    this.closeTimers.clear();
  }
}
