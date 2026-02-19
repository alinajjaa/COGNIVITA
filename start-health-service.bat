@echo off
echo ========================================
echo  Starting Health-Prevention-Service
echo  (Port 8081)
echo ========================================
echo IMPORTANT: Wait for Eureka Server to start first!
timeout /t 5
cd health-prevention-service
call mvn spring-boot:run
