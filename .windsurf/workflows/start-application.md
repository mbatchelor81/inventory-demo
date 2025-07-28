---
description: Start the full-stack inventory service application
---

# Start Inventory Service Demo

This workflow starts both the backend (Spring Boot) and frontend (React) services for the inventory service demo application.

## Quick Start

To start both services with a single command:

```bash
./start.sh
```

## What the Script Does

The `start.sh` script will:

1. **Clean up existing processes** - Kills any processes running on ports 3000 and 8080
2. **Start the backend** - Runs the Spring Boot application on port 8080
3. **Wait for backend health** - Ensures the backend is fully initialized
4. **Start the frontend** - Runs the React development server on port 3000
5. **Monitor services** - Keeps both services running and monitors their health
6. **Graceful shutdown** - Press `Ctrl+C` to stop both services cleanly

## Service URLs

Once started, you can access:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Backend Health Check**: http://localhost:8080/actuator/health

## Logs

The script creates log files in the `logs/` directory for debugging:
- `logs/backend.log` - Spring Boot application logs
- `logs/frontend.log` - React development server logs

## Start a preview
- Start a preview of the application now that it is running.