package com.banksim.util;

import com.banksim.config.SecurityConfig;
import java.util.regex.Pattern;

/**
 * Validation utility for banking business rules.
 * Validates account numbers, personal keys, and other banking-specific data.
 * 
 * Implements validation rules from the 12 test cases specification:
 * - Bank code: 3 digits
 * - Branch code: 4 digits
 * - Account number: 10 digits
 * - Personal key: Minimum 8 characters (alphanumeric with at least one uppercase, one lowercase, one digit)
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class ValidationUtil {
    
    private static final SecurityConfig config = SecurityConfig.getInstance();
    
    // Regex patterns from configuration
    private static final Pattern BANK_CODE_PATTERN;
    private static final Pattern BRANCH_CODE_PATTERN;
    private static final Pattern ACCOUNT_NUMBER_PATTERN;
    private static final Pattern PERSONAL_KEY_PATTERN;
    
    // Email and phone patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$" // E.164 international format
    );
    
    static {
        // Load patterns from configuration with defaults
        String bankCodeRegex = config.getProperty("validation.bank.code.pattern", "^[0-9]{3}$");
        String branchCodeRegex = config.getProperty("validation.branch.code.pattern", "^[0-9]{4}$");
        String accountNumberRegex = config.getProperty("validation.account.number.pattern", "^[0-9]{10}$");
        String personalKeyRegex = config.getProperty("validation.personal.key.pattern", 
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
        
        BANK_CODE_PATTERN = Pattern.compile(bankCodeRegex);
        BRANCH_CODE_PATTERN = Pattern.compile(branchCodeRegex);
        ACCOUNT_NUMBER_PATTERN = Pattern.compile(accountNumberRegex);
        PERSONAL_KEY_PATTERN = Pattern.compile(personalKeyRegex);
    }
    
    /**
     * Validates bank code format (3 digits)
     * 
     * @param bankCode Bank code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidBankCode(String bankCode) {
        return bankCode != null && BANK_CODE_PATTERN.matcher(bankCode).matches();
    }
    
    /**
     * Validates branch code format (4 digits)
     * 
     * @param branchCode Branch code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidBranchCode(String branchCode) {
        return branchCode != null && BRANCH_CODE_PATTERN.matcher(branchCode).matches();
    }
    
    /**
     * Validates account number format (10 digits)
     * 
     * @param accountNumber Account number to validate (without bank and branch codes)
     * @return true if valid, false otherwise
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches();
    }
    
    /**
     * Validates full account number format (XXX-XXXX-XXXXXXXXXX)
     * 
     * @param fullAccountNumber Complete account number with separators
     * @return true if valid, false otherwise
     */
    public static boolean isValidFullAccountNumber(String fullAccountNumber) {
        if (fullAccountNumber == null) {
            return false;
        }
        
        String[] parts = fullAccountNumber.split("-");
        if (parts.length != 3) {
            return false;
        }
        
        return isValidBankCode(parts[0]) && 
               isValidBranchCode(parts[1]) && 
               isValidAccountNumber(parts[2]);
    }
    
    /**
     * Validates personal key format
     * Must be at least 8 characters with at least one uppercase, one lowercase, and one digit
     * 
     * @param personalKey Personal key to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPersonalKey(String personalKey) {
        return personalKey != null && PERSONAL_KEY_PATTERN.matcher(personalKey).matches();
    }
    
    /**
     * Validates email format
     * 
     * @param email Email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates phone number format (E.164 international format)
     * 
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validates username format
     * Must be 3-50 characters, alphanumeric and underscore/dot only
     * 
     * @param username Username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9._]+$");
    }
    
    /**
     * Validates customer code format
     * Must start with "CUST" followed by digits
     * 
     * @param customerCode Customer code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCustomerCode(String customerCode) {
        return customerCode != null && customerCode.matches("^CUST\\d{3,}$");
    }
    
    /**
     * Validates amount is positive
     * 
     * @param amount Amount to validate
     * @return true if positive, false otherwise
     */
    public static boolean isPositiveAmount(double amount) {
        return amount > 0;
    }
    
    /**
     * Validates amount is within range
     * 
     * @param amount Amount to validate
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return true if within range, false otherwise
     */
    public static boolean isAmountInRange(double amount, double min, double max) {
        return amount >= min && amount <= max;
    }
    
    /**
     * Validates service request type
     * 
     * @param serviceType Service type to validate
     * @return true if valid service type, false otherwise
     */
    public static boolean isValidServiceType(String serviceType) {
        if (serviceType == null) {
            return false;
        }
        
        String normalized = serviceType.toUpperCase();
        return normalized.equals("CHECKBOOK") || 
               normalized.equals("STATEMENT") || 
               normalized.equals("CARD");
    }
    
    /**
     * Validates account type
     * 
     * @param accountType Account type to validate
     * @return true if valid account type, false otherwise
     */
    public static boolean isValidAccountType(String accountType) {
        if (accountType == null) {
            return false;
        }
        
        String normalized = accountType.toUpperCase();
        return normalized.equals("SAVINGS") || 
               normalized.equals("CHECKING") || 
               normalized.equals("BUSINESS");
    }
    
    /**
     * Sanitizes input string to prevent injection attacks
     * 
     * @param input Input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"'%;()&+]", "");
    }
    
    /**
     * Validates string is not null or empty
     * 
     * @param value String to validate
     * @return true if not null and not empty, false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates string length is within range
     * 
     * @param value String to validate
     * @param minLength Minimum length
     * @param maxLength Maximum length
     * @return true if length is within range, false otherwise
     */
    public static boolean isLengthInRange(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Gets validation error message for bank code
     * 
     * @param bankCode Bank code that failed validation
     * @return Error message
     */
    public static String getBankCodeError(String bankCode) {
        if (bankCode == null || bankCode.isEmpty()) {
            return "Bank code is required";
        }
        if (!bankCode.matches("^[0-9]+$")) {
            return "Bank code must contain only digits";
        }
        if (bankCode.length() != 3) {
            return "Bank code must be exactly 3 digits";
        }
        return "Invalid bank code format";
    }
    
    /**
     * Gets validation error message for branch code
     * 
     * @param branchCode Branch code that failed validation
     * @return Error message
     */
    public static String getBranchCodeError(String branchCode) {
        if (branchCode == null || branchCode.isEmpty()) {
            return "Branch code is required";
        }
        if (!branchCode.matches("^[0-9]+$")) {
            return "Branch code must contain only digits";
        }
        if (branchCode.length() != 4) {
            return "Branch code must be exactly 4 digits";
        }
        return "Invalid branch code format";
    }
    
    /**
     * Gets validation error message for account number
     * 
     * @param accountNumber Account number that failed validation
     * @return Error message
     */
    public static String getAccountNumberError(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return "Account number is required";
        }
        if (!accountNumber.matches("^[0-9]+$")) {
            return "Account number must contain only digits";
        }
        if (accountNumber.length() != 10) {
            return "Account number must be exactly 10 digits";
        }
        return "Invalid account number format";
    }
    
    /**
     * Gets validation error message for personal key
     * 
     * @param personalKey Personal key that failed validation
     * @return Error message
     */
    public static String getPersonalKeyError(String personalKey) {
        if (personalKey == null || personalKey.isEmpty()) {
            return "Personal key is required";
        }
        if (personalKey.length() < 8) {
            return "Personal key must be at least 8 characters";
        }
        if (!personalKey.matches(".*[A-Z].*")) {
            return "Personal key must contain at least one uppercase letter";
        }
        if (!personalKey.matches(".*[a-z].*")) {
            return "Personal key must contain at least one lowercase letter";
        }
        if (!personalKey.matches(".*\\d.*")) {
            return "Personal key must contain at least one digit";
        }
        if (!personalKey.matches("^[A-Za-z\\d]+$")) {
            return "Personal key must contain only letters and digits";
        }
        return "Invalid personal key format";
    }
}
