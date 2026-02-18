# Admin Service API Layer - Complete Documentation

## Overview
The Admin Service API Layer provides comprehensive dashboard analytics, user management, and system monitoring endpoints for the Alzheimer Detection System.

---

## Core Endpoints

### 1. Dashboard

#### Get Super Dashboard
- **Endpoint:** `GET /api/admin/super-dashboard`
- **Description:** Returns comprehensive dashboard data with analytics, stats, and system health
- **Response:** `AdminDashboardData`
```json
{
  "success": true,
  "message": "Dashboard data retrieved successfully",
  "data": {
    "stats": { /* DashboardStats */ },
    "mmseScoreDistribution": [ /* ChartData[] */ ],
    "userRoleDistribution": [ /* ChartData[] */ ],
    "recentActivities": [ /* RecentActivity[] */ ],
    "mmseTestAnalytics": [ /* MMSETestAnalytics[] */ ],
    "systemHealth": { /* SystemHealth */ }
  }
}
```

#### Get Dashboard
- **Endpoint:** `GET /api/admin/dashboard`
- **Description:** Returns list of users with their MMSE tests
- **Response:** `List<UserDashboardDTO>`

---

## Statistics Endpoints

### Get Admin Stats
- **Endpoint:** `GET /api/admin/stats`
- **Description:** Returns basic admin statistics
- **Response:** `AdminStats`

### Get Users Count
- **Endpoint:** `GET /api/admin/users-count`
- **Response:** Long

### Get MMSE Tests Count
- **Endpoint:** `GET /api/admin/mmse-tests-count`
- **Response:** Long

---

## Search & Filter Endpoints

### Search Users
- **Endpoint:** `GET /api/admin/search?query={query}`
- **Parameters:**
  - `query` (string, required): Search term (name, email)
- **Response:** `List<UserDashboardDTO>`

### Filter Users
- **Endpoint:** `GET /api/admin/filter?role={role}&active={active}`
- **Parameters:**
  - `role` (string, optional): ADMIN, DOCTOR, or USER
  - `active` (boolean, optional): true or false
- **Response:** `List<UserDashboardDTO>`

---

## Export Endpoints

### Export Users
- **Endpoint:** `GET /api/admin/export/users`
- **Description:** Export all users as CSV-compatible JSON
- **Response:** `List<Map<String, Object>>`
- **Fields:** ID, Email, Name, Role, Status, Phone, Created

### Export MMSE Tests
- **Endpoint:** `GET /api/admin/export/mmse`
- **Description:** Export all MMSE tests as CSV-compatible JSON
- **Response:** `List<Map<String, Object>>`
- **Fields:** ID, Patient, Total Score, Interpretation, Orientation, Registration, Attention, Recall, Language, Test Date, Notes

---

## Activity & Monitoring

### Get Activity Log
- **Endpoint:** `GET /api/admin/activity-log?limit={limit}`
- **Parameters:**
  - `limit` (int, default: 50): Maximum number of activities to retrieve
- **Response:** `List<Map<String, Object>>`
- **Activities Include:** USER_CREATED, TEST_CREATED events with timestamps

### Get System Health
- **Endpoint:** `GET /api/admin/health`
- **Description:** Returns system health status and metrics
- **Response:** `Map<String, Object>`
- **Fields:** status, database, timestamp, uptime

---

## Advanced Analytics Endpoints

### Monthly Trends
- **Endpoint:** `GET /api/admin/analytics/monthly-trends`
- **Description:** Get monthly average MMSE scores trend
- **Response:** `Map<String, Object>`
```json
{
  "monthlyAverages": {
    "2026-01": 25.5,
    "2026-02": 26.2
  },
  "totalMonths": 2
}
```

### Demographic Analysis
- **Endpoint:** `GET /api/admin/analytics/demographics`
- **Description:** Get user demographic breakdown by role and status
- **Response:** `Map<String, Object>`
```json
{
  "totalUsers": 4,
  "activeUsers": 4,
  "inactiveUsers": 0,
  "adminUsers": 1,
  "doctorUsers": 1,
  "patientUsers": 2
}
```

### Cognitive Distribution
- **Endpoint:** `GET /api/admin/analytics/cognitive-distribution`
- **Description:** Get distribution of cognitive impairment levels
- **Response:** `Map<String, Object>`
```json
{
  "distribution": {
    "Normal (24-30)": 2,
    "Mild (18-23)": 1,
    "Moderate (11-17)": 0,
    "Severe (0-10)": 0
  },
  "totalTests": 3
}
```

### MMSE Component Analysis
- **Endpoint:** `GET /api/admin/analytics/mmse-components`
- **Description:** Get average scores for each MMSE component
- **Response:** `Map<String, Object>`
```json
{
  "orientation": 9.0,
  "registration": 3.0,
  "attention": 4.67,
  "recall": 2.33,
  "language": 8.33
}
```

### High-Risk Patients
- **Endpoint:** `GET /api/admin/analytics/high-risk-patients?minScore={minScore}`
- **Parameters:**
  - `minScore` (int, default: 18): Score threshold for high-risk classification
- **Description:** Get patients with MMSE scores below threshold
- **Response:** `List<Map<String, Object>>`

### Registration Trends
- **Endpoint:** `GET /api/admin/analytics/registration-trends`
- **Description:** Get user registration trends by date
- **Response:** `Map<String, Object>`

### Performance Metrics
- **Endpoint:** `GET /api/admin/analytics/performance-metrics`
- **Description:** Get system performance metrics
- **Response:** `Map<String, Object>`
```json
{
  "totalUsers": 4,
  "totalTests": 3,
  "avgTestsPerUser": 0.75,
  "memoryUsage": 256,
  "maxMemory": 2048
}
```

---

## System Operations

### Create Backup
- **Endpoint:** `POST /api/admin/backup`
- **Description:** Create a system backup
- **Response:** `Map<String, String>`
```json
{
  "status": "success",
  "message": "Backup created successfully",
  "timestamp": "2026-02-04T10:30:45",
  "type": "full",
  "size": "N/A"
}
```

---

## Response Format

All endpoints follow a consistent response format using `ApiResponse<T>`:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* actual data */ },
  "error": null,
  "timestamp": "2026-02-04T10:30:45"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Operation failed",
  "data": null,
  "error": "Error message details",
  "timestamp": "2026-02-04T10:30:45"
}
```

---

## HTTP Status Codes

- **200 OK:** Successful request
- **400 Bad Request:** Invalid parameters
- **500 Internal Server Error:** Server error

---

## Data Models

### AdminDashboardData
- `stats`: DashboardStats
- `mmseScoreDistribution`: List<ChartData>
- `userRoleDistribution`: List<ChartData>
- `recentActivities`: List<RecentActivity>
- `mmseTestAnalytics`: List<MMSETestAnalytics>
- `systemHealth`: SystemHealth

### DashboardStats
- totalUsers, activeUsers, inactiveUsers
- totalAdmins, totalDoctors, totalPatients
- totalTests, averageScore
- normalCount, mildCount, moderateCount, severeCount
- testsThisMonth

### SystemHealth
- status: HEALTHY, WARNING, CRITICAL, UNKNOWN
- uptime: Long (seconds)
- cpuUsage: Double (percentage)
- memoryUsage: Double (percentage)
- databaseStatus: CONNECTED, ERROR
- lastBackup: String (date)

---

## Error Handling

All endpoints include comprehensive error handling with meaningful error messages. Errors are returned with appropriate HTTP status codes and include error details in the response.

---

## Authentication & Authorization

Currently, all endpoints are accessible without authentication. In production, implement authentication and role-based authorization.

---

## Performance Considerations

- Dashboard data is calculated on-demand for real-time accuracy
- Search and filter operations work on in-memory collections
- For large datasets, consider implementing pagination using `PaginationHelper`
- Monthly trend calculations use efficient stream operations

---

## Usage Examples

### Get Dashboard with Error Handling
```javascript
// Frontend
adminService.getSuperDashboard().subscribe({
  next: (response: ApiResponse<AdminDashboardData>) => {
    if (response.success) {
      console.log('Dashboard data:', response.data);
    }
  },
  error: (err) => {
    console.error('Failed to load dashboard:', err.error.error);
  }
});
```

### Export Data
```javascript
adminService.exportUsers().subscribe({
  next: (response: ApiResponse<any[]>) => {
    const csv = this.convertToCSV(response.data);
    // Download CSV file
  }
});
```

---

## Future Enhancements

- Real-time WebSocket updates for live dashboard
- Database backup/restore operations
- User activity audit trails
- Advanced filtering with date ranges
- Report generation (PDF, Excel)
- Role-based access control (RBAC)
