###############################################################################
# Test rapido de compilacion
###############################################################################

Write-Host "Probando compilacion..." -ForegroundColor Cyan

# Compilar BankingSystem
Write-Host "`n[1/2] Compilando BankingSystem..." -ForegroundColor Yellow
javac BankingSystem.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "  SUCCESS!" -ForegroundColor Green
} else {
    Write-Host "  FAILED!" -ForegroundColor Red
    exit 1
}

# Compilar BankingWebServer
Write-Host "`n[2/2] Compilando BankingWebServer..." -ForegroundColor Yellow
javac BankingWebServer.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "  SUCCESS!" -ForegroundColor Green
} else {
    Write-Host "  FAILED!" -ForegroundColor Red
    exit 1
}

Write-Host "`n================================================================" -ForegroundColor Green
Write-Host "COMPILACION EXITOSA!" -ForegroundColor Green
Write-Host "================================================================" -ForegroundColor Green
Write-Host "`nOpciones para ejecutar:" -ForegroundColor Cyan
Write-Host "  1. Interfaz Web:  .\run_web.ps1" -ForegroundColor White
Write-Host "  2. Consola:       .\run_system.ps1" -ForegroundColor White
Write-Host ""
