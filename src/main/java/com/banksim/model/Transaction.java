package com.banksim.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Transaction entity representing financial transactions.
 * Each transaction is linked to an account and records the balance changes.
 * Supports various transaction types: DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, FEE, INTEREST.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class Transaction {
    
    /**
     * Transaction type enumeration
     */
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_IN,
        TRANSFER_OUT,
        FEE,
        INTEREST
    }
    
    /**
     * Transaction status enumeration
     */
    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REVERSED
    }
    
    private Integer transactionId;
    private UUID transactionUuid;
    private Integer accountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceNumber;
    private Integer relatedAccountId;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor
     */
    public Transaction() {
        this.transactionUuid = UUID.randomUUID();
        this.status = TransactionStatus.COMPLETED;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor for transaction creation
     * 
     * @param accountId Account ID
     * @param transactionType Type of transaction
     * @param amount Transaction amount
     * @param balanceBefore Balance before transaction
     * @param balanceAfter Balance after transaction
     * @param description Transaction description
     */
    public Transaction(Integer accountId, TransactionType transactionType, BigDecimal amount,
                      BigDecimal balanceBefore, BigDecimal balanceAfter, String description) {
        this();
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }
    
    // Getters and Setters
    
    /**
     * Gets the transaction ID
     * @return Transaction ID
     */
    public Integer getTransactionId() {
        return transactionId;
    }
    
    /**
     * Sets the transaction ID
     * @param transactionId Transaction ID
     */
    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }
    
    /**
     * Gets the transaction UUID
     * @return Unique transaction UUID
     */
    public UUID getTransactionUuid() {
        return transactionUuid;
    }
    
    /**
     * Sets the transaction UUID
     * @param transactionUuid Transaction UUID
     */
    public void setTransactionUuid(UUID transactionUuid) {
        this.transactionUuid = transactionUuid;
    }
    
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
     * Gets the transaction type
     * @return Transaction type
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    /**
     * Sets the transaction type
     * @param transactionType Transaction type
     */
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    /**
     * Gets the transaction amount
     * @return Transaction amount
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Sets the transaction amount
     * @param amount Transaction amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    /**
     * Gets the balance before transaction
     * @return Balance before
     */
    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }
    
    /**
     * Sets the balance before transaction
     * @param balanceBefore Balance before
     */
    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }
    
    /**
     * Gets the balance after transaction
     * @return Balance after
     */
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    /**
     * Sets the balance after transaction
     * @param balanceAfter Balance after
     */
    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
    
    /**
     * Gets the transaction description
     * @return Description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the transaction description
     * @param description Transaction description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the reference number
     * @return Reference number
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    /**
     * Sets the reference number
     * @param referenceNumber Reference number
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    /**
     * Gets the related account ID (for transfers)
     * @return Related account ID
     */
    public Integer getRelatedAccountId() {
        return relatedAccountId;
    }
    
    /**
     * Sets the related account ID (for transfers)
     * @param relatedAccountId Related account ID
     */
    public void setRelatedAccountId(Integer relatedAccountId) {
        this.relatedAccountId = relatedAccountId;
    }
    
    /**
     * Gets the transaction status
     * @return Transaction status
     */
    public TransactionStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the transaction status
     * @param status Transaction status
     */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    /**
     * Gets the creation timestamp
     * @return Created date/time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp
     * @param createdAt Created date/time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    
    /**
     * Checks if transaction is completed
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() {
        return this.status == TransactionStatus.COMPLETED;
    }
    
    /**
     * Checks if transaction is pending
     * @return true if pending, false otherwise
     */
    public boolean isPending() {
        return this.status == TransactionStatus.PENDING;
    }
    
    /**
     * Checks if transaction is a transfer
     * @return true if transfer, false otherwise
     */
    public boolean isTransfer() {
        return this.transactionType == TransactionType.TRANSFER_IN || 
               this.transactionType == TransactionType.TRANSFER_OUT;
    }
    
    /**
     * Marks transaction as completed
     */
    public void complete() {
        this.status = TransactionStatus.COMPLETED;
    }
    
    /**
     * Marks transaction as failed
     */
    public void fail() {
        this.status = TransactionStatus.FAILED;
    }
    
    /**
     * Reverses a transaction (for cancellations)
     */
    public void reverse() {
        this.status = TransactionStatus.REVERSED;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", transactionUuid=" + transactionUuid +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", balanceBefore=" + balanceBefore +
                ", balanceAfter=" + balanceAfter +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
