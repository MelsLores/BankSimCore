-- ============================================================================
-- BankSim Core - PostgreSQL Database Schema
-- Enterprise Banking Application
-- Version: 1.0
-- Author: Jorge Pena - REM Consultancy
-- ============================================================================

-- Drop existing tables if they exist
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS audit_logs CASCADE;

-- ============================================================================
-- USERS TABLE - Authentication and Authorization
-- ============================================================================
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT true,
    is_locked BOOLEAN DEFAULT false,
    failed_login_attempts INTEGER DEFAULT 0,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'CUSTOMER', 'EMPLOYEE'))
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- ============================================================================
-- CUSTOMERS TABLE - Customer Information
-- ============================================================================
CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    customer_code VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    personal_key_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    date_of_birth DATE,
    identification_number VARCHAR(50),
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customers_code ON customers(customer_code);
CREATE INDEX idx_customers_user ON customers(user_id);

-- ============================================================================
-- ACCOUNTS TABLE - Bank Accounts
-- ============================================================================
CREATE TABLE accounts (
    account_id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    bank_code VARCHAR(3) NOT NULL,
    branch_code VARCHAR(4) NOT NULL,
    account_type VARCHAR(20) DEFAULT 'SAVINGS',
    balance DECIMAL(15, 2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    interest_rate DECIMAL(5, 2) DEFAULT 0.00,
    overdraft_limit DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_balance CHECK (balance >= -overdraft_limit),
    CONSTRAINT chk_account_type CHECK (account_type IN ('SAVINGS', 'CHECKING', 'BUSINESS')),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED')),
    CONSTRAINT chk_bank_code CHECK (bank_code ~ '^[0-9]{3}$'),
    CONSTRAINT chk_branch_code CHECK (branch_code ~ '^[0-9]{4}$')
);

CREATE INDEX idx_accounts_number ON accounts(account_number);
CREATE INDEX idx_accounts_customer ON accounts(customer_id);
CREATE INDEX idx_accounts_status ON accounts(status);

-- ============================================================================
-- TRANSACTIONS TABLE - Financial Transactions
-- ============================================================================
CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    transaction_uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    account_id INTEGER NOT NULL REFERENCES accounts(account_id) ON DELETE RESTRICT,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    balance_before DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    description TEXT,
    reference_number VARCHAR(50),
    related_account_id INTEGER REFERENCES accounts(account_id),
    status VARCHAR(20) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_amount CHECK (amount >= 0),
    CONSTRAINT chk_transaction_type CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT', 'FEE', 'INTEREST')),
    CONSTRAINT chk_transaction_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED'))
);

CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_type ON transactions(transaction_type);
CREATE INDEX idx_transactions_date ON transactions(created_at);
CREATE INDEX idx_transactions_uuid ON transactions(transaction_uuid);

-- ============================================================================
-- AUDIT LOGS TABLE - Security and Compliance
-- ============================================================================
CREATE TABLE audit_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INTEGER,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_date ON audit_logs(created_at);

-- ============================================================================
-- TRIGGERS - Automatic Timestamp Updates
-- ============================================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_accounts_updated_at BEFORE UPDATE ON accounts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- VIEWS - Business Intelligence
-- ============================================================================

-- Customer Account Summary View
CREATE OR REPLACE VIEW v_customer_summary AS
SELECT 
    c.customer_id,
    c.customer_code,
    c.first_name || ' ' || c.last_name AS full_name,
    c.phone,
    u.email,
    COUNT(a.account_id) AS total_accounts,
    COALESCE(SUM(a.balance), 0) AS total_balance,
    c.created_at AS customer_since
FROM customers c
LEFT JOIN users u ON c.user_id = u.user_id
LEFT JOIN accounts a ON c.customer_id = a.customer_id AND a.status = 'ACTIVE'
GROUP BY c.customer_id, c.customer_code, c.first_name, c.last_name, c.phone, u.email, c.created_at;

-- Account Activity View
CREATE OR REPLACE VIEW v_account_activity AS
SELECT 
    a.account_number,
    c.customer_code,
    c.first_name || ' ' || c.last_name AS customer_name,
    a.account_type,
    a.balance,
    a.status,
    COUNT(t.transaction_id) AS transaction_count,
    MAX(t.created_at) AS last_transaction_date
FROM accounts a
JOIN customers c ON a.customer_id = c.customer_id
LEFT JOIN transactions t ON a.account_id = t.account_id
GROUP BY a.account_id, a.account_number, c.customer_code, c.first_name, c.last_name, a.account_type, a.balance, a.status;

-- Daily Transaction Summary View
CREATE OR REPLACE VIEW v_daily_transactions AS
SELECT 
    DATE(created_at) AS transaction_date,
    transaction_type,
    COUNT(*) AS transaction_count,
    SUM(amount) AS total_amount
FROM transactions
WHERE status = 'COMPLETED'
GROUP BY DATE(created_at), transaction_type
ORDER BY transaction_date DESC, transaction_type;

-- ============================================================================
-- FUNCTIONS - Business Logic
-- ============================================================================

-- Function to validate bank account number format
CREATE OR REPLACE FUNCTION validate_account_number(
    p_bank_code VARCHAR(3),
    p_branch_code VARCHAR(4),
    p_account_number VARCHAR(10)
) RETURNS BOOLEAN AS $$
BEGIN
    RETURN (
        p_bank_code ~ '^[0-9]{3}$' AND
        p_branch_code ~ '^[0-9]{4}$' AND
        p_account_number ~ '^[0-9]{10}$'
    );
END;
$$ LANGUAGE plpgsql;

-- Function to generate unique account number
CREATE OR REPLACE FUNCTION generate_account_number(
    p_bank_code VARCHAR(3),
    p_branch_code VARCHAR(4)
) RETURNS VARCHAR(50) AS $$
DECLARE
    v_account_number VARCHAR(10);
    v_full_number VARCHAR(50);
    v_exists BOOLEAN;
BEGIN
    LOOP
        v_account_number := LPAD(FLOOR(RANDOM() * 10000000000)::TEXT, 10, '0');
        v_full_number := p_bank_code || '-' || p_branch_code || '-' || v_account_number;
        
        SELECT EXISTS(SELECT 1 FROM accounts WHERE account_number = v_full_number) INTO v_exists;
        
        EXIT WHEN NOT v_exists;
    END LOOP;
    
    RETURN v_full_number;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- GRANTS - Security Permissions
-- ============================================================================

-- Create application user (run this separately after creating the user)
-- CREATE USER banksim_app WITH PASSWORD 'your_secure_password';
-- GRANT CONNECT ON DATABASE banksim TO banksim_app;
-- GRANT USAGE ON SCHEMA public TO banksim_app;
-- GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO banksim_app;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO banksim_app;
-- GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO banksim_app;

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================
