/**
 * Banking Application Simulator
 * Project: Testing Procedures for Operational Issues
 * Sprint 3 - Develop
 * 
 * This application simulates a banking system that validates user input
 * based on equivalence classes and test cases designed in previous sprints.
 * 
 * Input Parameters:
 * - Bank Code: 3-digit code (numeric, 000-999)
 * - Branch Code: 4-digit code (numeric, 0000-9999)
 * - Account Number: 10-digit code (numeric)
 * - Personal Key: Alphanumeric, minimum 8 characters
 * - Order Value: "Checkbook" or "Statement"
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @date November 2025
 */

import java.util.Scanner;
import java.util.regex.Pattern;

public class BankingApplicationSimulator {
    
    // Constants for validation
    private static final int BANK_CODE_LENGTH = 3;
    private static final int BRANCH_CODE_LENGTH = 4;
    private static final int ACCOUNT_NUMBER_LENGTH = 10;
    private static final int KEY_MIN_LENGTH = 8;
    private static final String ORDER_CHECKBOOK = "Checkbook";
    private static final String ORDER_STATEMENT = "Statement";
    
    /**
     * Main method - Entry point of the application
     */
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("    BANKING APPLICATION SIMULATOR - DATA VALIDATION SYSTEM    ");
        System.out.println("===============================================================");
        System.out.println();
        
        // Execute automated test cases
        runAutomatedTests();
        
        // Interactive mode
        System.out.println("\n===============================================================");
        System.out.println("                     INTERACTIVE MODE                          ");
        System.out.println("===============================================================");
        
        Scanner scanner = new Scanner(System.in);
        boolean continueProcessing = true;
        
        while (continueProcessing) {
            System.out.println("\nWould you like to enter a transaction manually? (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();
            
            if (response.equals("Y")) {
                processInteractiveTransaction(scanner);
            } else if (response.equals("N")) {
                continueProcessing = false;
                System.out.println("\nThank you for using the Banking Application Simulator!");
            } else {
                System.out.println("Invalid input. Please enter Y or N.");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Executes the 12 automated test cases from the PDF
     */
    private static void runAutomatedTests() {
        System.out.println("Running 12 Automated Test Cases from Sprint 2...");
        System.out.println();
        
        // ========== VALID TEST CASES (SUCCESS EXPECTED) ==========
        
        // TC01: Valid transaction with correct data
        System.out.println("--------------------------------------------------------------");
        System.out.println("TC01: VALID - Transaction with correct data");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Valid transaction with all correct data");
        System.out.println("Expected Result: Request processed successfully");
        System.out.println();
        Transaction test01 = new Transaction("123", "4567", "1234567890", "Abc12345", "Checkbook");
        validateTransaction(test01, "TC01", "Valida 1");
        
        // TC07: Valid request for a statement
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC07: VALID - Statement request");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Valid request for a statement");
        System.out.println("Expected Result: Request processed successfully");
        System.out.println();
        Transaction test07 = new Transaction("999", "1111", "9876543210", "ZxY98765", "Statement");
        validateTransaction(test07, "TC07", "Valida 2");
        
        // TC08: Valid request for a checkbook
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC08: VALID - Checkbook request");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Valid request for a checkbook");
        System.out.println("Expected Result: Request processed successfully");
        System.out.println();
        Transaction test08 = new Transaction("555", "2222", "1111111111", "Key12345", "Checkbook");
        validateTransaction(test08, "TC08", "Valida 3");
        
        // TC12: Valid transaction with boundary values (minimums)
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC12: VALID - Boundary values (minimums)");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Valid transaction with minimum/edge values");
        System.out.println("Expected Result: Request processed successfully");
        System.out.println();
        Transaction test12 = new Transaction("000", "0000", "0000000000", "Edge1234", "Checkbook");
        validateTransaction(test12, "TC12", "Valida (Borde) 4");
        
        // ========== INVALID TEST CASES (ERROR EXPECTED) ==========
        
        // TC02: Invalid bank code (too short)
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC02: INVALID - Bank code too short");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Bank code with less than 3 digits");
        System.out.println("Expected Result: Error - invalid bank code");
        System.out.println();
        Transaction test02 = new Transaction("12", "4567", "1234567890", "Abc12345", "Statement");
        validateTransaction(test02, "TC02", "Banco (<3 digitos) 5");
        
        // TC03: Invalid branch code (non-numeric)
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC03: INVALID - Branch code non-numeric");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Branch code contains letters");
        System.out.println("Expected Result: Error - invalid branch code");
        System.out.println();
        Transaction test03 = new Transaction("123", "ABCD", "1234567890", "Abc12345", "Checkbook");
        validateTransaction(test03, "TC03", "Sucursal (no numerico) 6");
        
        // TC04: Invalid account number (too long)
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC04: INVALID - Account number too long");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Account number with more than 10 digits");
        System.out.println("Expected Result: Error - invalid account number");
        System.out.println();
        Transaction test04 = new Transaction("123", "4567", "1234567890123", "Abc12345", "Statement");
        validateTransaction(test04, "TC04", "Cuenta (>10 digitos) 7");
        
        // TC05: Invalid personal key (too short)
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC05: INVALID - Personal key too short");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Personal key with less than 8 characters");
        System.out.println("Expected Result: Error - invalid personal key");
        System.out.println();
        Transaction test05 = new Transaction("123", "4567", "1234567890", "A1", "Checkbook");
        validateTransaction(test05, "TC05", "Clave personal (demasiado corta) 8");
        
        // TC06: Invalid order value (not allowed type)
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC06: INVALID - Invalid order type");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Order value is not Checkbook or Statement");
        System.out.println("Expected Result: Error - invalid order type");
        System.out.println();
        Transaction test06 = new Transaction("123", "4567", "1234567890", "Abc12345", "Loan");
        validateTransaction(test06, "TC06", "Valor de Orden (no Checkbook/Statement) 9");
        
        // TC09: Invalid bank code (non-numeric)
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC09: INVALID - Bank code non-numeric");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Bank code contains letters");
        System.out.println("Expected Result: Error - invalid bank code");
        System.out.println();
        Transaction test09 = new Transaction("ABC", "4567", "1234567890", "Abc12345", "Statement");
        validateTransaction(test09, "TC09", "Banco (no numerico) 10");
        
        // TC10: Invalid branch code (too short)
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC10: INVALID - Branch code too short");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Branch code with less than 4 digits");
        System.out.println("Expected Result: Error - invalid branch code");
        System.out.println();
        Transaction test10 = new Transaction("123", "45", "1234567890", "Abc12345", "Checkbook");
        validateTransaction(test10, "TC10", "Sucursal (<4 digitos) 11");
        
        // TC11: Invalid account number (non-numeric)
        System.out.println("\n--------------------------------------------------------------");
        System.out.println("TC11: INVALID - Account number non-numeric");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Description: Account number contains letters");
        System.out.println("Expected Result: Error - invalid account number");
        System.out.println();
        Transaction test11 = new Transaction("123", "4567", "ABCDE12345", "Abc12345", "Statement");
        validateTransaction(test11, "TC11", "Cuenta (no numerico) 12");
        
        // Summary
        System.out.println("\n===============================================================");
        System.out.println("              ALL 12 TEST CASES EXECUTED SUCCESSFULLY          ");
        System.out.println("===============================================================");
    }
    
    /**
     * Validates a transaction based on equivalence classes
     */
    private static void validateTransaction(Transaction transaction, String testId, String equivalenceClass) {
        System.out.println("Test ID: " + testId);
        System.out.println("Equivalence Class: " + equivalenceClass);
        System.out.println("Input Data:");
        System.out.println("  - Bank Code: " + transaction.bankCode);
        System.out.println("  - Branch Code: " + transaction.branchCode);
        System.out.println("  - Account Number: " + transaction.accountNumber);
        System.out.println("  - Personal Key: " + transaction.personalKey);
        System.out.println("  - Order Value: " + transaction.orderValue);
        System.out.println();
        
        ValidationResult result = new ValidationResult();
        
        // Validate Bank Code
        if (!validateBankCode(transaction.bankCode, result)) {
            printResult(result);
            return;
        }
        
        // Validate Branch Code
        if (!validateBranchCode(transaction.branchCode, result)) {
            printResult(result);
            return;
        }
        
        // Validate Account Number
        if (!validateAccountNumber(transaction.accountNumber, result)) {
            printResult(result);
            return;
        }
        
        // Validate Personal Key
        if (!validatePersonalKey(transaction.personalKey, result)) {
            printResult(result);
            return;
        }
        
        // Validate Order Value
        if (!validateOrderValue(transaction.orderValue, result)) {
            printResult(result);
            return;
        }
        
        // All validations passed
        result.isValid = true;
        result.message = "Request processed successfully";
        result.details = "Confirmation message displayed: " + transaction.orderValue + " request approved";
        
        printResult(result);
    }
    
    /**
     * Validates the bank code
     * Valid: 3-digit numeric code (000-999)
     */
    private static boolean validateBankCode(String bankCode, ValidationResult result) {
        // Check if null or empty
        if (bankCode == null || bankCode.trim().isEmpty()) {
            result.isValid = false;
            result.message = "Error: invalid bank code";
            result.details = "Bank code cannot be empty";
            return false;
        }
        
        // Check if numeric
        if (!Pattern.matches("\\d+", bankCode)) {
            result.isValid = false;
            result.message = "Error: invalid bank code";
            result.details = "Bank code must be numeric";
            return false;
        }
        
        // Check length
        if (bankCode.length() != BANK_CODE_LENGTH) {
            result.isValid = false;
            result.message = "Error: invalid bank code";
            result.details = "Bank code must be exactly " + BANK_CODE_LENGTH + " digits (received: " + bankCode.length() + ")";
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates the branch code
     * Valid: 4-digit numeric code (0000-9999)
     */
    private static boolean validateBranchCode(String branchCode, ValidationResult result) {
        // Check if null or empty
        if (branchCode == null || branchCode.trim().isEmpty()) {
            result.isValid = false;
            result.message = "Error: invalid branch code";
            result.details = "Branch code cannot be empty";
            return false;
        }
        
        // Check if numeric
        if (!Pattern.matches("\\d+", branchCode)) {
            result.isValid = false;
            result.message = "Error: invalid branch code";
            result.details = "Branch code must be numeric";
            return false;
        }
        
        // Check length
        if (branchCode.length() != BRANCH_CODE_LENGTH) {
            result.isValid = false;
            result.message = "Error: invalid branch code";
            result.details = "Branch code must be exactly " + BRANCH_CODE_LENGTH + " digits (received: " + branchCode.length() + ")";
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates the account number
     * Valid: 10-digit numeric code
     */
    private static boolean validateAccountNumber(String accountNumber, ValidationResult result) {
        // Check if null or empty
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            result.isValid = false;
            result.message = "Error: invalid account number";
            result.details = "Account number cannot be empty";
            return false;
        }
        
        // Check if numeric
        if (!Pattern.matches("\\d+", accountNumber)) {
            result.isValid = false;
            result.message = "Error: invalid account number";
            result.details = "Account number must be numeric";
            return false;
        }
        
        // Check length
        if (accountNumber.length() != ACCOUNT_NUMBER_LENGTH) {
            result.isValid = false;
            result.message = "Error: invalid account number";
            result.details = "Account number must be exactly " + ACCOUNT_NUMBER_LENGTH + " digits (received: " + accountNumber.length() + ")";
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates the personal key
     * Valid: Alphanumeric, minimum 8 characters
     */
    private static boolean validatePersonalKey(String personalKey, ValidationResult result) {
        // Check if null or empty
        if (personalKey == null || personalKey.trim().isEmpty()) {
            result.isValid = false;
            result.message = "Error: invalid personal key";
            result.details = "Personal key cannot be empty";
            return false;
        }
        
        // Check minimum length
        if (personalKey.length() < KEY_MIN_LENGTH) {
            result.isValid = false;
            result.message = "Error: invalid personal key";
            result.details = "Personal key must be at least " + KEY_MIN_LENGTH + " characters (received: " + personalKey.length() + ")";
            return false;
        }
        
        // Check if alphanumeric
        if (!Pattern.matches("[a-zA-Z0-9]+", personalKey)) {
            result.isValid = false;
            result.message = "Error: invalid personal key";
            result.details = "Personal key must be alphanumeric only";
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates the order value
     * Valid: "Checkbook" or "Statement"
     */
    private static boolean validateOrderValue(String orderValue, ValidationResult result) {
        // Check if null or empty
        if (orderValue == null || orderValue.trim().isEmpty()) {
            result.isValid = false;
            result.message = "Error: invalid order type";
            result.details = "Order value cannot be empty";
            return false;
        }
        
        // Check valid values
        if (!orderValue.equals(ORDER_CHECKBOOK) && !orderValue.equals(ORDER_STATEMENT)) {
            result.isValid = false;
            result.message = "Error: invalid order type";
            result.details = "Order value must be '" + ORDER_CHECKBOOK + "' or '" + ORDER_STATEMENT + "' (received: '" + orderValue + "')";
            return false;
        }
        
        return true;
    }
    
    /**
     * Prints the validation result
     */
    private static void printResult(ValidationResult result) {
        System.out.println("Validation Result:");
        
        if (result.isValid) {
            System.out.println("  Status: SUCCESS");
            System.out.println("  " + result.message);
            if (result.details != null && !result.details.isEmpty()) {
                System.out.println("  " + result.details);
            }
        } else {
            System.out.println("  Status: FAILURE");
            System.out.println("  " + result.message);
            if (result.details != null && !result.details.isEmpty()) {
                System.out.println("  " + result.details);
            }
        }
        System.out.println();
    }
    
    /**
     * Processes an interactive transaction with user input
     */
    private static void processInteractiveTransaction(Scanner scanner) {
        System.out.println("\nPlease enter the following transaction details:");
        
        System.out.print("Bank Code (3 digits, numeric): ");
        String bankCode = scanner.nextLine().trim();
        
        System.out.print("Branch Code (4 digits, numeric): ");
        String branchCode = scanner.nextLine().trim();
        
        System.out.print("Account Number (10 digits, numeric): ");
        String accountNumber = scanner.nextLine().trim();
        
        System.out.print("Personal Key (min 8 alphanumeric characters): ");
        String personalKey = scanner.nextLine().trim();
        
        System.out.print("Order Value (Checkbook or Statement): ");
        String orderValue = scanner.nextLine().trim();
        
        System.out.println();
        Transaction transaction = new Transaction(bankCode, branchCode, accountNumber, personalKey, orderValue);
        validateTransaction(transaction, "INTERACTIVE", "User Input");
    }
    
    /**
     * Inner class to represent a transaction
     */
    private static class Transaction {
        String bankCode;
        String branchCode;
        String accountNumber;
        String personalKey;
        String orderValue;
        
        public Transaction(String bankCode, String branchCode, String accountNumber, String personalKey, String orderValue) {
            this.bankCode = bankCode;
            this.branchCode = branchCode;
            this.accountNumber = accountNumber;
            this.personalKey = personalKey;
            this.orderValue = orderValue;
        }
    }
    
    /**
     * Inner class to store validation results
     */
    private static class ValidationResult {
        boolean isValid;
        String message;
        String details;
        
        public ValidationResult() {
            this.isValid = false;
            this.message = "";
            this.details = "";
        }
    }
}
