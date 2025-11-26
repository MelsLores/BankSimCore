# 🏦 BankSim Enterprise v5.0

**Enterprise Banking System with PostgreSQL**  
Author: Jorge Pena - REM Consultancy

## Overview

BankSim Enterprise is a complete banking system built with Java and PostgreSQL, featuring:

- ✅ REST API with JWT authentication
- ✅ PostgreSQL database with complete schema
- ✅ Repository pattern for data access
- ✅ Service layer for business logic
- ✅ Custom BCrypt password hashing
- ✅ Transaction management (ACID compliance)
- ✅ Modern frontend with login/register
- ✅ 12 test cases validation

## Tech Stack

- **Backend:** Java 8+, HttpServer
- **Database:** PostgreSQL 12+
- **Connection Pool:** HikariCP
- **Security:** JWT (custom), BCrypt (custom)
- **Frontend:** HTML5, CSS3, JavaScript

## Quick Start

### 1. Prerequisites

```powershell
# Install PostgreSQL
choco install postgresql

# Or download from: https://www.postgresql.org/download/
```

### 2. Database Setup

```powershell
# Run the automated setup script
.\scripts\setup_database.ps1

# This will:
# - Create 'banksim_db' database
# - Execute schema.sql (tables, indexes, triggers)
# - Load seed_data.sql (test accounts)
# - Verify installation
```

### 3. Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database Connection
db.url=jdbc:postgresql://localhost:5432/banksim_db
db.username=postgres
db.password=your_password_here

# JWT Configuration
jwt.secret=your_secret_key_min_256_bits
jwt.expiration=3600000

# BCrypt Configuration
bcrypt.strength=10
```

### 4. Compile and Run

```powershell
# Compile
javac -d bin -sourcepath src\main\java src\main\java\com\banksim\**\*.java

# Run server
java -cp "bin;lib\*" com.banksim.BankSimServer
```

The server will start at: **http://localhost:8080**

### 5. Access the Application

- **Login:** http://localhost:8080/login.html
- **Register:** http://localhost:8080/register.html
- **API Docs:** See below

## Default Test Accounts

```
Username: admin
Password: Admin1234
Role: ADMIN

Username: user1
Password: User1234
Role: CUSTOMER
Customer: CUST001 (Juan Perez)
Accounts: 3 accounts ($1,000, $5,000, $500)

Username: user2
Password: User2234
Role: CUSTOMER
Customer: CUST002 (Maria Garcia)
Accounts: 2 accounts ($2,500, $7,500)
```

## API Endpoints

### Authentication

```http
POST /api/auth/login
POST /api/auth/register
POST /api/auth/refresh
POST /api/auth/change-password
```

### Customers

```http
GET    /api/customers
POST   /api/customers
GET    /api/customers/{id}
PUT    /api/customers/{id}
GET    /api/customers/user/{userId}
POST   /api/customers/{id}/verify-key
```

### Accounts

```http
POST   /api/accounts
GET    /api/accounts/{id}
GET    /api/accounts/{id}/balance
GET    /api/accounts/customer/{customerId}
```

### Transactions

```http
POST   /api/transactions/deposit
POST   /api/transactions/withdraw
POST   /api/transactions/transfer
GET    /api/transactions/statement/{accountId}
GET    /api/transactions/account/{accountId}
```

### Tests

```http
GET    /api/tests/run-all
```

## API Examples

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"User1234"}'

# Response:
# {"success":true,"token":"eyJ...","userId":"2","message":"Login successful"}
```

### Create Account

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"accountType":"SAVINGS","initialDeposit":"1000.00"}'
```

### Make Deposit

```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"accountId":1,"amount":"500.00","description":"Deposit"}'
```

### Transfer Money

```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId":1,"toAccountId":2,"amount":"100.00","description":"Transfer"}'
```

## Project Structure

```
├── database/
│   ├── schema.sql              # Database schema (tables, indexes, triggers)
│   └── seed_data.sql           # Test data
├── docs/
│   └── MIGRATION_PROGRESS.md   # Detailed migration documentation
├── scripts/
│   └── setup_database.ps1      # Automated database setup
├── src/main/
│   ├── java/com/banksim/
│   │   ├── config/             # DatabaseConfig, SecurityConfig
│   │   ├── model/              # User, Customer, Account, Transaction
│   │   ├── repository/         # Data access layer (4 repositories)
│   │   ├── service/            # Business logic (5 services)
│   │   ├── controller/         # REST API (5 controllers)
│   │   ├── util/               # PasswordUtil, JwtUtil, ValidationUtil
│   │   └── BankSimServer.java  # Main application
│   └── resources/
│       ├── application.properties  # Configuration
│       └── static/
│           ├── login.html      # Login page
│           └── register.html   # Registration page
└── README.md
```

## Features

### Security

- **JWT Authentication:** Custom implementation with HMAC-SHA256
- **BCrypt Passwords:** Custom BCrypt with SHA-256 + salt + rounds
- **SQL Injection Prevention:** PreparedStatements throughout
- **Failed Login Tracking:** Account locking after 5 failed attempts
- **Audit Logging:** All security events logged

### Database

- **Connection Pooling:** HikariCP (10 max, 2 min idle)
- **ACID Transactions:** Full transaction management for deposits/withdrawals/transfers
- **Indexes:** Optimized queries on username, email, account_number
- **Triggers:** Automatic updated_at timestamps
- **Views:** Business intelligence (customer_summary, account_activity)

### Business Logic

- **Account Management:** Create, retrieve, update accounts
- **Transactions:** Deposit, withdraw, transfer with balance validation
- **Customer Verification:** Personal key validation (12 test cases)
- **Balance Queries:** Real-time balance with transaction history

### Validation

- **Bank Code:** 3 digits (e.g., "123")
- **Branch Code:** 4 digits (e.g., "4567")
- **Account Number:** 10 digits (e.g., "1234567890")
- **Personal Key:** 8+ chars, uppercase, lowercase, digit (e.g., "Abcd1234")
- **Password:** 8+ chars, uppercase, lowercase, digit
- **Email:** Valid email format
- **Phone:** 10 digits

## Development

### Run Tests

```bash
# Run validation tests
curl -H "Authorization: Bearer TOKEN" \
  http://localhost:8080/api/tests/run-all

# Response shows 12 test case results:
# {"success":true,"totalTests":12,"passed":12,"failed":0,"results":[...]}
```

### Database Queries

```sql
-- View all customers
SELECT * FROM customers;

-- View customer summary
SELECT * FROM customer_summary;

-- View account activity
SELECT * FROM account_activity WHERE customer_code = 'CUST001';

-- Check account balance
SELECT account_number, balance FROM accounts WHERE customer_id = 1;

-- View transaction history
SELECT * FROM transactions WHERE account_id = 1 ORDER BY created_at DESC;
```

## Migration Documentation

For detailed migration notes, see [MIGRATION_PROGRESS.md](docs/MIGRATION_PROGRESS.md)

Includes:
- Complete layer-by-layer implementation
- Database schema design
- Service architecture
- API endpoint documentation
- Security implementation details

## License

Copyright © 2024 Jorge Pena - REM Consultancy

## Support

For questions or issues, please contact: jorge.pena@remconsultancy.com

---

**BankSim Enterprise v5.0** - Banking Made Simple 🏦
