@echo off
REM Alzheimer Detection System - Setup Script for Windows

echo ============================================
echo Alzheimer Detection System Setup
echo ============================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo [!] Docker is NOT installed
    echo Please install Docker Desktop from: https://www.docker.com/products/docker-desktop
    echo After installation, run this script again
    pause
    exit /b 1
)

echo [+] Docker found
docker --version

echo.
echo [*] Starting MySQL and phpMyAdmin containers...
cd /d "%~dp0"

REM Start Docker Compose
docker-compose up -d

echo.
echo [+] Waiting for services to start (30 seconds)...
timeout /t 30

echo.
echo ============================================
echo Setup Complete!
echo ============================================
echo.
echo phpMyAdmin URL: http://localhost:8888
echo   - Username: root
echo   - Password: root123
echo.
echo Database: alzheimer_db
echo Backend API: http://localhost:8080
echo.
echo [*] To stop the services, run: docker-compose down
echo.
pause
