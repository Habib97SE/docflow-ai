#!/bin/bash

# DocFlow Startup Script
# This script starts all required services for the DocFlow project

echo "Starting DocFlow services..."

# Check if Polytope CLI is available
if ! command -v pt &> /dev/null; then
    echo "Error: Polytope CLI (pt) not found. Please install it first."
    echo "Visit: https://polytope.com for installation instructions"
    exit 1
fi

# Check for Java and set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    echo "JAVA_HOME not set. Attempting to find Java..."
    
    # Try to find Java
    if command -v java &> /dev/null; then
        JAVA_PATH=$(which java)
        # Get the real path (resolve symlinks)
        JAVA_PATH=$(readlink -f "$JAVA_PATH")
        # Get the directory (remove /bin/java)
        export JAVA_HOME="${JAVA_PATH%/bin/java}"
        echo "Found Java at: $JAVA_HOME"
    else
        echo "Error: Java not found. Please install Java 17 or higher."
        echo "Ubuntu/Debian: sudo apt install openjdk-17-jdk"
        echo "Then set JAVA_HOME in your ~/.bashrc:"
        echo "  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
        exit 1
    fi
fi

# Verify Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven not found. Please install Maven."
    echo "Ubuntu/Debian: sudo apt install maven"
    exit 1
fi

# Start Postgres
echo "Starting Postgres..."
pt run postgres &
POSTGRES_PID=$!
sleep 3

# Start Temporal
echo "Starting Temporal..."
pt run temporal &
TEMPORAL_PID=$!
sleep 3

# Start Temporal UI
echo "Starting Temporal UI..."
pt run temporal-ui &
TEMPORAL_UI_PID=$!
sleep 2

# Start Backend (Spring Boot)
echo "Starting Backend API..."
cd backend
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

echo ""
echo "================================"
echo "DocFlow is starting up..."
echo "================================"
echo ""
echo "Services:"
echo "  - Postgres:     localhost:5432"
echo "  - Temporal:     localhost:7233"
echo "  - Temporal UI:  http://localhost:8233"
echo "  - Backend API:  http://localhost:8081"
echo ""
echo "Process IDs:"
echo "  - Postgres:     $POSTGRES_PID"
echo "  - Temporal:     $TEMPORAL_PID"
echo "  - Temporal UI:  $TEMPORAL_UI_PID"
echo "  - Backend:      $BACKEND_PID"
echo ""
echo "Press Ctrl+C to stop all services"
echo ""

# Wait for user interrupt
wait
