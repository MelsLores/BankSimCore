###############################################################################
# Banking Application Simulator - Automated Test Runner
# Sprint 3 - Testing Procedures for Operational Issues
# 
# This script automatically compiles and runs the Banking Application Simulator
# executing all 12 test cases from the Sprint 2 documentation.
#
# Author: Jorge Pena - REM Consultancy
# Date: November 2025
###############################################################################

# Configuration
$JAVA_FILE = "BankingApplicationSimulator.java"
$CLASS_NAME = "BankingApplicationSimulator"
$LOG_FILE = "test_execution_log.txt"

# Colors for output
$SUCCESS_COLOR = "Green"
$ERROR_COLOR = "Red"
$INFO_COLOR = "Cyan"
$WARNING_COLOR = "Yellow"

# Clear screen
Clear-Host

Write-Host "===============================================================" -ForegroundColor $INFO_COLOR
Write-Host "   BANKING APPLICATION SIMULATOR - AUTOMATED TEST RUNNER      " -ForegroundColor $INFO_COLOR
Write-Host "===============================================================" -ForegroundColor $INFO_COLOR
Write-Host ""

# Start logging
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
"Test Execution Started: $timestamp" | Out-File -FilePath $LOG_FILE -Encoding UTF8

# Step 1: Check if Java is installed
Write-Host "[Step 1/4] Checking Java installation..." -ForegroundColor $INFO_COLOR
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "  SUCCESS: Java is installed - $javaVersion" -ForegroundColor $SUCCESS_COLOR
    "Java Version: $javaVersion" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
}
catch {
    Write-Host "  ERROR: Java is not installed or not in PATH" -ForegroundColor $ERROR_COLOR
    "ERROR: Java not found" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
    Write-Host ""
    Write-Host "Please install Java JDK and add it to your PATH" -ForegroundColor $WARNING_COLOR
    exit 1
}
Write-Host ""

# Step 2: Check if source file exists
Write-Host "[Step 2/4] Checking source file..." -ForegroundColor $INFO_COLOR
if (Test-Path $JAVA_FILE) {
    Write-Host "  SUCCESS: Found $JAVA_FILE" -ForegroundColor $SUCCESS_COLOR
    "Source file found: $JAVA_FILE" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
}
else {
    Write-Host "  ERROR: $JAVA_FILE not found in current directory" -ForegroundColor $ERROR_COLOR
    "ERROR: Source file not found" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
    exit 1
}
Write-Host ""

# Step 3: Compile the Java file
Write-Host "[Step 3/4] Compiling Java source code..." -ForegroundColor $INFO_COLOR
$compileOutput = javac $JAVA_FILE 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "  SUCCESS: Compilation successful" -ForegroundColor $SUCCESS_COLOR
    "Compilation: SUCCESS" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
}
else {
    Write-Host "  ERROR: Compilation failed" -ForegroundColor $ERROR_COLOR
    Write-Host "  $compileOutput" -ForegroundColor $ERROR_COLOR
    "Compilation: FAILED" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
    "$compileOutput" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
    exit 1
}
Write-Host ""

# Step 4: Execute the test cases
Write-Host "[Step 4/4] Executing automated test cases..." -ForegroundColor $INFO_COLOR
Write-Host ""
Write-Host "===============================================================" -ForegroundColor $INFO_COLOR
Write-Host "                    TEST EXECUTION OUTPUT                      " -ForegroundColor $INFO_COLOR
Write-Host "===============================================================" -ForegroundColor $INFO_COLOR
Write-Host ""

# Create input file to automatically answer "N" to interactive mode
"N" | Out-File -FilePath "input.txt" -Encoding ASCII

# Run the Java program with input redirection
$executionOutput = Get-Content "input.txt" | java $CLASS_NAME 2>&1

# Display the output
$executionOutput | ForEach-Object {
    $line = $_
    if ($line -match "SUCCESS") {
        Write-Host $line -ForegroundColor $SUCCESS_COLOR
    }
    elseif ($line -match "FAILURE|ERROR|Error") {
        Write-Host $line -ForegroundColor $ERROR_COLOR
    }
    elseif ($line -match "TC\d+|Test ID") {
        Write-Host $line -ForegroundColor $WARNING_COLOR
    }
    elseif ($line -match "===|---") {
        Write-Host $line -ForegroundColor $INFO_COLOR
    }
    else {
        Write-Host $line
    }
}

# Save execution output to log
"" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
"Test Execution Output:" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
$executionOutput | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8

# Clean up temporary files
if (Test-Path "input.txt") {
    Remove-Item "input.txt" -Force
}

# Summary
Write-Host ""
Write-Host "===============================================================" -ForegroundColor $INFO_COLOR
Write-Host "                    EXECUTION SUMMARY                          " -ForegroundColor $INFO_COLOR
Write-Host "===============================================================" -ForegroundColor $INFO_COLOR

# Count test results
$successCount = ($executionOutput | Select-String -Pattern "Status: SUCCESS" -AllMatches).Matches.Count
$failureCount = ($executionOutput | Select-String -Pattern "Status: FAILURE" -AllMatches).Matches.Count
$totalTests = $successCount + $failureCount

Write-Host ""
Write-Host "  Total Test Cases Executed: $totalTests" -ForegroundColor $INFO_COLOR
Write-Host "  Passed (Valid Cases): $successCount" -ForegroundColor $SUCCESS_COLOR
Write-Host "  Failed (Invalid Cases): $failureCount" -ForegroundColor $ERROR_COLOR
Write-Host ""
Write-Host "  Expected Results:" -ForegroundColor $INFO_COLOR
Write-Host "    - Valid Cases (TC01, TC07, TC08, TC12): 4 should PASS" -ForegroundColor $SUCCESS_COLOR
Write-Host "    - Invalid Cases (TC02-TC06, TC09-TC11): 8 should FAIL" -ForegroundColor $ERROR_COLOR
Write-Host ""

# Validate results
if ($successCount -eq 4 -and $failureCount -eq 8) {
    Write-Host "  RESULT: ALL TESTS COMPLETED AS EXPECTED!" -ForegroundColor $SUCCESS_COLOR
    "RESULT: ALL TESTS PASSED" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
}
else {
    Write-Host "  WARNING: Test results differ from expected!" -ForegroundColor $WARNING_COLOR
    "WARNING: Unexpected test results" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
}

Write-Host ""
Write-Host "  Log file saved to: $LOG_FILE" -ForegroundColor $INFO_COLOR
Write-Host ""

# End timestamp
$endTimestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
"Test Execution Completed: $endTimestamp" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8

Write-Host "===============================================================" -ForegroundColor $INFO_COLOR
Write-Host "              Test Execution Completed Successfully            " -ForegroundColor $INFO_COLOR
Write-Host "===============================================================" -ForegroundColor $INFO_COLOR
Write-Host ""
