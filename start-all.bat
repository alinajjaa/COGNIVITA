@echo off
echo Starting all servers...

start "FastAPI Server" cmd /k "cd api && uvicorn main:app --host 127.0.0.1 --port 8000 --reload"
start "Spring Boot Backend" cmd /k "cd Backend && mvnw.cmd spring-boot:run"
start "Angular Frontend" cmd /k "cd frontend && ng serve"

echo All servers are starting in separate windows...
pause
