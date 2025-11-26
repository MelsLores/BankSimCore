# Script PowerShell para ejecutar tests con JaCoCo
Write-Host "`n=======================================" -ForegroundColor Cyan
Write-Host " EJECUTANDO TESTS CON JACOCO" -ForegroundColor Green
Write-Host "=======================================`n" -ForegroundColor Cyan

# Crear directorios
Write-Host "[1/6] Preparando directorios..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path "target\test-classes" | Out-Null
New-Item -ItemType Directory -Force -Path "target\jacoco\report" | Out-Null

# Descargar dependencias si no existen
Write-Host "`n[2/6] Verificando dependencias JUnit y JaCoCo..." -ForegroundColor Yellow

$junitJar = "lib\junit-platform-console-standalone-1.10.1.jar"
$jacocoAgent = "lib\jacocoagent.jar"
$jacocoCli = "lib\jacococli.jar"
$assertJJar = "lib\assertj-core-3.24.2.jar"

if (-not (Test-Path $junitJar)) {
    Write-Host "Descargando JUnit Platform Console..." -ForegroundColor Gray
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar" -OutFile $junitJar
}

if (-not (Test-Path $jacocoAgent)) {
    Write-Host "Descargando JaCoCo Agent..." -ForegroundColor Gray
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.11/org.jacoco.agent-0.8.11-runtime.jar" -OutFile $jacocoAgent
}

if (-not (Test-Path $jacocoCli)) {
    Write-Host "Descargando JaCoCo CLI..." -ForegroundColor Gray
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.11/org.jacoco.cli-0.8.11-nodeps.jar" -OutFile $jacocoCli
}

if (-not (Test-Path $assertJJar)) {
    Write-Host "Descargando AssertJ..." -ForegroundColor Gray
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/assertj/assertj-core/3.24.2/assertj-core-3.24.2.jar" -OutFile $assertJJar
}

# Compilar código fuente
Write-Host "`n[3/6] Compilando codigo fuente..." -ForegroundColor Yellow
javac -d bin -encoding UTF-8 src\main\java\com\banksim\util\ValidationUtilSimple.java

# Copiar ValidationUtilSimple como ValidationUtil para tests
Copy-Item "bin\com\banksim\util\ValidationUtilSimple.class" "bin\com\banksim\util\ValidationUtil.class" -Force

javac -d bin -cp bin -encoding UTF-8 src\main\java\com\banksim\service\ValidationTestService.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "`nERROR: Fallo la compilacion del codigo fuente" -ForegroundColor Red
    exit 1
}

# Compilar tests
Write-Host "`n[4/6] Compilando tests..." -ForegroundColor Yellow
javac -d target\test-classes -cp "bin;$junitJar;$assertJJar" -encoding UTF-8 src\test\java\com\banksim\*.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "`nERROR: Fallo la compilacion de los tests" -ForegroundColor Red
    exit 1
}

# Ejecutar tests con JaCoCo
Write-Host "`n[5/6] Ejecutando tests con JaCoCo..." -ForegroundColor Yellow
Write-Host "---------------------------------------" -ForegroundColor Gray
java -javaagent:$jacocoAgent=destfile=target\jacoco\jacoco.exec -jar $junitJar --class-path "bin;target\test-classes;$assertJJar" --scan-class-path

$testExitCode = $LASTEXITCODE

# Generar reporte HTML
Write-Host "`n[6/6] Generando reporte HTML..." -ForegroundColor Yellow
java -jar $jacocoCli report target\jacoco\jacoco.exec --classfiles bin --sourcefiles src\main\java --html target\jacoco\report --xml target\jacoco\jacoco.xml --csv target\jacoco\jacoco.csv

if ($LASTEXITCODE -eq 0) {
    # Copiar reporte a carpeta static para acceso web
    Write-Host "`n[Extra] Copiando reporte a carpeta web..." -ForegroundColor Yellow
    if (Test-Path "src\main\resources\static\jacoco-report") {
        Remove-Item -Recurse -Force "src\main\resources\static\jacoco-report"
    }
    Copy-Item -Recurse -Force "target\jacoco\report" "src\main\resources\static\jacoco-report"
    
    Write-Host "`n=======================================" -ForegroundColor Cyan
    Write-Host " TESTS COMPLETADOS" -ForegroundColor Green
    Write-Host "=======================================`n" -ForegroundColor Cyan
    
    Write-Host "REPORTES GENERADOS:" -ForegroundColor Yellow
    Write-Host "  HTML Local: " -NoNewline; Write-Host "target\jacoco\report\index.html" -ForegroundColor White
    Write-Host "  HTML Web:   " -NoNewline; Write-Host "http://localhost:8080/jacoco-report/index.html" -ForegroundColor Cyan
    Write-Host "  Reporte UI: " -NoNewline; Write-Host "http://localhost:8080/tests-report.html" -ForegroundColor Cyan
    Write-Host "  XML:        " -NoNewline; Write-Host "target\jacoco\jacoco.xml" -ForegroundColor White
    Write-Host "  CSV:        " -NoNewline; Write-Host "target\jacoco\jacoco.csv" -ForegroundColor White
    
    Write-Host "`nRESULTADO DE LOS TESTS:" -ForegroundColor Yellow
    if ($testExitCode -eq 0) {
        Write-Host "  TODOS LOS TESTS PASARON" -ForegroundColor Green
    } else {
        Write-Host "  ALGUNOS TESTS FALLARON (Ver detalles arriba)" -ForegroundColor Red
    }
    
    Write-Host "`nAbriendo reporte..." -ForegroundColor Cyan
    Start-Process "http://localhost:8080/tests-report.html"
} else {
    Write-Host "`nERROR: Fallo la generacion del reporte" -ForegroundColor Red
}

Write-Host ""
