#!/bin/bash

# DocFlow Stop Script
# This script stops all running DocFlow services

echo "Stopping DocFlow services..."

# Stop Backend (find and kill the Spring Boot process)
echo "Stopping Backend API..."
pkill -f "spring-boot:run"

# Stop Polytope services
echo "Stopping Temporal UI..."
pt stop temporal-ui 2>/dev/null || echo "  (not running)"

echo "Stopping Temporal..."
pt stop temporal 2>/dev/null || echo "  (not running)"

echo "Stopping Postgres..."
pt stop postgres 2>/dev/null || echo "  (not running)"

echo ""
echo "================================"
echo "DocFlow services stopped"
echo "================================"
