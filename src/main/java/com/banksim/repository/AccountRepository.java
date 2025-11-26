package com.banksim.repository;

import com.banksim.config.DatabaseConfig;
import com.banksim.model.Account;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Account entity data access operations.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class AccountRepository {
    
    private final DatabaseConfig dbConfig;
    
    public AccountRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Creates a new account
     * 
     * @param account Account entity
     * @return Created account with ID
     * @throws SQLException if error occurs
     */
    public Account create(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (customer_id, account_number, bank_code, branch_code, account_type, " +
                     "balance, currency, status, interest_rate, overdraft_limit) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING account_id, created_at, updated_at";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, account.getCustomerId());
            stmt.setString(2, account.getAccountNumber());
            stmt.setString(3, account.getBankCode());
            stmt.setString(4, account.getBranchCode());
            stmt.setString(5, account.getAccountType().name());
            stmt.setBigDecimal(6, account.getBalance());
            stmt.setString(7, account.getCurrency());
            stmt.setString(8, account.getStatus().name());
            stmt.setBigDecimal(9, account.getInterestRate());
            stmt.setBigDecimal(10, account.getOverdraftLimit());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                account.setAccountId(rs.getInt("account_id"));
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            
            return account;
        }
    }
    
    /**
     * Finds account by ID
     * 
     * @param accountId Account ID
     * @return Optional with account
     * @throws SQLException if error occurs
     */
    public Optional<Account> findById(Integer accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAccount(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Finds account by account number
     * 
     * @param accountNumber Full account number
     * @return Optional with account
     * @throws SQLException if error occurs
     */
    public Optional<Account> findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAccount(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Finds all accounts for a customer
     * 
     * @param customerId Customer ID
     * @return List of accounts
     * @throws SQLException if error occurs
     */
    public List<Account> findByCustomerId(Integer customerId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY created_at DESC";
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        }
        
        return accounts;
    }
    
    /**
     * Updates account information
     * 
     * @param account Account with updates
     * @return Updated account
     * @throws SQLException if error occurs
     */
    public Account update(Account account) throws SQLException {
        String sql = "UPDATE accounts SET balance = ?, status = ?, interest_rate = ?, overdraft_limit = ? " +
                     "WHERE account_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, account.getBalance());
            stmt.setString(2, account.getStatus().name());
            stmt.setBigDecimal(3, account.getInterestRate());
            stmt.setBigDecimal(4, account.getOverdraftLimit());
            stmt.setInt(5, account.getAccountId());
            
            stmt.executeUpdate();
            return account;
        }
    }
    
    /**
     * Updates account balance (for transactions)
     * 
     * @param accountId Account ID
     * @param newBalance New balance
     * @throws SQLException if error occurs
     */
    public void updateBalance(Integer accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Finds all accounts with pagination
     * 
     * @param limit Records to return
     * @param offset Records to skip
     * @return List of accounts
     * @throws SQLException if error occurs
     */
    public List<Account> findAll(int limit, int offset) throws SQLException {
        String sql = "SELECT * FROM accounts ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        }
        
        return accounts;
    }
    
    /**
     * Finds active accounts for a customer
     * 
     * @param customerId Customer ID
     * @return List of active accounts
     * @throws SQLException if error occurs
     */
    public List<Account> findActiveByCustomerId(Integer customerId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE customer_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC";
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        }
        
        return accounts;
    }
    
    /**
     * Counts total accounts
     * 
     * @return Total count
     * @throws SQLException if error occurs
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
        }
    }
    
    /**
     * Gets total balance for all accounts
     * 
     * @return Total balance
     * @throws SQLException if error occurs
     */
    public BigDecimal getTotalBalance() throws SQLException {
        String sql = "SELECT COALESCE(SUM(balance), 0) FROM accounts WHERE status = 'ACTIVE'";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Checks if account number exists
     * 
     * @param accountNumber Account number to check
     * @return true if exists
     * @throws SQLException if error occurs
     */
    public boolean existsByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
        }
    }
    
    /**
     * Maps ResultSet to Account entity
     * 
     * @param rs ResultSet
     * @return Account entity
     * @throws SQLException if error occurs
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setCustomerId(rs.getInt("customer_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setBankCode(rs.getString("bank_code"));
        account.setBranchCode(rs.getString("branch_code"));
        account.setAccountType(Account.AccountType.valueOf(rs.getString("account_type")));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setCurrency(rs.getString("currency"));
        account.setStatus(Account.AccountStatus.valueOf(rs.getString("status")));
        account.setInterestRate(rs.getBigDecimal("interest_rate"));
        account.setOverdraftLimit(rs.getBigDecimal("overdraft_limit"));
        account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        return account;
    }
}
