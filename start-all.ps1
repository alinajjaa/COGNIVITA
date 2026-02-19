Write-Host "========================================"
Write-Host "  Starting Microservices Architecture  "
Write-Host "========================================"
Write-Host ""

Write-Host "Step 1: Starting Eureka Server (8761)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd eureka-server; mvn spring-boot:run"
Write-Host "Waiting 30 seconds for Eureka to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host "Step 2: Starting Health-Prevention-Service (8081)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd health-prevention-service; mvn spring-boot:run"
Write-Host "Waiting 20 seconds for service to register..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

Write-Host "Step 3: Starting API Gateway (9090)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd api-gateway; mvn spring-boot:run"
Write-Host "Waiting 15 seconds for gateway to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host ""
Write-Host "========================================"
Write-Host "       All services started!            "
Write-Host "========================================"
Write-Host ""
Write-Host "✅ Eureka Dashboard: http://localhost:8761" -ForegroundColor Green
Write-Host "✅ API Gateway: http://localhost:9090" -ForegroundColor Green
Write-Host "✅ Health Service: http://localhost:8081" -ForegroundColor Green
Write-Host ""
Write-Host "Now update frontend to use: http://localhost:9090" -ForegroundColor Cyan
Write-Host "Then run: ng serve" -ForegroundColor Cyan
