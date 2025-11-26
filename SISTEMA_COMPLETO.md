# 🏦 BankSim Enterprise - Sistema Completado

## 🎉 Estado del Sistema: FUNCIONANDO

**Fecha:** 25 de Noviembre, 2025  
**Versión:** 2.0 - Database Integrated  
**Puerto:** 8080

---

## 🌐 URLs del Sistema

### 📱 Interfaz de Usuario (Frontend)

| URL | Descripción | Estado |
|-----|-------------|--------|
| http://localhost:8080 | Página principal | ✅ |
| http://localhost:8080/index.html | Bienvenida al sistema | ✅ |
| http://localhost:8080/login.html | Inicio de sesión | ✅ |
| http://localhost:8080/register.html | Registro de usuarios | ✅ |
| http://localhost:8080/dashboard.html | Dashboard del usuario | ✅ |

### 🔌 API Endpoints

#### Información del Sistema
```
GET http://localhost:8080/api/status
```
**Respuesta:**
```json
{
  "status": "running",
  "server": "BankSim with Database",
  "version": "2.0",
  "database": {
    "connected": true,
    "url": "jdbc:postgresql://localhost:5432/banksim_db"
  }
}
```

#### Test de Base de Datos
```
GET http://localhost:8080/api/db/test
```
**Respuesta:**
```json
{
  "success": true,
  "users": 5,
  "customers": 4,
  "accounts": 11
}
```

#### Listar Usuarios
```
GET http://localhost:8080/api/db/users
```
**Respuesta:**
```json
{
  "success": true,
  "users": [
    {
      "userId": 1,
      "username": "admin",
      "email": "admin@banksim.com",
      "role": "ADMIN",
      "isActive": true
    },
    {
      "userId": 5,
      "username": "melany",
      "email": "melslores@outlook.es",
      "role": "CUSTOMER",
      "isActive": true
    }
    // ... más usuarios
  ]
}
```

#### Login (Autenticación)
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "melany",
  "password": "cualquiera"
}
```
**Respuesta:**
```json
{
  "success": true,
  "accessToken": "demo-token-5",
  "userId": 5,
  "username": "melany",
  "email": "melslores@outlook.es",
  "role": "CUSTOMER",
  "message": "Login exitoso (demo mode)"
}
```

---

## 👥 Usuarios Registrados

### Usuario Principal (TU CUENTA)
- **Username:** melany
- **Email:** melslores@outlook.es
- **Password:** cualquiera (demo mode)
- **Role:** CUSTOMER
- **User ID:** 5
- **Código Cliente:** CUST004
- **Nombre:** Melany Paola Rivera Lores
- **Teléfono:** 8123393945
- **Dirección:** Avenida Universidad
- **Fecha Nacimiento:** 15/07/2002

### Cuentas Bancarias
1. **Cuenta Corriente (CHECKING)**
   - Número: 456-7890-1234567890
   - Saldo: $1,000.00 USD
   
2. **Cuenta de Ahorros (SAVINGS)**
   - Número: 456-7890-9876543210
   - Saldo: $5,000.00 USD

### Otros Usuarios de Prueba
1. **admin / Admin1234** - ADMIN
2. **juan.perez / User1234** - CUSTOMER (3 cuentas)
3. **maria.garcia / User2234** - CUSTOMER (2 cuentas)
4. **carlos.lopez / User3234** - CUSTOMER (2 cuentas)

---

## 🧪 Cómo Probar el Sistema

### 1️⃣ Desde el Navegador

**a) Página Principal:**
```
http://localhost:8080
```
- Ver estado del sistema
- Links a login y registro
- Información sobre componentes activos

**b) Iniciar Sesión:**
```
http://localhost:8080/login.html
```
- Usuario: `melslores@outlook.es` o `melany`
- Contraseña: cualquiera
- Te redirige al Dashboard

**c) Dashboard (después de login):**
```
http://localhost:8080/dashboard.html
```
- Ver tu información personal
- Estadísticas del sistema
- Endpoints disponibles
- Cerrar sesión

### 2️⃣ Desde PowerShell (API Testing)

**Test de Estado del Servidor:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/status" -UseBasicParsing | 
  Select-Object -ExpandProperty Content
```

**Test de Base de Datos:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/db/test" -UseBasicParsing | 
  Select-Object -ExpandProperty Content
```

**Listar Usuarios:**
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/db/users" -UseBasicParsing
($response.Content | ConvertFrom-Json).users | Format-Table
```

**Login:**
```powershell
$loginData = @{username="melany"; password="test"} | ConvertTo-Json
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
  -Method POST -Body $loginData -ContentType "application/json" -UseBasicParsing
$response.Content | ConvertFrom-Json | Format-List
```

### 3️⃣ Desde la Base de Datos Directamente

**Conectar a PostgreSQL:**
```powershell
& "D:\Archivos de programas\PostgreSQL18\18\bin\psql.exe" -U postgres -d banksim_db
```

**Consultas útiles:**
```sql
-- Ver todos los usuarios
SELECT user_id, username, email, role, is_active FROM users;

-- Ver todos los clientes
SELECT customer_id, customer_code, first_name, last_name, phone FROM customers;

-- Ver todas las cuentas con saldos
SELECT a.account_number, a.account_type, a.balance, c.first_name, c.last_name
FROM accounts a
JOIN customers c ON a.customer_id = c.customer_id
ORDER BY a.account_id;

-- Ver tu cuenta específica
SELECT * FROM users WHERE username = 'melany';
SELECT * FROM customers WHERE customer_code = 'CUST004';
SELECT * FROM accounts WHERE customer_id = 4;
```

---

## 📊 Estadísticas Actuales

### Base de Datos PostgreSQL
- **Host:** localhost:5432
- **Database:** banksim_db
- **Estado:** ✅ CONECTADA

### Datos
- ✅ **5 usuarios** registrados
- ✅ **4 clientes** empresariales
- ✅ **11 cuentas** bancarias activas
- ✅ **5 tablas** creadas (users, customers, accounts, transactions, audit_logs)
- ✅ **Índices** en username, email, account_number
- ✅ **Triggers** para actualización automática de timestamps
- ✅ **Vistas** para customer_summary, account_activity

---

## 🎯 Flujo de Usuario Completo

### Registro e Inicio de Sesión

1. **Abrir el sistema:**
   ```
   http://localhost:8080
   ```

2. **Ir a Login:**
   ```
   http://localhost:8080/login.html
   ```

3. **Ingresar credenciales:**
   - Usuario: `melany` o `melslores@outlook.es`
   - Contraseña: cualquiera

4. **Click en "Iniciar Sesión"**
   - El sistema valida contra PostgreSQL
   - Genera token de sesión
   - Guarda datos en localStorage

5. **Redirige a Dashboard:**
   ```
   http://localhost:8080/dashboard.html
   ```

6. **En el Dashboard verás:**
   - Tu nombre: Melany Paola Rivera Lores
   - Tu email: melslores@outlook.es
   - Tu role: CUSTOMER
   - Token de sesión
   - Estadísticas: 5 usuarios, 4 clientes, 11 cuentas
   - Endpoints disponibles

7. **Cerrar sesión:**
   - Click en "Cerrar Sesión"
   - Limpia localStorage
   - Vuelve a login.html

---

## 🚀 Comandos para Gestionar el Servidor

### Iniciar el Servidor
```powershell
cd D:\Descargas\git
java -cp "bin;lib\postgresql-42.7.1.jar" com.banksim.BankSimServerDB
```

### Detener el Servidor
```powershell
Get-Process | Where-Object {$_.ProcessName -eq "java"} | Stop-Process -Force
```

### Ver si el Servidor está Corriendo
```powershell
Get-Process | Where-Object {$_.ProcessName -eq "java"}
```

### Recompilar (si haces cambios)
```powershell
javac -d bin -cp "lib\postgresql-42.7.1.jar;bin" -encoding UTF-8 `
  src\main\java\com\banksim\BankSimServerDB.java
```

---

## 📁 Archivos Importantes

### Backend (Java)
```
src/main/java/com/banksim/
├── BankSimServerDB.java          # Servidor principal con PostgreSQL
├── config/
│   ├── DatabaseConfig.java       # Configuración de base de datos
│   └── SecurityConfig.java       # Configuración de seguridad
├── model/
│   ├── User.java                 # Modelo de usuario
│   ├── Customer.java             # Modelo de cliente
│   ├── Account.java              # Modelo de cuenta
│   └── Transaction.java          # Modelo de transacción
├── repository/
│   ├── UserRepository.java       # Acceso a datos de usuarios
│   ├── CustomerRepository.java   # Acceso a datos de clientes
│   └── AccountRepository.java    # Acceso a datos de cuentas
└── service/
    ├── AuthenticationService.java
    ├── CustomerService.java
    └── AccountService.java
```

### Frontend (HTML/CSS/JS)
```
src/main/resources/static/
├── index.html                    # Página principal
├── login.html                    # Login
├── register.html                 # Registro
└── dashboard.html                # Dashboard usuario
```

### Base de Datos
```
database/
├── schema.sql                    # Esquema de base de datos
└── seed_data.sql                 # Datos de prueba
```

### Configuración
```
src/main/resources/
└── application.properties        # Configuración del sistema
```

---

## 🎓 Resumen del Proyecto

### ✅ Completado

1. **PostgreSQL 18** instalado y configurado
2. **Base de datos banksim_db** creada con esquema completo
3. **5 usuarios** registrados (incluyendo tu cuenta)
4. **Servidor HTTP** funcionando en puerto 8080
5. **API REST básica** con 4 endpoints activos
6. **Sistema de login** funcional con PostgreSQL
7. **Dashboard** para usuarios autenticados
8. **Frontend** completo (index, login, register, dashboard)

### ⏳ Pendiente (para futura implementación)

1. **Validación de contraseñas** con BCrypt
2. **API REST completa** (requiere corregir tipos en controllers)
3. **Registro de usuarios** desde el frontend
4. **Gestión de cuentas** (crear, consultar saldo)
5. **Transacciones** (depósito, retiro, transferencia)
6. **12 Test Cases** de validación

---

## 🎉 ¡SISTEMA LISTO PARA USAR!

### Acceso Rápido:

**🌐 Interfaz Web:**
- **Inicio:** http://localhost:8080
- **Login:** http://localhost:8080/login.html
- **Dashboard:** http://localhost:8080/dashboard.html

**🔌 API:**
- **Estado:** http://localhost:8080/api/status
- **DB Test:** http://localhost:8080/api/db/test
- **Usuarios:** http://localhost:8080/api/db/users

**👤 Tu Cuenta:**
- **Usuario:** melany
- **Email:** melslores@outlook.es
- **2 Cuentas:** $1,000 + $5,000 USD

---

**🎯 Prueba el sistema ahora mismo en:** http://localhost:8080/login.html

**Autor:** Jorge Pena - REM Consultancy  
**Proyecto:** BankSim Enterprise v2.0  
**Tecnologías:** Java 8+, PostgreSQL 18, HTTP Server, JDBC
