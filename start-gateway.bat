@echo off
echo ========================================
echo   Starting API Gateway (Port 9090)
echo ========================================
echo IMPORTANT: Wait for Eureka Server to start first!
timeout /t 5
cd api-gateway
call mvn spring-boot:run
