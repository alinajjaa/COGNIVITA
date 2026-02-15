# Alzheimer Detection System - Admin Dashboard & User Management

## System Setup Completed ✅

Your Alzheimer Detection System is now fully configured with:
- **Backend**: Spring Boot with MySQL/phpMyAdmin
- **Admin Dashboard**: Angular component with user CRUD and MMSE test results
- **Database**: MySQL (via XAMPP) with sample data

---

## Quick Start Guide

### 1. Start XAMPP
```
1. Open XAMPP Control Panel
2. Click "Start" next to MySQL
3. Optionally start Apache to access phpMyAdmin
```

### 2. Run the Backend
**Option A: Using Batch File**
```
Double-click: C:\Alzheimer-Detection-System\run-backend.bat
```

**Option B: Using Terminal**
```powershell
cd C:\Alzheimer-Detection-System\Backend
.\mvnw.cmd spring-boot:run
```

The backend will start at `http://localhost:8080`

### 3. Access phpMyAdmin
```
URL: http://localhost/phpmyadmin
Username: root
Password: (leave empty)
Database: alzheimer_db
```

### 4. Run Angular Frontend
```powershell
cd C:\Alzheimer-Detection-System\frontend
npm install
ng serve
```

Frontend will be available at `http://localhost:4200`

---

## API Endpoints

### User Management (`/api/users`)
```bash
# Get all users
GET http://localhost:8080/api/users

# Get user by ID
GET http://localhost:8080/api/users/{id}

# Get user by email
GET http://localhost:8080/api/users/email/{email}

# Get active users only
GET http://localhost:8080/api/users/active

# Get users by role
GET http://localhost:8080/api/users/role/{role}  # ADMIN, USER, DOCTOR

# Create new user
POST http://localhost:8080/api/users
Content-Type: application/json
{
  "email": "newuser@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "password": "secure123",
  "phone": "555-1234",
  "role": "USER",
  "active": true
}

# Update user
PUT http://localhost:8080/api/users/{id}
Content-Type: application/json
{
  "email": "updated@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "password": "newsecure123",
  "phone": "555-5678",
  "role": "DOCTOR",
  "active": true
}

# Delete user
DELETE http://localhost:8080/api/users/{id}
```

### MMSE Tests (`/api/mmse-tests`)
```bash
# Get all MMSE tests
GET http://localhost:8080/api/mmse-tests

# Get MMSE test by ID
GET http://localhost:8080/api/mmse-tests/{id}

# Create MMSE test
POST http://localhost:8080/api/mmse-tests
Content-Type: application/json
{
  "patientName": "John Doe",
  "orientationScore": 10,
  "registrationScore": 3,
  "attentionScore": 5,
  "recallScore": 3,
  "languageScore": 9,
  "notes": "Initial assessment"
}

# Update MMSE test
PUT http://localhost:8080/api/mmse-tests/{id}

# Delete MMSE test
DELETE http://localhost:8080/api/mmse-tests/{id}
```

### Admin Dashboard (`/api/admin`)
```bash
# Get complete dashboard data (all users with their MMSE tests)
GET http://localhost:8080/api/admin/dashboard

# Get admin statistics
GET http://localhost:8080/api/admin/stats
Response: {
  "usersCount": 4,
  "mmseTestsCount": 3,
  "activeUsersCount": 4
}

# Get user count
GET http://localhost:8080/api/admin/users-count

# Get MMSE tests count
GET http://localhost:8080/api/admin/mmse-tests-count
```

---

## Database Schema

### users table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(50) DEFAULT 'USER',  -- ADMIN, USER, DOCTOR
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL
);
```

### mmse_tests table
```sql
CREATE TABLE mmse_tests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_name VARCHAR(255),
    orientation_score INT NOT NULL,      -- 0-10
    registration_score INT NOT NULL,    -- 0-3
    attention_score INT NOT NULL,       -- 0-5
    recall_score INT NOT NULL,          -- 0-3
    language_score INT NOT NULL,        -- 0-9
    total_score INT NOT NULL,           -- 0-30
    interpretation VARCHAR(255),        -- Normal, Mild, Moderate, Severe
    test_date DATE,
    notes VARCHAR(255)
);
```

### Sample Data
**Users:**
- admin@alzheimer.com (ADMIN)
- doctor@alzheimer.com (DOCTOR)
- patient1@alzheimer.com (USER)
- patient2@alzheimer.com (USER)

**MMSE Tests:**
- John Doe: Score 30/30 (Normal cognition)
- Jane Smith: Score 26/30 (Normal cognition)
- John Doe: Score 26/30 (Normal cognition)

---

## Angular Admin Dashboard Component

### Files Created:
```
frontend/src/app/admin/
├── admin-dashboard/
│   ├── admin-dashboard.component.ts
│   ├── admin-dashboard.component.html
│   └── admin-dashboard.component.css
└── services/
    └── admin.service.ts
```

### Features:
✅ Display all users with their details
✅ Show MMSE test results for each user
✅ Display admin statistics (total users, active users, total tests)
✅ Delete users
✅ View MMSE test scores and interpretations
✅ Responsive table layout

### Route Setup (in app.routes.ts):
```typescript
{
  path: 'admin',
  component: AdminDashboardComponent
}
```

Access at: `http://localhost:4200/admin`

---

## Testing the System

### Test User CRUD Operations
```powershell
# Create a new user
$body = @{
    email = "test@example.com"
    firstName = "Test"
    lastName = "User"
    password = "test123"
    role = "USER"
    active = $true
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/users" `
    -Method POST `
    -Body $body `
    -ContentType "application/json"

# Get all users
Invoke-WebRequest -Uri "http://localhost:8080/api/users" -Method GET | Select-Object Content

# Get admin dashboard
Invoke-WebRequest -Uri "http://localhost:8080/api/admin/dashboard" -Method GET | Select-Object Content
```

---

## File Structure

```
Alzheimer-Detection-System/
├── Backend/
│   ├── src/main/java/com/alzheimer/backend/
│   │   ├── admin/
│   │   │   ├── AdminDashboardController.java
│   │   │   └── AdminStats.java
│   │   ├── user/
│   │   │   ├── User.java
│   │   │   ├── UserRole.java
│   │   │   ├── UserRepository.java
│   │   │   ├── UserService.java
│   │   │   └── UserController.java
│   │   ├── mmse/
│   │   │   ├── MMSETest.java
│   │   │   ├── MMSERepository.java
│   │   │   ├── MMSEService.java
│   │   │   └── MMSEController.java
│   │   ├── dto/
│   │   │   └── UserDashboardDTO.java
│   │   └── controller/
│   │       └── PredictionController.java
│   └── src/main/resources/
│       └── application.properties
├── frontend/
│   └── src/app/
│       ├── admin/
│       │   └── admin-dashboard/
│       ├── services/
│       │   └── admin.service.ts
│       └── ...
└── run-backend.bat
```

---

## Troubleshooting

### MySQL Connection Failed
```
✅ Solution: Ensure XAMPP MySQL is running
- Open XAMPP Control Panel
- Click "Start" next to MySQL
```

### Port 8080 Already in Use
```
✅ Solution: 
Method 1: Stop the process using port 8080
  netstat -ano | findstr ":8080"
  taskkill /PID <PID> /F

Method 2: Change port in application.properties
  server.port=8081
```

### Database Not Found
```
✅ Solution: Create the database
& 'C:\xampp\mysql\bin\mysql.exe' -u root < setup_database.sql
```

---

## Next Steps

1. **Deploy Frontend**: Update Angular routing to include admin dashboard
2. **Add Authentication**: Implement JWT or Spring Security
3. **Enhanced UI**: Improve dashboard with charts and graphs
4. **Email Notifications**: Add email alerts for MMSE test results
5. **File Upload**: Support for medical images and documents
6. **API Documentation**: Generate Swagger/OpenAPI documentation

---

## Support

For issues or questions:
- Check the Backend logs: Terminal running the Spring Boot application
- Check the Browser Console: F12 in Angular app
- Verify MySQL connection: phpMyAdmin at `http://localhost/phpmyadmin`

---

**System Ready for Use!** 🚀
