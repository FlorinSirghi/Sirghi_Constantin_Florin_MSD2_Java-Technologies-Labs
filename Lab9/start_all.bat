@echo off
REM ========================================
REM Start All Services for Lab9 Testing
REM ========================================

echo.
echo ========================================
echo Starting All Lab9 Services
echo ========================================
echo.

REM Set JAVA_HOME for all sessions
set JAVA_HOME=C:\Program Files\Java\jdk-25
set PATH=%JAVA_HOME%\bin;%PATH%

echo [INFO] Java: %JAVA_HOME%
echo [INFO] Working Directory: %CD%
echo.

REM Check if Docker is available (optional)
docker --version >nul 2>&1
if %errorlevel% equ 0 (
    set DOCKER_AVAILABLE=1
    echo [INFO] Docker detected - will start Prometheus and Grafana
) else (
    set DOCKER_AVAILABLE=0
    echo [WARN] Docker not available - skipping Prometheus/Grafana
    echo        (Start them manually with: docker-compose up -d)
)
echo.

REM ========================================
REM Step 1: Start Eureka Server
REM ========================================
echo [1/6] Starting Eureka Server (port 8761)...
start "Eureka Server" cmd /k "%CD%\start-eureka-wrapper.bat"
echo [INFO] Waiting for Eureka Server to start (15 seconds)...
timeout /t 15 /nobreak >nul
echo [OK] Eureka Server should be ready
echo.

REM ========================================
REM Step 2: Start StableMatch Instances
REM ========================================
echo [2/6] Starting StableMatch Instance 1 (port 8081)...
start "StableMatch-8081" cmd /k "%CD%\start-stablematch-wrapper.bat 8081"
timeout /t 5 /nobreak >nul

echo [3/6] Starting StableMatch Instance 2 (port 8082)...
start "StableMatch-8082" cmd /k "%CD%\start-stablematch-wrapper.bat 8082"
timeout /t 5 /nobreak >nul

echo [4/6] Starting StableMatch Instance 3 (port 8083)...
start "StableMatch-8083" cmd /k "%CD%\start-stablematch-wrapper.bat 8083"
timeout /t 5 /nobreak >nul

echo [INFO] Waiting for StableMatch instances to register with Eureka (20 seconds)...
timeout /t 20 /nobreak >nul
echo [OK] StableMatch instances should be registered
echo.

REM ========================================
REM Step 3: Start API Gateway
REM ========================================
echo [5/6] Starting API Gateway (port 8080)...
start "API Gateway" cmd /k "%CD%\start-gateway-wrapper.bat"
timeout /t 5 /nobreak >nul
echo [OK] API Gateway starting
echo.

REM ========================================
REM Step 4: Start Prometheus and Grafana (if Docker available)
REM ========================================
if %DOCKER_AVAILABLE% equ 1 (
    echo [6/6] Starting Prometheus and Grafana (Docker)...
    docker-compose up -d
    if %errorlevel% equ 0 (
        echo [OK] Prometheus and Grafana started
        echo [INFO] Prometheus: http://localhost:9090
        echo [INFO] Grafana: http://localhost:3000 (admin/admin)
    ) else (
        echo [WARN] Failed to start Docker services. Check if Docker Desktop is running.
    )
) else (
    echo [6/6] Skipping Prometheus/Grafana (Docker not available)
)
echo.

REM ========================================
REM Summary
REM ========================================
echo.
echo ========================================
echo All Services Started!
echo ========================================
echo.
echo Services:
echo   [OK] Eureka Server        - http://localhost:8761
echo   [OK] StableMatch-8081     - http://localhost:8081
echo   [OK] StableMatch-8082     - http://localhost:8082
echo   [OK] StableMatch-8083     - http://localhost:8083
echo   [OK] API Gateway         - http://localhost:8080
if %DOCKER_AVAILABLE% equ 1 (
    echo   [OK] Prometheus          - http://localhost:9090
    echo   [OK] Grafana             - http://localhost:3000
)
echo.
echo Test the API:
echo   curl -X POST http://localhost:8080/api/stable-match/solve ^
echo     -H "Content-Type: application/json" ^
echo     -d "{\"students\":[{\"id\":\"s1\",\"name\":\"Student 1\",\"code\":\"S001\"}],\"courses\":[{\"id\":\"c1\",\"name\":\"Course 1\",\"code\":\"C001\",\"capacity\":10}]}"
echo.
echo Check Eureka Dashboard: http://localhost:8761
echo.
echo Press any key to exit (services will continue running)...
pause >nul