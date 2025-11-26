package com.banksim.repository;

import com.banksim.config.DatabaseConfig;
import com.banksim.model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Transaction entity data access operations.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class TransactionRepository {
    
    private final DatabaseConfig dbConfig;
    
    public TransactionRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Creates a new transaction
     * 
     * @param transaction Transaction entity
     * @return Created transaction with ID
     * @throws SQLException if error occurs
     */
    public Transaction create(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_uuid, account_id, transaction_type, amount, " +
                     "balance_before, balance_after, description, reference_number, related_account_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING transaction_id, created_at";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, transaction.getTransactionUuid());
            stmt.setInt(2, transaction.getAccountId());
            stmt.setString(3, transaction.getTransactionType().name());
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setBigDecimal(5, transaction.getBalanceBefore());
            stmt.setBigDecimal(6, transaction.getBalanceAfter());
            stmt.setString(7, transaction.getDescription());
            stmt.setString(8, transaction.getReferenceNumber());
            
            if (transaction.getRelatedAccountId() != null) {
                stmt.setInt(9, transaction.getRelatedAccountId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            
            stmt.setString(10, transaction.getStatus().name());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            
            return transaction;
        }
    }
    
    /**
     * Finds transaction by ID
     * 
     * @param transactionId Transaction ID
     * @return Optional with transaction
     * @throws SQLException if error occurs
     */
    public Optional<Transaction> findById(Integer transactionId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToTransaction(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Finds transaction by UUID
     * 
     * @param uuid Transaction UUID
     * @return Optional with transaction
     * @throws SQLException if error occurs
     */
    public Optional<Transaction> findByUuid(UUID uuid) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_uuid = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, uuid);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToTransaction(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Finds all transactions for an account
     * 
     * @param accountId Account ID
     * @param limit Records to return
     * @param offset Records to skip
     * @return List of transactions
     * @throws SQLException if error occurs
     */
    public List<Transaction> findByAccountId(Integer accountId, int limit, int offset) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        
        return transactions;
    }
    
    /**
     * Finds all transactions for an account (no pagination)
     * 
     * @param accountId Account ID
     * @return List of transactions
     * @throws SQLException if error occurs
     */
    public List<Transaction> findByAccountId(Integer accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        
        return transactions;
    }
    
    /**
     * Finds transactions by type
     * 
     * @param accountId Account ID
     * @param type Transaction type
     * @return List of transactions
     * @throws SQLException if error occurs
     */
    public List<Transaction> findByAccountIdAndType(Integer accountId, Transaction.TransactionType type) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? AND transaction_type = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountId);
            stmt.setString(2, type.name());
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        
        return transactions;
    }
    
    /**
     * Updates transaction status
     * 
     * @param transactionId Transaction ID
     * @param status New status
     * @throws SQLException if error occurs
     */
    public void updateStatus(Integer transactionId, Transaction.TransactionStatus status) throws SQLException {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, transactionId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Counts transactions for an account
     * 
     * @param accountId Account ID
     * @return Transaction count
     * @throws SQLException if error occurs
     */
    public int countByAccountId(Integer accountId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE account_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
        }
    }
    
    /**
     * Finds recent transactions across all accounts
     * 
     * @param limit Number of records
     * @return List of recent transactions
     * @throws SQLException if error occurs
     */
    public List<Transaction> findRecent(int limit) throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC LIMIT ?";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        
        return transactions;
    }
    
    /**
     * Counts total transactions
     * 
     * @return Total count
     * @throws SQLException if error occurs
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions";
        
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
     * Maps ResultSet to Transaction entity
     * 
     * @param rs ResultSet
     * @return Transaction entity
     * @throws SQLException if error occurs
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setTransactionUuid((UUID) rs.getObject("transaction_uuid"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setTransactionType(Transaction.TransactionType.valueOf(rs.getString("transaction_type")));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setBalanceBefore(rs.getBigDecimal("balance_before"));
        transaction.setBalanceAfter(rs.getBigDecimal("balance_after"));
        transaction.setDescription(rs.getString("description"));
        transaction.setReferenceNumber(rs.getString("reference_number"));
        
        Integer relatedAccountId = (Integer) rs.getObject("related_account_id");
        transaction.setRelatedAccountId(relatedAccountId);
        
        transaction.setStatus(Transaction.TransactionStatus.valueOf(rs.getString("status")));
        transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        return transaction;
    }
}
