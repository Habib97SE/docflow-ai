# DocFlow - First Time Setup Guide

This guide will help you set up the DocFlow project on your local machine for the first time.

## Prerequisites

Before you start, make sure you have these installed:

### Required Software

1. **Java 17 or higher**
   ```bash
   # Check your Java version
   java -version
   ```
   Download from: https://adoptium.net/

2. **Maven 3.6+**
   ```bash
   # Check your Maven version
   mvn -version
   ```
   Download from: https://maven.apache.org/download.cgi

3. **Polytope CLI**
   ```bash
   # Check if Polytope is installed
   pt --version
   ```
   Installation instructions: https://polytope.com

4. **Docker** (required for Polytope)
   ```bash
   # Check if Docker is installed
   docker --version
   ```
   Download from: https://www.docker.com/products/docker-desktop

## Setup Steps

### 1. Clone the Repository

```bash
git clone https://github.com/Habib97SE/docflow-ai.git
cd docflow-ai
```

### 2. No Environment Variables Needed! 

**Good news:** There are NO environment variables to configure! Everything is pre-configured for local development in the `application.properties` file.

### 3. Make Scripts Executable (Linux/Mac/Ubuntu only)

```bash
chmod +x start-project.sh stop-project.sh
```

### 4. Start the Project

**For Linux/Mac/Ubuntu:**
```bash
./start-project.sh
```

**For Windows (PowerShell):**
```powershell
.\start-project.ps1
```

That's it! The script will automatically:
- Start PostgreSQL database
- Start Temporal workflow engine
- Start Temporal UI
- Start the Spring Boot backend

### 5. Verify Everything is Running

After a minute or two, you should be able to access:

- **Backend API**: http://localhost:8081
- **Temporal UI**: http://localhost:8233

Try the health check endpoint:
```bash
curl http://localhost:8081/api/documents/health
```

You should see: `Document service is running`

## Troubleshooting

### Port Already in Use

If you get a "port already in use" error:

1. Check what's using the port:
   ```bash
   # Linux/Mac
   lsof -i :8081
   
   # Windows
   netstat -ano | findstr :8081
   ```

2. Stop the conflicting process or change the port in `backend/src/main/resources/application.properties`

### Polytope CLI Not Found

If the startup script says Polytope is not found:

1. Install Polytope from https://polytope.com
2. Make sure it's in your PATH
3. Restart your terminal

### Java Version Issues

If you get Java version errors:

1. Make sure you have Java 17 or higher installed
2. Set JAVA_HOME environment variable:
   ```bash
   # Linux/Mac
   export JAVA_HOME=/path/to/java17
   
   # Windows (PowerShell)
   $env:JAVA_HOME="C:\path\to\java17"
   ```

### Maven Not Found

If Maven is not found:

1. Download Maven from https://maven.apache.org/download.cgi
2. Extract and add to PATH
3. Set M2_HOME environment variable

## Configuration Files

All configuration is in these files (no changes needed for local dev):

- `backend/src/main/resources/application.properties` - Backend configuration
- `polytope.yml` - Polytope service orchestration
- `modules/postgres/polytope.yml` - Postgres configuration
- `modules/temporal/polytope.yml` - Temporal configuration

**Default Configuration:**
- Server Port: 8081
- Database: PostgreSQL on localhost:5432
- Database User: postgres
- Database Password: (none - dev mode)
- Temporal: localhost:7233

## Stopping the Project

**For Linux/Mac/Ubuntu:**
```bash
./stop-project.sh
```

**For Windows (PowerShell):**
```powershell
.\stop-project.ps1
```

## Next Steps

Once everything is running:

1. Read the main [README.md](README.md) for API documentation
2. Try uploading a document via the API
3. View workflows in the Temporal UI at http://localhost:8233

## Getting Help

If you run into issues:

1. Check the [README.md](README.md) Troubleshooting section
2. Check the backend logs (visible in the terminal where you started the project)
3. Check Polytope service status: `pt list-services`
4. Open an issue on GitHub

## Summary

**What you need to do:**
1. Install Java 17+, Maven, Docker, and Polytope CLI
2. Clone the repo
3. Run the startup script

**What you DON'T need to do:**
- ❌ Set up environment variables
- ❌ Create databases manually
- ❌ Configure connection strings
- ❌ Install additional dependencies
- ❌ Manually start each service

Everything else is handled automatically!
