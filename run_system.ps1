###############################################################################
# Banking System - Automated Compilation and Execution
# Sprint 3 - Complete Banking System with Automated Tests
# 
# This script compiles all Java classes and runs the complete banking system
#
###############################################################################

# Configuration
$MAIN_CLASS = "BankingSystem"
$SOURCE_FILE = "BankingSystem.java"

# Clear screen
Clear-Host

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "   BANKING SYSTEM - AUTOMATED COMPILATION AND EXECUTION         " -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Check Java installation
Write-Host "[Step 1/4] Checking Java installation..." -ForegroundColor Cyan
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "  SUCCESS: $javaVersion" -ForegroundColor Green
}
catch {
    Write-Host "  ERROR: Java not installed or not in PATH" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 2: Check source files
Write-Host "[Step 2/4] Checking source file..." -ForegroundColor Cyan
if (-Not (Test-Path $SOURCE_FILE)) {
    Write-Host "  ERROR: $SOURCE_FILE not found!" -ForegroundColor Red
    exit 1
}
Write-Host "  SUCCESS: File found!" -ForegroundColor Green
Write-Host ""

# Step 3: Compile
Write-Host "[Step 3/4] Compiling..." -ForegroundColor Cyan
javac $SOURCE_FILE 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "  ERROR: Compilation failed!" -ForegroundColor Red
    javac $SOURCE_FILE
    exit 1
}
Write-Host "  SUCCESS: Compilation successful!" -ForegroundColor Green
Write-Host ""

# Step 4: Run
Write-Host "[Step 4/4] Starting Banking System..." -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Yellow
Write-Host ""

java $MAIN_CLASS

Write-Host ""
Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "              Banking System Terminated                          " -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
