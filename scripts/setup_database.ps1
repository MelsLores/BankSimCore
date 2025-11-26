# ============================================================================
# BankSim Core - PostgreSQL Setup Script
# Sets up PostgreSQL database for BankSim application
# ============================================================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "BankSim Core - Database Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$DB_NAME = "banksim"
$DB_USER = "banksim_app"
$DB_PASSWORD = "BankSim2024_SecurePass!"
$POSTGRES_USER = "postgres"
$SCHEMA_FILE = "database\schema.sql"
$SEED_FILE = "database\seed_data.sql"

# Check if PostgreSQL is installed
Write-Host "[1/6] Checking PostgreSQL installation..." -ForegroundColor Yellow
$psqlPath = Get-Command psql -ErrorAction SilentlyContinue

if (-not $psqlPath) {
    Write-Host "ERROR: PostgreSQL is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install PostgreSQL from: https://www.postgresql.org/download/" -ForegroundColor Yellow
    Write-Host "Or add PostgreSQL bin directory to PATH" -ForegroundColor Yellow
    exit 1
}

Write-Host "PostgreSQL found at: $($psqlPath.Source)" -ForegroundColor Green
Write-Host ""

# Prompt for postgres password
Write-Host "[2/6] Database connection setup" -ForegroundColor Yellow
$postgresPassword = Read-Host "Enter PostgreSQL 'postgres' user password" -AsSecureString
$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($postgresPassword)
$POSTGRES_PASSWORD = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
Write-Host ""

# Set PGPASSWORD environment variable
$env:PGPASSWORD = $POSTGRES_PASSWORD

# Check if database exists
Write-Host "[3/6] Checking if database exists..." -ForegroundColor Yellow
$dbExists = psql -U $POSTGRES_USER -h localhost -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'" 2>$null

if ($dbExists -eq "1") {
    Write-Host "Database '$DB_NAME' already exists" -ForegroundColor Yellow
    $recreate = Read-Host "Do you want to drop and recreate it? (yes/no)"
    
    if ($recreate -eq "yes") {
        Write-Host "Dropping database '$DB_NAME'..." -ForegroundColor Yellow
        
        # Terminate existing connections
        psql -U $POSTGRES_USER -h localhost -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='$DB_NAME' AND pid <> pg_backend_pid();" 2>$null
        
        # Drop database
        psql -U $POSTGRES_USER -h localhost -c "DROP DATABASE IF EXISTS $DB_NAME;" 2>$null
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Database dropped successfully" -ForegroundColor Green
        } else {
            Write-Host "ERROR: Failed to drop database" -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "Skipping database recreation" -ForegroundColor Yellow
        Write-Host ""
    }
}

# Create database if it doesn't exist
$dbExists = psql -U $POSTGRES_USER -h localhost -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'" 2>$null

if ($dbExists -ne "1") {
    Write-Host "Creating database '$DB_NAME'..." -ForegroundColor Yellow
    psql -U $POSTGRES_USER -h localhost -c "CREATE DATABASE $DB_NAME WITH ENCODING='UTF8' LC_COLLATE='en_US.UTF-8' LC_CTYPE='en_US.UTF-8' TEMPLATE=template0;" 2>$null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Database created successfully" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Failed to create database" -ForegroundColor Red
        exit 1
    }
}
Write-Host ""

# Create application user
Write-Host "[4/6] Creating application user..." -ForegroundColor Yellow
$userExists = psql -U $POSTGRES_USER -h localhost -tAc "SELECT 1 FROM pg_roles WHERE rolname='$DB_USER'" 2>$null

if ($userExists -eq "1") {
    Write-Host "User '$DB_USER' already exists, updating password..." -ForegroundColor Yellow
    psql -U $POSTGRES_USER -h localhost -c "ALTER USER $DB_USER WITH PASSWORD '$DB_PASSWORD';" 2>$null
} else {
    Write-Host "Creating user '$DB_USER'..." -ForegroundColor Yellow
    psql -U $POSTGRES_USER -h localhost -c "CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';" 2>$null
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "User configured successfully" -ForegroundColor Green
} else {
    Write-Host "ERROR: Failed to configure user" -ForegroundColor Red
    exit 1
}

# Grant permissions
Write-Host "Granting permissions..." -ForegroundColor Yellow
psql -U $POSTGRES_USER -h localhost -d $DB_NAME -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;" 2>$null
psql -U $POSTGRES_USER -h localhost -d $DB_NAME -c "GRANT ALL PRIVILEGES ON SCHEMA public TO $DB_USER;" 2>$null
psql -U $POSTGRES_USER -h localhost -d $DB_NAME -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER;" 2>$null
psql -U $POSTGRES_USER -h localhost -d $DB_NAME -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER;" 2>$null
Write-Host ""

# Execute schema
Write-Host "[5/6] Creating database schema..." -ForegroundColor Yellow
if (Test-Path $SCHEMA_FILE) {
    psql -U $POSTGRES_USER -h localhost -d $DB_NAME -f $SCHEMA_FILE 2>$null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Schema created successfully" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Failed to create schema" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "ERROR: Schema file not found: $SCHEMA_FILE" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Execute seed data
Write-Host "[6/6] Inserting seed data..." -ForegroundColor Yellow
if (Test-Path $SEED_FILE) {
    psql -U $POSTGRES_USER -h localhost -d $DB_NAME -f $SEED_FILE 2>$null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Seed data inserted successfully" -ForegroundColor Green
    } else {
        Write-Host "WARNING: Some seed data may have failed (this might be OK if data already exists)" -ForegroundColor Yellow
    }
} else {
    Write-Host "WARNING: Seed file not found: $SEED_FILE" -ForegroundColor Yellow
}
Write-Host ""

# Clear password from environment
$env:PGPASSWORD = ""

# Verification
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Verifying database setup..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$env:PGPASSWORD = $DB_PASSWORD
$tableCount = psql -U $DB_USER -h localhost -d $DB_NAME -tAc "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE';" 2>$null
$env:PGPASSWORD = ""

Write-Host "Tables created: $tableCount" -ForegroundColor Green
Write-Host ""

# Connection string
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Database setup completed successfully!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Connection details:" -ForegroundColor Yellow
Write-Host "  Host: localhost" -ForegroundColor White
Write-Host "  Port: 5432" -ForegroundColor White
Write-Host "  Database: $DB_NAME" -ForegroundColor White
Write-Host "  Username: $DB_USER" -ForegroundColor White
Write-Host "  Password: $DB_PASSWORD" -ForegroundColor White
Write-Host ""
Write-Host "JDBC URL:" -ForegroundColor Yellow
Write-Host "  jdbc:postgresql://localhost:5432/$DB_NAME" -ForegroundColor White
Write-Host ""
Write-Host "Update these credentials in:" -ForegroundColor Yellow
Write-Host "  src\main\resources\application.properties" -ForegroundColor White
Write-Host ""
