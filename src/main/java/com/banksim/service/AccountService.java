package com.banksim.service;

import com.banksim.model.Account;
import com.banksim.model.Customer;
import com.banksim.repository.AccountRepository;
import com.banksim.repository.CustomerRepository;
import com.banksim.util.ValidationUtil;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service for account management operations.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    
    public AccountService() {
        this.accountRepository = new AccountRepository();
        this.customerRepository = new CustomerRepository();
    }
    
    /**
     * Creates a new account
     * 
     * @param customerCode Customer code
     * @param bankCode Bank code (3 digits)
     * @param branchCode Branch code (4 digits)
     * @param accountType Account type
     * @param initialBalance Initial balance
     * @return Created account
     * @throws AccountServiceException if creation fails
     */
    public Account createAccount(String customerCode, String bankCode, String branchCode,
                                 String accountType, BigDecimal initialBalance) throws AccountServiceException {
        try {
            // Validate inputs
            validateAccountCreation(bankCode, branchCode, accountType, initialBalance);
            
            // Find customer
            Optional<Customer> customerOpt = customerRepository.findByCustomerCode(customerCode);
            if (!customerOpt.isPresent()) {
                throw new AccountServiceException("Customer not found: " + customerCode);
            }
            
            Customer customer = customerOpt.get();
            
            // Generate account number
            String accountNumber = generateAccountNumber(bankCode, branchCode);
            
            // Check if account number already exists (very unlikely but possible)
            if (accountRepository.existsByAccountNumber(accountNumber)) {
                accountNumber = generateAccountNumber(bankCode, branchCode);
            }
            
            // Create account
            Account account = new Account(
                customer.getCustomerId(),
                accountNumber,
                bankCode,
                branchCode,
                Account.AccountType.valueOf(accountType.toUpperCase())
            );
            
            account.setBalance(initialBalance);
            account.setStatus(Account.AccountStatus.ACTIVE);
            
            return accountRepository.create(account);
            
        } catch (SQLException e) {
            throw new AccountServiceException("Database error creating account: " + e.getMessage());
        }
    }
    
    /**
     * Finds account by account number
     * 
     * @param accountNumber Full account number (XXX-XXXX-XXXXXXXXXX)
     * @return Account if found
     * @throws AccountServiceException if not found
     */
    public Account getAccountByNumber(String accountNumber) throws AccountServiceException {
        try {
            if (!ValidationUtil.isValidFullAccountNumber(accountNumber)) {
                throw new AccountServiceException("Invalid account number format");
            }
            
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (!accountOpt.isPresent()) {
                throw new AccountServiceException("Account not found: " + accountNumber);
            }
            
            return accountOpt.get();
            
        } catch (SQLException e) {
            throw new AccountServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Gets all accounts for a customer
     * 
     * @param customerCode Customer code
     * @return List of accounts
     * @throws AccountServiceException if error occurs
     */
    public List<Account> getCustomerAccounts(String customerCode) throws AccountServiceException {
        try {
            Optional<Customer> customerOpt = customerRepository.findByCustomerCode(customerCode);
            if (!customerOpt.isPresent()) {
                throw new AccountServiceException("Customer not found: " + customerCode);
            }
            
            return accountRepository.findByCustomerId(customerOpt.get().getCustomerId());
            
        } catch (SQLException e) {
            throw new AccountServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Gets account balance
     * 
     * @param accountNumber Account number
     * @return Current balance
     * @throws AccountServiceException if error occurs
     */
    public BigDecimal getBalance(String accountNumber) throws AccountServiceException {
        Account account = getAccountByNumber(accountNumber);
        return account.getBalance();
    }
    
    /**
     * Updates account status
     * 
     * @param accountNumber Account number
     * @param newStatus New status (ACTIVE, SUSPENDED, CLOSED)
     * @throws AccountServiceException if error occurs
     */
    public void updateAccountStatus(String accountNumber, String newStatus) throws AccountServiceException {
        try {
            Account account = getAccountByNumber(accountNumber);
            
            Account.AccountStatus status;
            try {
                status = Account.AccountStatus.valueOf(newStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AccountServiceException("Invalid status: " + newStatus);
            }
            
            account.setStatus(status);
            accountRepository.update(account);
            
        } catch (SQLException e) {
            throw new AccountServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Gets all accounts with pagination
     * 
     * @param page Page number (0-based)
     * @param pageSize Page size
     * @return List of accounts
     * @throws AccountServiceException if error occurs
     */
    public List<Account> getAllAccounts(int page, int pageSize) throws AccountServiceException {
        try {
            int offset = page * pageSize;
            return accountRepository.findAll(pageSize, offset);
            
        } catch (SQLException e) {
            throw new AccountServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Gets total account count
     * 
     * @return Total accounts
     * @throws AccountServiceException if error occurs
     */
    public int getTotalAccountCount() throws AccountServiceException {
        try {
            return accountRepository.count();
        } catch (SQLException e) {
            throw new AccountServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Gets total balance across all accounts
     * 
     * @return Total balance
     * @throws AccountServiceException if error occurs
     */
    public BigDecimal getTotalBalance() throws AccountServiceException {
        try {
            return accountRepository.getTotalBalance();
        } catch (SQLException e) {
            throw new AccountServiceException("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Validates account creation parameters
     * 
     * @param bankCode Bank code
     * @param branchCode Branch code
     * @param accountType Account type
     * @param initialBalance Initial balance
     * @throws AccountServiceException if validation fails
     */
    private void validateAccountCreation(String bankCode, String branchCode, String accountType,
                                         BigDecimal initialBalance) throws AccountServiceException {
        
        if (!ValidationUtil.isValidBankCode(bankCode)) {
            throw new AccountServiceException(ValidationUtil.getBankCodeError(bankCode));
        }
        
        if (!ValidationUtil.isValidBranchCode(branchCode)) {
            throw new AccountServiceException(ValidationUtil.getBranchCodeError(branchCode));
        }
        
        if (!ValidationUtil.isValidAccountType(accountType)) {
            throw new AccountServiceException("Invalid account type. Must be SAVINGS, CHECKING, or BUSINESS");
        }
        
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountServiceException("Initial balance cannot be negative");
        }
    }
    
    /**
     * Generates a unique account number
     * 
     * @param bankCode Bank code
     * @param branchCode Branch code
     * @return Full account number (XXX-XXXX-XXXXXXXXXX)
     */
    private String generateAccountNumber(String bankCode, String branchCode) {
        // Generate 10-digit random account number
        long accountNum = (long) (Math.random() * 10000000000L);
        String accountDigits = String.format("%010d", accountNum);
        
        return bankCode + "-" + branchCode + "-" + accountDigits;
    }
    
    /**
     * Custom exception for account service errors
     */
    public static class AccountServiceException extends Exception {
        public AccountServiceException(String message) {
            super(message);
        }
    }
}
