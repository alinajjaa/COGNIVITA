# System Architecture Diagram

## Application Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│                     AlzDetect Application                       │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│              ┌─ Fixed Navigation Bar (z: 1000) ─┐              │
│              │   📋 MMSE | 🤖 CNN | ⚙️ Admin   │              │
│              └──────────────────────────────────┘              │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                                                          │  │
│  │           3D Brain Background (z: -1)                   │  │
│  │          (Fixed, behind all content)                    │  │
│  │                                                          │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                                                          │  │
│  │              Content Overlay (z: 1, 2)                  │  │
│  │              (Responsive Content Area)                  │  │
│  │                                                          │  │
│  │  ┌─────────────┬──────────────┬──────────────┐         │  │
│  │  │   MMSE      │    CNN       │    ADMIN     │         │  │
│  │  │  Component  │ Component    │  Component   │         │  │
│  │  │             │              │              │         │  │
│  │  │ • Test Form │ • Upload Box │ • 4 Tabs     │         │  │
│  │  │ • Sections  │ • Preview    │ • Stats      │         │  │
│  │  │ • Score     │ • Results    │ • Tables     │         │  │
│  │  │ • Results   │ • Analysis   │ • Charts     │         │  │
│  │  └─────────────┴──────────────┴──────────────┘         │  │
│  │                                                          │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Navigation Structure

```
                           Homepage (/)
                                 │
                    Redirects to /mmse
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
      /mmse                    /cnn                     /admin
        │                        │                        │
   ┌────────────┐           ┌─────────────┐          ┌──────────┐
   │ MMSE Test  │           │ CNN Scanner │          │ Dashboard│
   │ Component  │           │ Component   │          │Component │
   └────────────┘           └─────────────┘          └──────────┘
        │                        │                        │
        │                        │                        │
   [Backend API]           [Python API]            [Backend API]
   /api/mmse/*            /api/predict             /api/admin/*
```

## Component Hierarchy

```
AppComponent (Root)
├── NavigationComponent (Fixed Navbar)
│   ├── Logo & Branding
│   ├── Nav Menu (3 items)
│   │   ├── MMSE Test Link
│   │   ├── CNN Link
│   │   └── Admin Link
│   └── Mobile Hamburger Toggle
│
├── Brain3dComponent (Background Layer, z: -1)
│   ├── Three.js Scene
│   ├── Rotating Brain Model
│   └── Animation Loop
│
└── Router Outlet (Content Area, z: 2)
    ├── MMSETestComponent
    │   ├── Patient Info Section
    │   ├── 7 Test Sections
    │   ├── Results Display
    │   └── API Submission
    │
    ├── CNNPredictionComponent
    │   ├── Upload Section
    │   ├── Image Preview
    │   ├── Results Display
    │   ├── Analysis Details
    │   └── Score Breakdown
    │
    └── SuperAdminDashboardComponent
        ├── Header with Stats
        ├── Tab Navigation (4 tabs)
        ├── Overview Tab
        ├── Analytics Tab
        ├── Users Tab
        └── System Health Tab
```

## Data Flow

### MMSE Test Flow
```
User Input
    │
    ▼
MMSETestComponent
    │ (collect 7 section scores)
    │
    ▼
Calculate Total Score (0-30)
    │
    ▼
Determine Interpretation
(Normal/Mild/Moderate/Severe)
    │
    ▼
POST /api/mmse/submit
    │
    ▼
Backend: MMSEController
    │
    ▼
Store in MySQL Database
    │
    ▼
Display Results to User
```

### CNN Prediction Flow
```
User Input (Brain Scan Image)
    │
    ▼
CNNPredictionComponent
    │ (validate file)
    │
    ▼
Show Image Preview
    │
    ▼
POST /api/predict
    │
    ▼
Python Backend
    │ (CNN Model Processing)
    │
    ▼
Get Prediction Result
(Diagnosis + Confidence)
    │
    ▼
Display with Analysis
    │
    ▼
Optional: Store Result
```

### Admin Dashboard Flow
```
SuperAdminDashboardComponent
    │
    ├─→ GET /api/admin/super-dashboard
    │
    ├─→ GET /api/admin/stats
    │
    ├─→ GET /api/admin/users
    │
    ├─→ GET /api/admin/analytics/*
    │
    ├─→ GET /api/admin/system-health
    │
    └─→ Display Real-time Data
        on 4 Tabs
```

## File Structure Tree

```
frontend/
├── src/
│   ├── app/
│   │   ├── navigation/
│   │   │   └── navigation.component.ts ........... Fixed Navbar
│   │   │
│   │   ├── mmse/
│   │   │   ├── mmse-test.component.ts .......... Component
│   │   │   ├── mmse-test.component.html ....... Template
│   │   │   └── mmse-test.component.css ........ Styles
│   │   │
│   │   ├── cnn/ ......................... NEW
│   │   │   ├── cnn-prediction.component.ts .... Component
│   │   │   ├── cnn-prediction.component.html . Template
│   │   │   └── cnn-prediction.component.css .. Styles
│   │   │
│   │   ├── admin/
│   │   │   └── super-admin-dashboard/
│   │   │       ├── component.ts ............ Component
│   │   │       ├── component.html ......... Template
│   │   │       └── component.css .......... Styles
│   │   │
│   │   ├── brain3d/
│   │   │   ├── brain3d.component.ts
│   │   │   └── brain3d.component.css
│   │   │
│   │   ├── services/
│   │   │   └── admin.service.ts ......... API Service
│   │   │
│   │   ├── app.ts ................. Root Component (Updated)
│   │   ├── app.routes.ts .......... Routes (Updated)
│   │   └── app.config.ts
│   │
│   ├── index.html
│   ├── main.ts
│   ├── styles.css ................. Global Styles
│   └── assets/
│
└── Documentation/
    ├── NAVIGATION_GUIDE.md ......... Detailed Guide
    ├── NAVIGATION_IMPLEMENTATION.md  Technical Details
    └── QUICK_START_NAVIGATION.md ... Quick Reference
```

## Responsive Design Breakpoints

```
Desktop (> 1200px)
├─ Navbar: Horizontal, all items visible
├─ Content: Grid layout
└─ Sidebar: Full width

Tablet (768px - 1200px)
├─ Navbar: Horizontal, compact spacing
├─ Content: Adjusted grid
└─ Single column layout for forms

Mobile (< 768px)
├─ Navbar: Hamburger menu toggle
├─ Content: Full width
├─ Grid: Single column
└─ Forms: Stacked vertical
```

## Animation Timeline

```
Page Load
    │
    ├─→ 0.0s: Header slides down (0.6s duration)
    ├─→ 0.1s: Upload box slides up (0.6s duration)
    ├─→ 0.2s: Results slides up (0.6s duration)
    │
    └─→ Continuous: Brain background rotation
            (3s loop)

User Interaction
    │
    ├─→ Hover on nav link: Scale 1.05 (0.3s)
    ├─→ Hover on buttons: translateY(-2px) (0.3s)
    ├─→ Click navigation: Fade page (0.5s)
    │
    └─→ Results appear: Slide in effect (0.3s)
```

## Z-Index Stack (Layering)

```
┌──────────────────────────────────────────┐
│ Navigation Bar        (z: 1000)  ← Always on top
├──────────────────────────────────────────┤
│ Content Overlay       (z: 2)    ← Pages
├──────────────────────────────────────────┤
│ Brain Background      (z: -1)   ← Behind all
└──────────────────────────────────────────┘
```

## API Endpoints Map

```
Backend (Spring Boot) - localhost:8080

├── /api/mmse/
│   ├── POST /submit ..................... Submit test
│   ├── GET /results ..................... Get all results
│   └── GET /results/{patientName} ....... Get patient results
│
├── /api/admin/
│   ├── GET /super-dashboard ............ Dashboard data
│   ├── GET /dashboard ................. User dashboard
│   ├── GET /stats ..................... Statistics
│   ├── GET /search .................... Search users
│   ├── GET /filter .................... Filter users
│   ├── GET /export/users .............. Export CSV
│   ├── GET /export/mmse ............... Export MMSE
│   ├── GET /system-health ............ System status
│   └── ... (+ more analytics endpoints)
│

Python Backend (Flask/Django) - localhost:5000

└── /api/predict
    └── POST ............................ CNN prediction
        (Currently: Ready to integrate)
```

---

**Legend**:
- → : Flow direction
- [ ] : Component/Section
- z: : CSS z-index value
- ✓ : Implemented
- ⏳ : Pending/Optional

**Version**: 1.0  
**Last Updated**: February 4, 2026
