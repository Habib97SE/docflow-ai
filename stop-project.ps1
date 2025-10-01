# DocFlow Stop Script for Windows PowerShell
# This script stops all running DocFlow services

Write-Host "Stopping DocFlow services..." -ForegroundColor Yellow

# Stop Backend (find and kill the Spring Boot process)
Write-Host "Stopping Backend API..." -ForegroundColor Yellow
Get-Process | Where-Object {$_.CommandLine -like "*spring-boot:run*"} | Stop-Process -Force -ErrorAction SilentlyContinue

# Stop Polytope services using Polytope CLI
Write-Host "Stopping Temporal UI..." -ForegroundColor Yellow
& pt stop temporal-ui 2>$null

Write-Host "Stopping Temporal..." -ForegroundColor Yellow
& pt stop temporal 2>$null

Write-Host "Stopping Postgres..." -ForegroundColor Yellow
& pt stop postgres 2>$null

Write-Host ""
Write-Host "================================" -ForegroundColor Green
Write-Host "DocFlow services stopped" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "To verify all services are stopped:"
Write-Host "  pt list-services"
Write-Host ""
