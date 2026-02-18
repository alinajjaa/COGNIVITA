# Alzheimer Detection System - MySQL + phpMyAdmin Setup

## Prerequisites

You need to install **Docker Desktop** for Windows to run MySQL and phpMyAdmin.

### Installation Steps

1. **Install Docker Desktop**
   - Download from: https://www.docker.com/products/docker-desktop
   - Run the installer and follow the prompts
   - Restart your computer after installation

2. **Verify Docker Installation**
   ```powershell
   docker --version
   ```

## Running the Services

### Option 1: Using Setup Script (Easiest)
1. Open PowerShell or Command Prompt
2. Navigate to the Alzheimer-Detection-System folder
3. Double-click `setup.bat` or run:
   ```powershell
   .\setup.bat
   ```

### Option 2: Manual Docker Compose
1. Open PowerShell in the project root directory
2. Run:
   ```powershell
   docker-compose up -d
   ```

## Accessing the Services

### phpMyAdmin (Database Management)
- **URL:** http://localhost:8888
- **Username:** root
- **Password:** root123
- **Database:** alzheimer_db

### MySQL Database
- **Host:** localhost
- **Port:** 3306
- **Username:** root
- **Password:** root123
- **Database:** alzheimer_db

### Backend API
- **URL:** http://localhost:8080
- **Available Endpoints:**
  - `POST /api/users` - Create user
  - `GET /api/users` - Get all users
  - `GET /api/users/{id}` - Get user by ID
  - `PUT /api/users/{id}` - Update user
  - `DELETE /api/users/{id}` - Delete user
  - `GET /api/admin/dashboard` - Admin dashboard with all users and MMSE tests
  - `GET /api/admin/stats` - Admin statistics
  - `POST /api/mmse-tests` - Create MMSE test
  - `GET /api/mmse-tests` - Get all MMSE tests

## Database Schema

The system automatically creates the following tables:

### users table
- id (Primary Key)
- email (Unique)
- first_name
- last_name
- password
- phone
- role (ADMIN, USER, DOCTOR)
- active (Boolean)
- created_at
- updated_at

### mmse_tests table
- id (Primary Key)
- patient_name
- orientation_score (0-10)
- registration_score (0-3)
- attention_score (0-5)
- recall_score (0-3)
- language_score (0-9)
- total_score (0-30)
- interpretation
- test_date
- notes

## Starting the Backend

1. Open PowerShell in the Backend folder
2. Run:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

## Stopping the Services

```powershell
docker-compose down
```

## Troubleshooting

### Port Already in Use
If you get "port already in use" errors:
```powershell
# For MySQL (3306)
Get-Process | Where-Object {$_.Name -like "*mysql*"} | Stop-Process -Force

# For phpMyAdmin (8888)
Get-NetTCPConnection -LocalPort 8888 | Stop-Process -Force
```

### Docker Connection Issues
Make sure Docker Desktop is running. If it's not:
1. Open Docker Desktop application
2. Wait for it to fully start
3. Try again

### Database Connection Failed
1. Check if MySQL container is running:
   ```powershell
   docker ps
   ```
2. If not running, restart:
   ```powershell
   docker-compose restart
   ```

## Sample Data

The database is pre-populated with:
- 4 sample users (admin, doctor, 2 patients)
- 3 sample MMSE test results

## API Examples

### Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "password": "password123",
    "phone": "1234567890",
    "role": "USER",
    "active": true
  }'
```

### Get Admin Dashboard
```bash
curl http://localhost:8080/api/admin/dashboard
```

### Create MMSE Test
```bash
curl -X POST http://localhost:8080/api/mmse-tests \
  -H "Content-Type: application/json" \
  -d '{
    "patientName": "John Doe",
    "orientationScore": 10,
    "registrationScore": 3,
    "attentionScore": 5,
    "recallScore": 3,
    "languageScore": 9,
    "notes": "Initial assessment"
  }'
```

## Next Steps

1. Install Docker Desktop
2. Run `.\setup.bat` or `docker-compose up -d`
3. Access phpMyAdmin at http://localhost:8888
4. Run the backend with `.\mvnw.cmd spring-boot:run`
5. Access the admin dashboard frontend at http://localhost:4200

---

For more help, refer to the main README.md in the project root.
