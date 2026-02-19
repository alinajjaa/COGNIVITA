# Alzheimer Detection System - Microservices Architecture

## 🏗️ Architecture Overview

```
┌─────────────────┐
│  Angular App    │  Port 4200
│  (Frontend)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  API Gateway    │  Port 9090 ⚡ NEW!
│  (Spring Cloud) │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Eureka Server   │  Port 8761
│ (Service Reg.)  │
└─────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌─────────┐ ┌──────────┐
│ Health  │ │  Future  │
│ Service │ │ Services │
│  8081   │ │          │
└─────────┘ └──────────┘
```

## 🔌 Port Assignments

| Service | Port | Purpose |
|---------|------|---------|
| Eureka Server | **8761** | Service registry & discovery |
| **API Gateway** | **9090** | Single entry point for all APIs |
| Health-Prevention-Service | **8081** | Medical records backend (unchanged) |
| Angular Frontend | **4200** | User interface |

**⚠️ IMPORTANT:** 
- Port **8080** is NOT used (likely occupied by old backend)
- Port **8081** remains unchanged (existing backend service)
- Port **9090** is the NEW API Gateway

## 🚀 How to Run

### Step 1: Start Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
✅ Check: http://localhost:8761

### Step 2: Start Health-Prevention-Service
```bash
cd health-prevention-service
mvn spring-boot:run
```
✅ Registers with Eureka at port 8081 (unchanged)

### Step 3: Start API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```
✅ Listens on port 9090 (NEW)

### Step 4: Update & Start Frontend
```bash
cd frontend
# Run the update script first (changes 8081 → 9090)
# Then:
npm install
ng serve
```
✅ Navigate to http://localhost:4200

## 📡 URL Mapping

**Before (Direct Backend):**
```
Frontend → http://localhost:8081/api/medical-records
```

**After (Via Gateway):**
```
Frontend → http://localhost:9090/api/medical-records
           ↓
Gateway routes to → http://localhost:8081/api/medical-records
```

## 🎯 Testing

**1. Eureka Dashboard**
```
http://localhost:8761
```
Should show both services registered.

**2. Direct Backend (still works)**
```
http://localhost:8081/api/medical-records
```

**3. Through Gateway (recommended)**
```
http://localhost:9090/api/medical-records
```

Both #2 and #3 return the same data, but #3 goes through the gateway for load balancing and routing.

## 🔄 Startup Order

1. **Eureka Server** (8761) - FIRST
2. **Health-Prevention-Service** (8081) - registers with Eureka
3. **API Gateway** (9090) - discovers services from Eureka
4. **Frontend** (4200) - calls Gateway at 9090

## 🛠️ Frontend Configuration

The update script changes these files from port **8081** → **9090**:

- `medical-records.component.ts`
- `risk-factor.service.ts`
- `timeline.service.ts`
- `prevention-action.service.ts`
- `statistics.service.ts`
- `admin.service.ts`
- `cnn-prediction.component.ts`
- `medical-charts.component.ts`

## ⚡ Quick Start Script

**PowerShell:**
```powershell
.\start-all.ps1
```

**Batch:**
```cmd
start-all.bat
```

Opens 3 terminals automatically in correct order.
