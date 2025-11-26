-- ============================================================================
-- BankSim Core - Seed Data
-- Initial data for development and testing
-- Version: 1.0
-- ============================================================================

-- ============================================================================
-- ADMIN USER
-- Password: Admin@2024 (BCrypt hashed)
-- ============================================================================
INSERT INTO users (username, email, password_hash, salt, role, is_active) VALUES
('admin', 'admin@banksim.com', '$2a$10$YourHashHere', 'admin_salt_123', 'ADMIN', true);

-- ============================================================================
-- TEST CUSTOMER USERS
-- Password for all: Test@1234 (BCrypt hashed)
-- ============================================================================
INSERT INTO users (username, email, password_hash, salt, role, is_active) VALUES
('juan.perez', 'juan.perez@email.com', '$2a$10$YourHashHere', 'salt_juan_123', 'CUSTOMER', true),
('maria.garcia', 'maria.garcia@email.com', '$2a$10$YourHashHere', 'salt_maria_456', 'CUSTOMER', true),
('carlos.lopez', 'carlos.lopez@email.com', '$2a$10$YourHashHere', 'salt_carlos_789', 'CUSTOMER', true);

-- ============================================================================
-- CUSTOMERS
-- Personal Key: Abcd1234 (hashed)
-- ============================================================================
INSERT INTO customers (user_id, customer_code, first_name, last_name, personal_key_hash, phone, address, date_of_birth, identification_number, is_verified) VALUES
(2, 'CUST001', 'Juan', 'Perez', '$2a$10$PersonalKeyHashHere', '+1-555-0101', '123 Main St, New York, NY 10001', '1985-05-15', 'ID-12345678', true),
(3, 'CUST002', 'Maria', 'Garcia', '$2a$10$PersonalKeyHashHere', '+1-555-0102', '456 Oak Ave, Los Angeles, CA 90001', '1990-08-22', 'ID-87654321', true),
(4, 'CUST003', 'Carlos', 'Lopez', '$2a$10$PersonalKeyHashHere', '+1-555-0103', '789 Pine Rd, Chicago, IL 60601', '1988-03-10', 'ID-11223344', true);

-- ============================================================================
-- ACCOUNTS
-- Account numbers for testing the 12 test cases from PDF
-- ============================================================================

-- CUST001 Main Account (for testing)
INSERT INTO accounts (customer_id, account_number, bank_code, branch_code, account_type, balance, status) VALUES
(1, '123-4567-1234567890', '123', '4567', 'SAVINGS', 1000.00, 'ACTIVE');

-- CUST001 Additional Accounts
INSERT INTO accounts (customer_id, account_number, bank_code, branch_code, account_type, balance, status) VALUES
(1, '123-4567-9876543210', '123', '4567', 'CHECKING', 5000.00, 'ACTIVE'),
(1, '123-4567-5555555555', '123', '4567', 'SAVINGS', 500.00, 'ACTIVE');

-- CUST002 Accounts
INSERT INTO accounts (customer_id, account_number, bank_code, branch_code, account_type, balance, status) VALUES
(2, '234-5678-1111111111', '234', '5678', 'SAVINGS', 2500.00, 'ACTIVE'),
(2, '234-5678-2222222222', '234', '5678', 'CHECKING', 7500.00, 'ACTIVE');

-- CUST003 Accounts
INSERT INTO accounts (customer_id, account_number, bank_code, branch_code, account_type, balance, status) VALUES
(3, '345-6789-3333333333', '345', '6789', 'SAVINGS', 15000.00, 'ACTIVE'),
(3, '345-6789-4444444444', '345', '6789', 'BUSINESS', 50000.00, 'ACTIVE');

-- ============================================================================
-- SAMPLE TRANSACTIONS
-- Historical transaction data for testing and demonstration
-- ============================================================================

-- Deposits for CUST001 Main Account
INSERT INTO transactions (account_id, transaction_type, amount, balance_before, balance_after, description, status) VALUES
(1, 'DEPOSIT', 1000.00, 0.00, 1000.00, 'Initial deposit', 'COMPLETED'),
(1, 'DEPOSIT', 500.00, 1000.00, 1500.00, 'Payroll deposit', 'COMPLETED');

-- Withdrawal from CUST001 Main Account
INSERT INTO transactions (account_id, transaction_type, amount, balance_before, balance_after, description, status) VALUES
(1, 'WITHDRAWAL', 500.00, 1500.00, 1000.00, 'ATM withdrawal', 'COMPLETED');

-- Transfers between CUST001 accounts
INSERT INTO transactions (account_id, transaction_type, amount, balance_before, balance_after, description, related_account_id, status) VALUES
(1, 'TRANSFER_OUT', 200.00, 1000.00, 800.00, 'Transfer to checking', 2, 'COMPLETED'),
(2, 'TRANSFER_IN', 200.00, 5000.00, 5200.00, 'Transfer from savings', 1, 'COMPLETED');

-- CUST002 Transactions
INSERT INTO transactions (account_id, transaction_type, amount, balance_before, balance_after, description, status) VALUES
(4, 'DEPOSIT', 2500.00, 0.00, 2500.00, 'Initial deposit', 'COMPLETED'),
(5, 'DEPOSIT', 7500.00, 0.00, 7500.00, 'Business income', 'COMPLETED');

-- CUST003 Transactions
INSERT INTO transactions (account_id, transaction_type, amount, balance_before, balance_after, description, status) VALUES
(6, 'DEPOSIT', 15000.00, 0.00, 15000.00, 'Salary deposit', 'COMPLETED'),
(7, 'DEPOSIT', 50000.00, 0.00, 50000.00, 'Business capital', 'COMPLETED');

-- ============================================================================
-- AUDIT LOGS - Sample Security Events
-- ============================================================================
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, ip_address, user_agent) VALUES
(1, 'LOGIN', 'USER', 1, '127.0.0.1', 'Mozilla/5.0'),
(2, 'LOGIN', 'USER', 2, '192.168.1.100', 'Mozilla/5.0'),
(2, 'CREATE_ACCOUNT', 'ACCOUNT', 1, '192.168.1.100', 'Mozilla/5.0'),
(2, 'DEPOSIT', 'TRANSACTION', 1, '192.168.1.100', 'Mozilla/5.0');

-- ============================================================================
-- TEST DATA FOR 12 TEST CASES FROM PDF
-- These records are specifically designed to validate the 12 equivalence class tests
-- ============================================================================

-- Test Accounts with specific characteristics for validation testing
-- TC01: Valid bank code (123), branch code (4567), account number (1234567890)
-- Already created above as main account

-- TC02-TC12: Various invalid combinations for testing
-- These will be validated programmatically through the API

-- Account with invalid bank code (for TC02 testing)
-- Will be rejected by validation - not inserted

-- Account with invalid branch code (for TC03 testing)
-- Will be rejected by validation - not inserted

-- Account with invalid account number (for TC04 testing)
-- Will be rejected by validation - not inserted

-- Account with valid format but non-existent (for TC05-TC12 testing)
INSERT INTO accounts (customer_id, account_number, bank_code, branch_code, account_type, balance, status) VALUES
(1, '999-9999-9999999999', '999', '9999', 'SAVINGS', 0.00, 'ACTIVE');

-- ============================================================================
-- VERIFICATION QUERIES
-- Run these to verify data was inserted correctly
-- ============================================================================

-- Verify users
-- SELECT username, email, role, is_active FROM users;

-- Verify customers
-- SELECT customer_code, first_name, last_name, phone, is_verified FROM customers;

-- Verify accounts
-- SELECT a.account_number, c.customer_code, a.account_type, a.balance, a.status 
-- FROM accounts a 
-- JOIN customers c ON a.customer_id = c.customer_id;

-- Verify transactions
-- SELECT t.transaction_type, t.amount, t.description, a.account_number, t.created_at
-- FROM transactions t
-- JOIN accounts a ON t.account_id = a.account_id
-- ORDER BY t.created_at DESC;

-- Verify customer summary
-- SELECT * FROM v_customer_summary;

-- ============================================================================
-- NOTES
-- ============================================================================
-- 1. Password hashes shown as '$2a$10$YourHashHere' need to be replaced with actual BCrypt hashes
-- 2. Personal key hashes need to be generated using the same BCrypt algorithm
-- 3. For production, use strong unique salts for each user
-- 4. Update IP addresses and user agents with actual values during runtime
-- 5. The test account 999-9999-9999999999 is for negative testing scenarios

-- ============================================================================
-- END OF SEED DATA
-- ============================================================================
