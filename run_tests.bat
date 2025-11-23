@echo off
REM ============================================================================
REM Banking Application Simulator - Automated Test Runner (Batch Script)
REM Sprint 3 - Testing Procedures for Operational Issues
REM 
REM This script automatically compiles and runs the Banking Application
REM Simulator executing all 12 test cases from Sprint 2 documentation.
REM
REM Author: Jorge Pena - REM Consultancy
REM Date: November 2025
REM ============================================================================

SETLOCAL ENABLEDELAYEDEXPANSION

SET JAVA_FILE=BankingApplicationSimulator.java
SET CLASS_NAME=BankingApplicationSimulator
SET LOG_FILE=test_execution_log.txt

CLS
echo ===============================================================
echo    BANKING APPLICATION SIMULATOR - AUTOMATED TEST RUNNER
echo ===============================================================
echo.

REM Start logging
echo Test Execution Started: %date% %time% > %LOG_FILE%
echo. >> %LOG_FILE%

REM Step 1: Check Java installation
echo [Step 1/4] Checking Java installation...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo   ERROR: Java is not installed or not in PATH
    echo ERROR: Java not found >> %LOG_FILE%
    echo.
    echo Please install Java JDK and add it to your PATH
    pause
    exit /b 1
)
echo   SUCCESS: Java is installed
java -version 2>&1 | findstr "version" >> %LOG_FILE%
echo.

REM Step 2: Check source file
echo [Step 2/4] Checking source file...
if not exist %JAVA_FILE% (
    echo   ERROR: %JAVA_FILE% not found in current directory
    echo ERROR: Source file not found >> %LOG_FILE%
    pause
    exit /b 1
)
echo   SUCCESS: Found %JAVA_FILE%
echo Source file found: %JAVA_FILE% >> %LOG_FILE%
echo.

REM Step 3: Compile
echo [Step 3/4] Compiling Java source code...
javac %JAVA_FILE% 2>compile_errors.txt
if %ERRORLEVEL% NEQ 0 (
    echo   ERROR: Compilation failed
    type compile_errors.txt
    echo Compilation: FAILED >> %LOG_FILE%
    type compile_errors.txt >> %LOG_FILE%
    del compile_errors.txt
    pause
    exit /b 1
)
echo   SUCCESS: Compilation successful
echo Compilation: SUCCESS >> %LOG_FILE%
if exist compile_errors.txt del compile_errors.txt
echo.

REM Step 4: Execute tests
echo [Step 4/4] Executing automated test cases...
echo.
echo ===============================================================
echo                    TEST EXECUTION OUTPUT
echo ===============================================================
echo.

REM Create input file to skip interactive mode
echo N > input.txt

REM Run the program
java %CLASS_NAME% < input.txt

REM Save to log
echo. >> %LOG_FILE%
echo Test Execution Output: >> %LOG_FILE%
java %CLASS_NAME% < input.txt >> %LOG_FILE% 2>&1

REM Clean up
if exist input.txt del input.txt

echo.
echo ===============================================================
echo                    EXECUTION SUMMARY
echo ===============================================================
echo.
echo   Total Expected Test Cases: 12
echo   - Valid Cases (Should PASS): 4
echo   - Invalid Cases (Should FAIL): 8
echo.
echo   Log file saved to: %LOG_FILE%
echo.
echo ===============================================================
echo              Test Execution Completed Successfully
echo ===============================================================
echo.

REM End logging
echo Test Execution Completed: %date% %time% >> %LOG_FILE%

pause
