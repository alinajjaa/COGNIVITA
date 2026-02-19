@echo off
echo ========================================
echo   Starting Microservices Architecture
echo ========================================
echo.
echo Step 1: Starting Eureka Server (8761)...
start cmd /k "cd eureka-server && mvn spring-boot:run"
echo Waiting 30 seconds for Eureka to start...
timeout /t 30

echo.
echo Step 2: Starting Health-Prevention-Service (8081)...
start cmd /k "cd health-prevention-service && mvn spring-boot:run"
echo Waiting 20 seconds for service to register...
timeout /t 20

echo.
echo Step 3: Starting API Gateway (9090)...
start cmd /k "cd api-gateway && mvn spring-boot:run"
echo Waiting 15 seconds for gateway to start...
timeout /t 15

echo.
echo ========================================
echo   All services starting!
echo ========================================
echo.
echo Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:9090
echo Health Service: http://localhost:8081
echo.
echo Now update frontend to use: http://localhost:9090
echo Then start frontend with: ng serve
pause
