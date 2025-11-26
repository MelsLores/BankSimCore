@echo off
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Banking System - Automated Compilation and Execution
:: Sprint 3 - Complete Banking System with Automated Tests
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

cls
echo =================================================================
echo    BANKING SYSTEM - AUTOMATED COMPILATION AND EXECUTION
echo =================================================================
echo.

:: Step 1: Check Java
echo [Step 1/4] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo   ERROR: Java not installed or not in PATH
    pause
    exit /b 1
)
echo   SUCCESS: Java installed
echo.

:: Step 2: Check files
echo [Step 2/4] Checking source file...
if not exist BankingSystem.java (
    echo   ERROR: BankingSystem.java not found!
    pause
    exit /b 1
)
echo   SUCCESS: File found!
echo.

:: Step 3: Compile
echo [Step 3/4] Compiling...
javac BankingSystem.java
if errorlevel 1 (
    echo   ERROR: Compilation failed!
    pause
    exit /b 1
)
echo   SUCCESS: Compilation successful!
echo.

:: Step 4: Run
echo [Step 4/4] Starting Banking System...
echo =================================================================
echo.

java BankingSystem

echo.
echo =================================================================
echo               Banking System Terminated
echo =================================================================
pause
