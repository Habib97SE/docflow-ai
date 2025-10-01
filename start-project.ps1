# DocFlow Startup Script for Windows PowerShell
# This script starts all required services for the DocFlow project

Write-Host "Starting DocFlow services..." -ForegroundColor Green

# Start Postgres
Write-Host "Starting Postgres..." -ForegroundColor Yellow
Start-Process -NoNewWindow -FilePath "pt" -ArgumentList "run", "postgres", "--background"
Start-Sleep -Seconds 3

# Start Temporal
Write-Host "Starting Temporal..." -ForegroundColor Yellow
Start-Process -NoNewWindow -FilePath "pt" -ArgumentList "run", "temporal", "--background"
Start-Sleep -Seconds 3

# Start Temporal UI
Write-Host "Starting Temporal UI..." -ForegroundColor Yellow
Start-Process -NoNewWindow -FilePath "pt" -ArgumentList "run", "temporal-ui", "--background"
Start-Sleep -Seconds 2

# Start Backend (Spring Boot)
Write-Host "Starting Backend API..." -ForegroundColor Yellow
Set-Location backend
Start-Process -NoNewWindow -FilePath "mvn" -ArgumentList "spring-boot:run"
Set-Location ..

Write-Host ""
Write-Host "================================" -ForegroundColor Green
Write-Host "DocFlow is starting up..." -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "Services:"
Write-Host "  - Postgres:     localhost:5432"
Write-Host "  - Temporal:     localhost:7233"
Write-Host "  - Temporal UI:  http://localhost:8233"
Write-Host "  - Backend API:  http://localhost:8081"
Write-Host ""
Write-Host "To check service status, use the Polytope CLI:"
Write-Host "  pt list-services"
Write-Host ""
Write-Host "To stop services, run: .\stop-project.ps1"
Write-Host ""
