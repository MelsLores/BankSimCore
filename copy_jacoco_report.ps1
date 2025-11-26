# Script para copiar reporte JaCoCo a la carpeta static
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host " COPIANDO REPORTE JACOCO A WEB" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

$source = "target\jacoco\report"
$destination = "src\main\resources\static\jacoco-report"

if (Test-Path $source) {
    Write-Host "[1/2] Copiando archivos del reporte..." -ForegroundColor Yellow
    
    # Crear directorio destino si no existe
    if (Test-Path $destination) {
        Remove-Item -Recurse -Force $destination
    }
    
    Copy-Item -Recurse -Force $source $destination
    
    Write-Host "[2/2] Reporte copiado exitosamente`n" -ForegroundColor Green
    
    Write-Host "REPORTE DISPONIBLE EN:" -ForegroundColor Yellow
    Write-Host "  http://localhost:8080/jacoco-report/index.html" -ForegroundColor White
    Write-Host ""
    
    # Actualizar el enlace en tests-report.html
    Write-Host "Actualizando enlaces..." -ForegroundColor Yellow
    
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host " COMPLETADO - REPORTE ACCESIBLE VIA WEB" -ForegroundColor Green
    Write-Host "========================================`n" -ForegroundColor Cyan
    
} else {
    Write-Host "ERROR: No se encontro el reporte JaCoCo" -ForegroundColor Red
    Write-Host "Por favor ejecuta primero: .\run_tests_jacoco.ps1`n" -ForegroundColor Yellow
}
