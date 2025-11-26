@echo off
REM Script para ejecutar tests con JaCoCo y generar reporte HTML

echo ========================================
echo  EJECUTANDO TESTS CON JACOCO
echo ========================================

REM Limpiar directorios
if exist "target\test-classes" rmdir /s /q "target\test-classes"
if exist "target\jacoco" rmdir /s /q "target\jacoco"
mkdir target\test-classes 2>nul
mkdir target\jacoco 2>nul

echo.
echo [1/5] Descargando dependencias necesarias...
echo.

REM Descargar JUnit y JaCoCo si no existen
if not exist "lib\junit-platform-console-standalone-1.10.1.jar" (
    echo Descargando JUnit Platform Console...
    curl -L -o lib\junit-platform-console-standalone-1.10.1.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar
)

if not exist "lib\jacoco-agent.jar" (
    echo Descargando JaCoCo Agent...
    curl -L -o lib\jacoco-agent.jar https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.11/org.jacoco.agent-0.8.11-runtime.jar
)

if not exist "lib\jacoco-cli.jar" (
    echo Descargando JaCoCo CLI...
    curl -L -o lib\jacoco-cli.jar https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.11/org.jacoco.cli-0.8.11-nodeps.jar
)

echo.
echo [2/5] Compilando codigo fuente...
javac -d bin -encoding UTF-8 src\main\java\com\banksim\util\ValidationUtil.java src\main\java\com\banksim\service\ValidationTestService.java

echo.
echo [3/5] Compilando tests...
javac -d target\test-classes -cp "bin;lib\junit-platform-console-standalone-1.10.1.jar" -encoding UTF-8 src\test\java\com\banksim\*.java

echo.
echo [4/5] Ejecutando tests con JaCoCo...
java -javaagent:lib\jacoco-agent.jar=destfile=target\jacoco\jacoco.exec -jar lib\junit-platform-console-standalone-1.10.1.jar --class-path "bin;target\test-classes" --scan-class-path --disable-banner

echo.
echo [5/5] Generando reporte HTML...
java -jar lib\jacoco-cli.jar report target\jacoco\jacoco.exec --classfiles bin --sourcefiles src\main\java --html target\jacoco\report --xml target\jacoco\jacoco.xml

echo.
echo ========================================
echo  TESTS COMPLETADOS CON EXITO
echo ========================================
echo.
echo Reporte HTML: target\jacoco\report\index.html
echo Reporte XML:  target\jacoco\jacoco.xml
echo.
echo Abriendo reporte...
start target\jacoco\report\index.html

pause
