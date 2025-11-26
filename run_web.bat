@echo off
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Banking Web Server - Compilation and Execution Script
:: Sprint 3 - Sistema Bancario con Frontend Web
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

cls
echo =================================================================
echo    BANKING WEB SERVER - COMPILACION E INICIO
echo =================================================================
echo.

:: Step 1: Check Java
echo [Step 1/5] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo   ERROR: Java not installed or not in PATH
    pause
    exit /b 1
)
echo   SUCCESS: Java installed
echo.

:: Step 2: No dependencies needed
echo [Step 2/5] Checking dependencies...
echo   SUCCESS: No external dependencies required!
echo.

:: Step 3: Check source files
echo [Step 3/5] Checking source files...
if not exist BankingWebServer.java (
    echo   ERROR: BankingWebServer.java not found!
    pause
    exit /b 1
)
if not exist BankingSystem.java (
    echo   ERROR: BankingSystem.java not found!
    pause
    exit /b 1
)
echo   SUCCESS: All files found!
echo.

:: Step 4: Compile
echo [Step 4/5] Compiling...
javac BankingWebServer.java BankingSystem.java
if errorlevel 1 (
    echo   ERROR: Compilation failed!
    pause
    exit /b 1
)
echo   SUCCESS: Compilation successful!
echo.

:: Step 5: Run server
echo [Step 5/5] Starting web server...
echo =================================================================
echo.

java BankingWebServer

echo.
echo =================================================================
echo               Web Server Terminated
echo =================================================================
pause
