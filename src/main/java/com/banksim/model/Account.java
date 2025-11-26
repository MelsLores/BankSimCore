package com.banksim.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account entity representing bank accounts.
 * Each account belongs to a customer and can have multiple transactions.
 * Supports different account types: SAVINGS, CHECKING, BUSINESS.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class Account {
    
    /**
     * Account type enumeration
     */
    public enum AccountType {
        SAVINGS,
        CHECKING,
        BUSINESS
    }
    
    /**
     * Account status enumeration
     */
    public enum AccountStatus {
        ACTIVE,
        SUSPENDED,
        CLOSED
    }
    
    private Integer accountId;
    private Integer customerId;
    private String accountNumber;
    private String bankCode;
    private String branchCode;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
    private BigDecimal interestRate;
    private BigDecimal overdraftLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor
     */
    public Account() {
        this.balance = BigDecimal.ZERO;
        this.currency = "USD";
        this.status = AccountStatus.ACTIVE;
        this.accountType = AccountType.SAVINGS;
        this.interestRate = BigDecimal.ZERO;
        this.overdraftLimit = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor for account creation
     * 
     * @param customerId Customer ID who owns this account
     * @param accountNumber Full account number (XXX-XXXX-XXXXXXXXXX)
     * @param bankCode Bank code (3 digits)
     * @param branchCode Branch code (4 digits)
     * @param accountType Type of account
     */
    public Account(Integer customerId, String accountNumber, String bankCode, String branchCode, AccountType accountType) {
        this();
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
        this.branchCode = branchCode;
        this.accountType = accountType;
    }
    
    // Getters and Setters
    
    /**
     * Gets the account ID
     * @return Account ID
     */
    public Integer getAccountId() {
        return accountId;
    }
    
    /**
     * Sets the account ID
     * @param accountId Account ID
     */
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    
    /**
     * Gets the customer ID
     * @return Customer ID
     */
    public Integer getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets the customer ID
     * @param customerId Customer ID
     */
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Gets the account number
     * @return Full account number (XXX-XXXX-XXXXXXXXXX)
     */
    public String getAccountNumber() {
        return accountNumber;
    }
    
    /**
     * Sets the account number
     * @param accountNumber Full account number
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    /**
     * Gets the bank code
     * @return Bank code (3 digits)
     */
    public String getBankCode() {
        return bankCode;
    }
    
    /**
     * Sets the bank code
     * @param bankCode Bank code (3 digits)
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    
    /**
     * Gets the branch code
     * @return Branch code (4 digits)
     */
    public String getBranchCode() {
        return branchCode;
    }
    
    /**
     * Sets the branch code
     * @param branchCode Branch code (4 digits)
     */
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
    
    /**
     * Gets the account type
     * @return Account type (SAVINGS, CHECKING, BUSINESS)
     */
    public AccountType getAccountType() {
        return accountType;
    }
    
    /**
     * Sets the account type
     * @param accountType Account type
     */
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    
    /**
     * Gets the current balance
     * @return Account balance
     */
    public BigDecimal getBalance() {
        return balance;
    }
    
    /**
     * Sets the account balance
     * @param balance New balance
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    /**
     * Gets the currency code
     * @return Currency code (e.g., USD, EUR)
     */
    public String getCurrency() {
        return currency;
    }
    
    /**
     * Sets the currency code
     * @param currency Currency code
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    /**
     * Gets the account status
     * @return Account status (ACTIVE, SUSPENDED, CLOSED)
     */
    public AccountStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the account status
     * @param status Account status
     */
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    
    /**
     * Gets the interest rate
     * @return Interest rate percentage
     */
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    
    /**
     * Sets the interest rate
     * @param interestRate Interest rate percentage
     */
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
    
    /**
     * Gets the overdraft limit
     * @return Overdraft limit amount
     */
    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }
    
    /**
     * Sets the overdraft limit
     * @param overdraftLimit Overdraft limit amount
     */
    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
    
    /**
     * Gets creation timestamp
     * @return Created date/time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets creation timestamp
     * @param createdAt Created date/time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets last update timestamp
     * @return Updated date/time
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets last update timestamp
     * @param updatedAt Updated date/time
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business methods
    
    /**
     * Checks if account is active
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }
    
    /**
     * Checks if account has sufficient balance for withdrawal
     * @param amount Amount to check
     * @return true if sufficient funds, false otherwise
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        BigDecimal minimumBalance = overdraftLimit.negate();
        return balance.subtract(amount).compareTo(minimumBalance) >= 0;
    }
    
    /**
     * Deposits amount to account
     * @param amount Amount to deposit
     * @throws IllegalArgumentException if amount is negative or zero
     */
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Withdraws amount from account
     * @param amount Amount to withdraw
     * @throws IllegalArgumentException if amount is negative or zero
     * @throws IllegalStateException if insufficient funds
     */
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (!hasSufficientBalance(amount)) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType=" + accountType +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                '}';
    }
}
