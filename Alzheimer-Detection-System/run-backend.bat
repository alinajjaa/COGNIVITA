@echo off
cd /d C:\Alzheimer-Detection-System\Backend
echo Starting Alzheimer Detection System Backend...
echo.
echo Backend will be available at: http://localhost:8080
echo API Documentation:
echo   - GET  /api/users           - Get all users
echo   - POST /api/users           - Create new user
echo   - GET  /api/users/{id}      - Get user by ID
echo   - PUT  /api/users/{id}      - Update user
echo   - DELETE /api/users/{id}    - Delete user
echo.
echo   - GET  /api/mmse-tests      - Get all MMSE tests
echo   - POST /api/mmse-tests      - Create MMSE test
echo   - GET  /api/admin/dashboard - Get admin dashboard
echo   - GET  /api/admin/stats     - Get admin statistics
echo.
echo MySQL Database: alzheimer_db
echo phpMyAdmin URL: http://localhost/phpmyadmin
echo.
call mvnw spring-boot:run
pause
