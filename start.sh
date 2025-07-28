#!/bin/bash

# Inventory Service Demo Startup Script
# This script starts both the backend (Spring Boot) and frontend (React) services

set -e  # Exit on any error

echo "🚀 Starting Inventory Service Demo..."
echo "=================================="

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to cleanup processes on script exit
cleanup() {
    echo -e "\n${YELLOW}Shutting down services...${NC}"
    if [ ! -z "$BACKEND_PID" ]; then
        echo "Stopping backend (PID: $BACKEND_PID)"
        kill $BACKEND_PID 2>/dev/null || true
    fi
    if [ ! -z "$FRONTEND_PID" ]; then
        echo "Stopping frontend (PID: $FRONTEND_PID)"
        kill $FRONTEND_PID 2>/dev/null || true
    fi
    echo "Services stopped."
    exit 0
}

# Set up trap to cleanup on script exit
trap cleanup SIGINT SIGTERM EXIT

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"
FRONTEND_DIR="$SCRIPT_DIR/frontend"
LOGS_DIR="$SCRIPT_DIR/logs"

# Create logs directory if it doesn't exist
mkdir -p "$LOGS_DIR"

# Check if directories exist
if [ ! -d "$BACKEND_DIR" ]; then
    echo -e "${RED}Error: Backend directory not found at $BACKEND_DIR${NC}"
    exit 1
fi

if [ ! -d "$FRONTEND_DIR" ]; then
    echo -e "${RED}Error: Frontend directory not found at $FRONTEND_DIR${NC}"
    exit 1
fi

# Kill any existing processes on ports 8080 and 3000
echo -e "${YELLOW}Checking for existing processes on ports 8080 and 3000...${NC}"
EXISTING_8080=$(lsof -ti:8080 2>/dev/null || true)
EXISTING_3000=$(lsof -ti:3000 2>/dev/null || true)

if [ ! -z "$EXISTING_8080" ]; then
    echo "Killing existing process on port 8080 (PID: $EXISTING_8080)"
    kill -9 $EXISTING_8080 2>/dev/null || true
fi

if [ ! -z "$EXISTING_3000" ]; then
    echo "Killing existing processes on port 3000 (PIDs: $EXISTING_3000)"
    kill -9 $EXISTING_3000 2>/dev/null || true
fi

# Start backend
echo -e "${BLUE}Starting backend (Spring Boot)...${NC}"
cd "$BACKEND_DIR"
./mvnw spring-boot:run > "$LOGS_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
echo "Backend started with PID: $BACKEND_PID"

# Wait a moment for backend to start
echo "Waiting for backend to initialize..."
sleep 10

# Check if backend is running
if ! kill -0 $BACKEND_PID 2>/dev/null; then
    echo -e "${RED}Backend failed to start. Check backend.log for details.${NC}"
    exit 1
fi

# Test backend health
echo "Testing backend health..."
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}Backend is healthy!${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${RED}Backend health check failed after 30 attempts${NC}"
        exit 1
    fi
    sleep 1
done

# Start frontend
echo -e "${BLUE}Starting frontend (React)...${NC}"
cd "$FRONTEND_DIR"
npm start > "$LOGS_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "Frontend started with PID: $FRONTEND_PID"

# Wait for frontend to start
echo "Waiting for frontend to initialize..."
sleep 15

# Check if frontend is running
if ! kill -0 $FRONTEND_PID 2>/dev/null; then
    echo -e "${RED}Frontend failed to start. Check frontend.log for details.${NC}"
    exit 1
fi

# Test frontend health
echo "Testing frontend health..."
for i in {1..30}; do
    if curl -s http://localhost:3000 > /dev/null 2>&1; then
        echo -e "${GREEN}Frontend is healthy!${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${RED}Frontend health check failed after 30 attempts${NC}"
        exit 1
    fi
    sleep 1
done

echo -e "${GREEN}✅ Both services are running successfully!${NC}"
echo "=================================="
echo -e "${GREEN}🌐 Frontend: http://localhost:3000${NC}"
echo -e "${GREEN}🔧 Backend:  http://localhost:8080${NC}"
echo -e "${GREEN}📊 Backend Health: http://localhost:8080/actuator/health${NC}"
echo "=================================="
echo -e "${YELLOW}Press Ctrl+C to stop both services${NC}"
echo ""
echo "Logs:"
echo "  Backend: logs/backend.log"
echo "  Frontend: logs/frontend.log"

# Keep script running and show logs
echo -e "${BLUE}Monitoring services... (Press Ctrl+C to stop)${NC}"
while true; do
    # Check if processes are still running
    if ! kill -0 $BACKEND_PID 2>/dev/null; then
        echo -e "${RED}Backend process died unexpectedly!${NC}"
        exit 1
    fi
    if ! kill -0 $FRONTEND_PID 2>/dev/null; then
        echo -e "${RED}Frontend process died unexpectedly!${NC}"
        exit 1
    fi
    sleep 5
done
