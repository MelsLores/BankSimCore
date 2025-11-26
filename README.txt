###############################################################################
# SISTEMA BANCARIO COMPLETO - SPRINT 3
# Testing de Aplicacion Bancaria
# VERSION 4.0 - CON FRONTEND WEB
###############################################################################

PROYECTO: Sistema Bancario con Testing Automatizado y Interfaz Web
AUTOR: Jorge Pena - REM Consultancy
FECHA: Noviembre 2025

===============================================================================
DESCRIPCION DEL SISTEMA
===============================================================================

** NUEVO: INTERFAZ WEB MODERNA **

Este sistema bancario ahora incluye una interfaz web profesional con:
- Frontend HTML5/CSS3/JavaScript moderno
- Diseño responsive con gradientes y animaciones
- API REST para todas las operaciones
- Servidor HTTP integrado en Java
- Dashboard interactivo en tiempo real
- Ejecución de tests desde el navegador

FUNCIONALIDADES:

1. GESTION DE CLIENTES
   - Creacion de clientes con validacion
   - Vista de todos los clientes registrados
   - Formularios interactivos

2. GESTION DE CUENTAS
   - Creacion de cuentas bancarias
   - Formato: XXX-XXXX-XXXXXXXXXX (Banco-Sucursal-Cuenta)
   - Validacion en tiempo real

3. OPERACIONES BANCARIAS
   - Depositos con confirmacion instantanea
   - Retiros con validacion de saldo
   - Transferencias entre cuentas
   - Consulta de saldo en tiempo real

4. SOLICITUDES DE SERVICIOS
   - Solicitud de chequera
   - Generacion de estado de cuenta
   - Autenticacion con clave personal

5. TESTS AUTOMATIZADOS
   - 12 casos de prueba del PDF
   - Ejecucion desde interfaz web
   - Resultados visuales coloreados
   - Resumen automático

===============================================================================
INSTRUCCIONES DE USO - INTERFAZ WEB (RECOMENDADO)
===============================================================================

OPCION 1: Usar Scripts Automatizados (MAS FACIL)
------------------------------------------------

Windows PowerShell:
  .\run_web.ps1

Windows Command Prompt:
  run_web.bat

Los scripts automaticamente:
1. Verifican instalacion de Java
2. Descargan libreria Gson (si no existe)
3. Compilan BankingWebServer.java y BankingSystem.java
4. Inician el servidor en http://localhost:8080

Una vez iniciado el servidor:
- Abrir navegador en: http://localhost:8080
- La interfaz web se carga automaticamente
- Navegar por las pestanas para usar el sistema


OPCION 2: Compilacion y Ejecucion Manual
----------------------------------------

1. Descargar Gson manualmente (si no existe):
   https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
   Guardar como: gson-2.10.1.jar

2. Compilar:
   javac -cp gson-2.10.1.jar BankingWebServer.java BankingSystem.java

3. Ejecutar servidor:
   java -cp ".;gson-2.10.1.jar" BankingWebServer

4. Abrir navegador en:
   http://localhost:8080


===============================================================================
INTERFAZ DE CONSOLA (VERSION CLASICA)
===============================================================================

Para usar la version de consola tradicional:

Windows PowerShell:
  .\run_system.ps1

Windows Command Prompt:
  run_system.bat

O manualmente:
  javac BankingSystem.java
  java BankingSystem


===============================================================================
NAVEGACION EN LA INTERFAZ WEB
===============================================================================

TAB 1: DASHBOARD
- Resumen del banco (clientes, cuentas, saldo total)
- Tabla de todas las cuentas registradas
- Actualizacion automatica

TAB 2: CLIENTES
- Formulario para crear nuevos clientes
- Lista de clientes registrados
- Validacion de clave personal (min 8 alfanumericos)

TAB 3: CUENTAS
- Formulario para crear nuevas cuentas
- Validacion de codigos (3-4-10 digitos)
- Asociacion con clientes existentes

TAB 4: OPERACIONES
- 4 operaciones principales en cards:
  * Deposito
  * Retiro
  * Transferencia
  * Consulta de saldo
- Confirmacion instantanea
- Actualizacion de saldos en tiempo real

TAB 5: SERVICIOS
- Solicitud de chequera
- Generacion de estado de cuenta
- Autenticacion con clave personal
- Vista previa de estados de cuenta

TAB 6: TESTS AUTOMATIZADOS
- Boton para ejecutar los 12 tests del PDF
- Resultados coloreados (verde=OK, rojo=error)
- Detalles de cada test
- Resumen final con estadisticas


===============================================================================
DATOS DE EJEMPLO
===============================================================================

El sistema se inicializa con datos de ejemplo para facilitar las pruebas:

Cliente:
  ID: CUST001
  Nombre: Juan Perez
  Clave Personal: Abcd1234
  Email: juan.perez@email.com
  Telefono: 555-1234

Cuenta:
  Numero: 123-4567-1234567890
  Titular: Juan Perez
  Saldo Inicial: $1000.00

Puede usar estos datos para probar inmediatamente.


===============================================================================
CASOS DE PRUEBA (PDF)
===============================================================================

CASOS VALIDOS (Deben PASAR):
-----------------------------
TC01: 123-4567-1234567890, Abcd1234, Checkbook
TC02: 999-9999-9999999999, 12345678, Statement
TC03: 456-7890-5678901234, Pass1234, Checkbook
TC04: 111-2222-3333333333, Secure99, Statement

CASOS INVALIDOS (Deben FALLAR):
--------------------------------
TC05: 12-4567-1234567890 (Banco: 2 digitos)
TC06: 123-456-1234567890 (Sucursal: 3 digitos)
TC07: 123-4567-123456789 (Cuenta: 9 digitos)
TC08: 123-4567-1234567890, Short1 (Clave: 6 caracteres)
TC09: 123-4567-1234567890, Abcd1234, CreditCard (Orden invalida)
TC10: 12A-4567-1234567890 (Banco: letra)
TC11: 123-45BC-1234567890 (Sucursal: letras)
TC12: 123-4567-12345ABCDE (Cuenta: letras)


===============================================================================
ARQUITECTURA TECNICA
===============================================================================

BACKEND:
- Java HttpServer (com.sun.net.httpserver)
- API REST con endpoints JSON
- Puerto: 8080
- CORS habilitado para desarrollo
- Libreria Gson 2.10.1 para JSON parsing

FRONTEND:
- HTML5 semantico
- CSS3 con gradientes y animaciones
- JavaScript vanilla (sin frameworks)
- Fetch API para peticiones HTTP
- Diseño responsive (mobile-friendly)

ENDPOINTS API:
- GET  /api/customers      - Lista de clientes
- POST /api/customers      - Crear cliente
- GET  /api/accounts       - Lista de cuentas
- POST /api/accounts       - Crear cuenta
- POST /api/deposit        - Realizar deposito
- POST /api/withdraw       - Realizar retiro
- POST /api/transfer       - Transferencia
- GET  /api/balance        - Consultar saldo
- POST /api/statement      - Estado de cuenta
- POST /api/checkbook      - Solicitar chequera
- GET  /api/summary        - Resumen del banco
- GET  /api/tests          - Ejecutar tests


===============================================================================
REQUISITOS DEL SISTEMA
===============================================================================

- Java JDK 8 o superior
- Navegador web moderno (Chrome, Firefox, Edge, Safari)
- Conexion a internet (solo para descargar Gson la primera vez)
- Puerto 8080 disponible
- 100 MB de espacio en disco


===============================================================================
RESOLUCION DE PROBLEMAS
===============================================================================

Error: "Bind failed: Address already in use"
  Solucion: El puerto 8080 esta ocupado. Cerrar otras aplicaciones o
            cambiar PORT en BankingWebServer.java

Error: "Class not found: com.google.gson.Gson"
  Solucion: Descargar gson-2.10.1.jar manualmente y colocarlo en la
            carpeta del proyecto

Error: "No se puede cargar la pagina"
  Solucion: Verificar que el servidor este corriendo y acceder a
            http://localhost:8080 (no https)

Error: "404 Not Found en archivos frontend"
  Solucion: Verificar que exista la carpeta frontend/ con los archivos
            index.html, styles.css y app.js

Error: "CORS error en consola del navegador"
  Solucion: El servidor ya tiene CORS habilitado. Verificar que se
            acceda desde localhost:8080


===============================================================================
CARACTERISTICAS DESTACADAS
===============================================================================

VISUAL:
- Gradientes morados/azules profesionales
- Tarjetas con sombras y hover effects
- Formularios con validacion visual
- Notificaciones toast animadas
- Tablas responsive con scroll

USABILIDAD:
- Navegacion por pestanas intuitiva
- Formularios que se limpian automaticamente
- Confirmaciones inmediatas
- Mensajes de error descriptivos
- Dashboard actualizado en tiempo real

TESTING:
- Ejecucion de tests con un click
- Resultados visuales coloreados
- Detalles expandidos de cada caso
- Resumen con estadisticas
- Validacion automatica de resultados esperados


===============================================================================
NOTAS ADICIONALES
===============================================================================

- El servidor web NO requiere instalacion de servidores externos
- Toda la logica esta en Java puro
- Los datos se almacenan en memoria (se pierden al cerrar el servidor)
- Para produccion, agregar persistencia en base de datos
- El frontend es completamente estatico (HTML/CSS/JS)
- Compatible con todos los navegadores modernos


===============================================================================
CONTACTO Y SOPORTE
===============================================================================

Desarrollador: Jorge Pena
Empresa: REM Consultancy
Proyecto: Sprint 3 - Testing de Aplicacion Bancaria
Version: 4.0 - Con Frontend Web
Fecha: Noviembre 2025

===============================================================================
FIN DEL DOCUMENTO
===============================================================================
