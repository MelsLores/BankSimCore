package com.banksim.util;

import java.util.regex.Pattern;

/**
 * Validation utility for banking business rules.
 * Standalone version without external dependencies for testing.
 * 
 * Implements validation rules from the 12 test cases specification:
 * - Bank code: 3 digits
 * - Branch code: 4 digits
 * - Account number: 10 digits
 * - Personal key: Minimum 8 characters (alphanumeric with at least one uppercase, one lowercase, one digit)
 * 
 * @author BankSim Team
 * @version 2.0
 */
public class ValidationUtil {
    
    // Regex patterns for validation
    private static final Pattern BANK_CODE_PATTERN = Pattern.compile("^[0-9]{3}$");
    private static final Pattern BRANCH_CODE_PATTERN = Pattern.compile("^[0-9]{4}$");
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final Pattern PERSONAL_KEY_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    
    // Email and phone patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$" // E.164 international format
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    
    /**
     * Validates bank code format (3 digits)
     * 
     * @param bankCode Bank code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidBankCode(String bankCode) {
        return bankCode != null && !bankCode.trim().isEmpty() && BANK_CODE_PATTERN.matcher(bankCode).matches();
    }
    
    /**
     * Validates branch code format (4 digits)
     * 
     * @param branchCode Branch code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidBranchCode(String branchCode) {
        return branchCode != null && !branchCode.trim().isEmpty() && BRANCH_CODE_PATTERN.matcher(branchCode).matches();
    }
    
    /**
     * Validates account number format (10 digits)
     * 
     * @param accountNumber Account number to validate (without bank and branch codes)
     * @return true if valid, false otherwise
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && !accountNumber.trim().isEmpty() && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches();
    }
    
    /**
     * Validates personal key format
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - Only alphanumeric characters (no special chars)
     * 
     * @param personalKey Personal key to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPersonalKey(String personalKey) {
        return personalKey != null && !personalKey.trim().isEmpty() && PERSONAL_KEY_PATTERN.matcher(personalKey).matches();
    }
    
    /**
     * Validates username format
     * - 3-50 characters
     * - Only letters, numbers, and underscores
     * 
     * @param username Username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty() && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * Validates email format
     * 
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates phone number format (E.164 international format)
     * 
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && !phone.trim().isEmpty() && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Get error message for invalid bank code
     */
    public static String getBankCodeError(String bankCode) {
        if (bankCode == null || bankCode.trim().isEmpty()) {
            return "Bank code is required";
        }
        if (bankCode.length() != 3) {
            return "Bank code must be exactly 3 digits";
        }
        if (!bankCode.matches("[0-9]+")) {
            return "Bank code must contain only numeric digits";
        }
        return "Invalid bank code";
    }
    
    /**
     * Get error message for invalid branch code
     */
    public static String getBranchCodeError(String branchCode) {
        if (branchCode == null || branchCode.trim().isEmpty()) {
            return "Branch code is required";
        }
        if (branchCode.length() != 4) {
            return "Branch code must be exactly 4 digits";
        }
        if (!branchCode.matches("[0-9]+")) {
            return "Branch code must contain only numeric digits";
        }
        return "Invalid branch code";
    }
    
    /**
     * Get error message for invalid account number
     */
    public static String getAccountNumberError(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return "Account number is required";
        }
        if (accountNumber.length() != 10) {
            return "Account number must be exactly 10 digits";
        }
        if (!accountNumber.matches("[0-9]+")) {
            return "Account number must contain only numeric digits";
        }
        return "Invalid account number";
    }
    
    /**
     * Get error message for invalid personal key
     */
    public static String getPersonalKeyError(String personalKey) {
        if (personalKey == null || personalKey.trim().isEmpty()) {
            return "Personal key is required";
        }
        if (personalKey.length() < 8) {
            return "Personal key must be at least 8 characters long";
        }
        if (!personalKey.matches(".*[A-Z].*")) {
            return "Personal key must contain at least one uppercase letter";
        }
        if (!personalKey.matches(".*[a-z].*")) {
            return "Personal key must contain at least one lowercase letter";
        }
        if (!personalKey.matches(".*[0-9].*")) {
            return "Personal key must contain at least one digit";
        }
        if (!personalKey.matches("[A-Za-z0-9]+")) {
            return "Personal key must contain only alphanumeric characters (no special characters)";
        }
        return "Invalid personal key";
    }
}
