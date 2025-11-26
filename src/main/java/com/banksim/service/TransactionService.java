package com.banksim.service;

import com.banksim.model.Account;
import com.banksim.model.Transaction;
import com.banksim.repository.AccountRepository;
import com.banksim.repository.TransactionRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import com.banksim.config.DatabaseConfig;

/**
 * Service for banking transaction operations.
 * Handles deposits, withdrawals, and transfers with proper transaction management.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class TransactionService {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final DatabaseConfig dbConfig;
    
    public TransactionService() {
        this.accountRepository = new AccountRepository();
        this.transactionRepository = new TransactionRepository();
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Deposits money into an account
     * 
     * @param accountNumber Account number
     * @param amount Amount to deposit
     * @param description Transaction description
     * @return Created transaction
     * @throws TransactionServiceException if deposit fails
     */
    public Transaction deposit(String accountNumber, BigDecimal amount, String description) 
            throws TransactionServiceException {
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionServiceException("Deposit amount must be positive");
        }
        
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Find account
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (!accountOpt.isPresent()) {
                throw new TransactionServiceException("Account not found: " + accountNumber);
            }
            
            Account account = accountOpt.get();
            
            // Check account is active
            if (!account.isActive()) {
                throw new TransactionServiceException("Account is not active");
            }
            
            // Calculate new balance
            BigDecimal oldBalance = account.getBalance();
            BigDecimal newBalance = oldBalance.add(amount);
            
            // Update account balance
            account.setBalance(newBalance);
            accountRepository.update(account);
            
            // Create transaction record
            Transaction transaction = new Transaction(
                account.getAccountId(),
                Transaction.TransactionType.DEPOSIT,
                amount,
                oldBalance,
                newBalance,
                description
            );
            
            transaction = transactionRepository.create(transaction);
            
            conn.commit();
            return transaction;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            throw new TransactionServiceException("Database error during deposit: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log error
                }
            }
        }
    }
    
    /**
     * Withdraws money from an account
     * 
     * @param accountNumber Account number
     * @param amount Amount to withdraw
     * @param description Transaction description
     * @return Created transaction
     * @throws TransactionServiceException if withdrawal fails
     */
    public Transaction withdraw(String accountNumber, BigDecimal amount, String description) 
            throws TransactionServiceException {
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionServiceException("Withdrawal amount must be positive");
        }
        
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Find account
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (!accountOpt.isPresent()) {
                throw new TransactionServiceException("Account not found: " + accountNumber);
            }
            
            Account account = accountOpt.get();
            
            // Check account is active
            if (!account.isActive()) {
                throw new TransactionServiceException("Account is not active");
            }
            
            // Check sufficient funds
            if (!account.hasSufficientBalance(amount)) {
                throw new TransactionServiceException("Insufficient funds");
            }
            
            // Calculate new balance
            BigDecimal oldBalance = account.getBalance();
            BigDecimal newBalance = oldBalance.subtract(amount);
            
            // Update account balance
            account.setBalance(newBalance);
            accountRepository.update(account);
            
            // Create transaction record
            Transaction transaction = new Transaction(
                account.getAccountId(),
                Transaction.TransactionType.WITHDRAWAL,
                amount,
                oldBalance,
                newBalance,
                description
            );
            
            transaction = transactionRepository.create(transaction);
            
            conn.commit();
            return transaction;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            throw new TransactionServiceException("Database error during withdrawal: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log error
                }
            }
        }
    }
    
    /**
     * Transfers money between accounts
     * 
     * @param fromAccountNumber Source account
     * @param toAccountNumber Destination account
     * @param amount Amount to transfer
     * @param description Transaction description
     * @return Array of two transactions [withdrawal, deposit]
     * @throws TransactionServiceException if transfer fails
     */
    public Transaction[] transfer(String fromAccountNumber, String toAccountNumber, 
                                  BigDecimal amount, String description) 
            throws TransactionServiceException {
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionServiceException("Transfer amount must be positive");
        }
        
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new TransactionServiceException("Cannot transfer to the same account");
        }
        
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Find both accounts
            Optional<Account> fromAccountOpt = accountRepository.findByAccountNumber(fromAccountNumber);
            Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(toAccountNumber);
            
            if (!fromAccountOpt.isPresent()) {
                throw new TransactionServiceException("Source account not found: " + fromAccountNumber);
            }
            
            if (!toAccountOpt.isPresent()) {
                throw new TransactionServiceException("Destination account not found: " + toAccountNumber);
            }
            
            Account fromAccount = fromAccountOpt.get();
            Account toAccount = toAccountOpt.get();
            
            // Check both accounts are active
            if (!fromAccount.isActive()) {
                throw new TransactionServiceException("Source account is not active");
            }
            
            if (!toAccount.isActive()) {
                throw new TransactionServiceException("Destination account is not active");
            }
            
            // Check sufficient funds
            if (!fromAccount.hasSufficientBalance(amount)) {
                throw new TransactionServiceException("Insufficient funds in source account");
            }
            
            // Process withdrawal from source
            BigDecimal fromOldBalance = fromAccount.getBalance();
            BigDecimal fromNewBalance = fromOldBalance.subtract(amount);
            fromAccount.setBalance(fromNewBalance);
            accountRepository.update(fromAccount);
            
            // Process deposit to destination
            BigDecimal toOldBalance = toAccount.getBalance();
            BigDecimal toNewBalance = toOldBalance.add(amount);
            toAccount.setBalance(toNewBalance);
            accountRepository.update(toAccount);
            
            // Create withdrawal transaction
            Transaction withdrawalTx = new Transaction(
                fromAccount.getAccountId(),
                Transaction.TransactionType.TRANSFER_OUT,
                amount,
                fromOldBalance,
                fromNewBalance,
                description
            );
            withdrawalTx.setRelatedAccountId(toAccount.getAccountId());
            withdrawalTx = transactionRepository.create(withdrawalTx);
            
            // Create deposit transaction
            Transaction depositTx = new Transaction(
                toAccount.getAccountId(),
                Transaction.TransactionType.TRANSFER_IN,
                amount,
                toOldBalance,
                toNewBalance,
                description
            );
            depositTx.setRelatedAccountId(fromAccount.getAccountId());
            depositTx = transactionRepository.create(depositTx);
            
            conn.commit();
            
            return new Transaction[] { withdrawalTx, depositTx };
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            throw new TransactionServiceException("Database error during transfer: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log error
                }
            }
        }
    }
    
    /**
     * Gets account statement (transaction history)
     * 
     * @param accountNumber Account number
     * @param limit Number of transactions to return
     * @return List of transactions
     * @throws TransactionServiceException if error occurs
     */
    public List<Transaction> getStatement(String accountNumber, int limit) 
            throws TransactionServiceException {
        try {
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (!accountOpt.isPresent()) {
                throw new TransactionServiceException("Account not found: " + accountNumber);
            }
            
            Account account = accountOpt.get();
            return transactionRepository.findByAccountId(account.getAccountId(), limit, 0);
            
        } catch (SQLException e) {
            throw new TransactionServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Gets all transactions for an account
     * 
     * @param accountNumber Account number
     * @return List of all transactions
     * @throws TransactionServiceException if error occurs
     */
    public List<Transaction> getAllTransactions(String accountNumber) 
            throws TransactionServiceException {
        try {
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (!accountOpt.isPresent()) {
                throw new TransactionServiceException("Account not found: " + accountNumber);
            }
            
            Account account = accountOpt.get();
            return transactionRepository.findByAccountId(account.getAccountId());
            
        } catch (SQLException e) {
            throw new TransactionServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Gets transaction count for an account
     * 
     * @param accountNumber Account number
     * @return Transaction count
     * @throws TransactionServiceException if error occurs
     */
    public int getTransactionCount(String accountNumber) throws TransactionServiceException {
        try {
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (!accountOpt.isPresent()) {
                throw new TransactionServiceException("Account not found: " + accountNumber);
            }
            
            Account account = accountOpt.get();
            return transactionRepository.countByAccountId(account.getAccountId());
            
        } catch (SQLException e) {
            throw new TransactionServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Gets recent transactions across all accounts
     * 
     * @param limit Number of transactions
     * @return List of recent transactions
     * @throws TransactionServiceException if error occurs
     */
    public List<Transaction> getRecentTransactions(int limit) throws TransactionServiceException {
        try {
            return transactionRepository.findRecent(limit);
        } catch (SQLException e) {
            throw new TransactionServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Custom exception for transaction service errors
     */
    public static class TransactionServiceException extends Exception {
        public TransactionServiceException(String message) {
            super(message);
        }
    }
}
