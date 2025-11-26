###############################################################################
# Banking Web Server - Compilation and Execution Script
# Sprint 3 - Sistema Bancario con Frontend Web
###############################################################################

Clear-Host

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "   BANKING WEB SERVER - COMPILACION E INICIO                     " -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Check Java
Write-Host "[Step 1/5] Checking Java installation..." -ForegroundColor Cyan
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "  SUCCESS: $javaVersion" -ForegroundColor Green
}
catch {
    Write-Host "  ERROR: Java not installed or not in PATH" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 2: No dependencies needed
Write-Host "[Step 2/5] Checking dependencies..." -ForegroundColor Cyan
Write-Host "  SUCCESS: No external dependencies required!" -ForegroundColor Green
Write-Host ""

# Step 3: Check source files
Write-Host "[Step 3/5] Checking source files..." -ForegroundColor Cyan
if (-Not (Test-Path "BankingWebServer.java")) {
    Write-Host "  ERROR: BankingWebServer.java not found!" -ForegroundColor Red
    exit 1
}
if (-Not (Test-Path "BankingSystem.java")) {
    Write-Host "  ERROR: BankingSystem.java not found!" -ForegroundColor Red
    exit 1
}
Write-Host "  SUCCESS: All files found!" -ForegroundColor Green
Write-Host ""

# Step 4: Compile
Write-Host "[Step 4/5] Compiling..." -ForegroundColor Cyan
javac BankingWebServer.java BankingSystem.java 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "  ERROR: Compilation failed!" -ForegroundColor Red
    javac BankingWebServer.java BankingSystem.java
    exit 1
}
Write-Host "  SUCCESS: Compilation successful!" -ForegroundColor Green
Write-Host ""

# Step 5: Run server
Write-Host "[Step 5/5] Starting web server..." -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Yellow
Write-Host ""

java BankingWebServer

Write-Host ""
Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "              Web Server Terminated                              " -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
