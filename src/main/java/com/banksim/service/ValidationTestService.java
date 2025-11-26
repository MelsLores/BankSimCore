package com.banksim.service;

import com.banksim.util.ValidationUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validation service for the 12 test cases from PDF specification.
 * Implements equivalence class testing for banking operations.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class ValidationTestService {
    
    /**
     * Runs all 12 test cases
     * 
     * @return List of test results
     */
    public List<Map<String, Object>> runAllTests() {
        List<Map<String, Object>> results = new ArrayList<>();
        
        results.add(runTestCase01());
        results.add(runTestCase02());
        results.add(runTestCase03());
        results.add(runTestCase04());
        results.add(runTestCase05());
        results.add(runTestCase06());
        results.add(runTestCase07());
        results.add(runTestCase08());
        results.add(runTestCase09());
        results.add(runTestCase10());
        results.add(runTestCase11());
        results.add(runTestCase12());
        
        return results;
    }
    
    /**
     * TC01: Valid bank code, branch code, account number, and personal key
     * Expected: PASS (Valid)
     */
    public Map<String, Object> runTestCase01() {
        String bankCode = "123";
        String branchCode = "4567";
        String accountNumber = "1234567890";
        String personalKey = "Abcd1234";
        
        boolean bankValid = ValidationUtil.isValidBankCode(bankCode);
        boolean branchValid = ValidationUtil.isValidBranchCode(branchCode);
        boolean accountValid = ValidationUtil.isValidAccountNumber(accountNumber);
        boolean keyValid = ValidationUtil.isValidPersonalKey(personalKey);
        
        boolean passed = bankValid && branchValid && accountValid && keyValid;
        
        return createTestResult(
            "TC01",
            "Valid bank code, branch code, account number, and personal key",
            bankCode, branchCode, accountNumber, personalKey,
            true,
            passed,
            passed ? "All validations passed" : "Validation failed"
        );
    }
    
    /**
     * TC02: Invalid bank code (only 2 digits)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase02() {
        String bankCode = "12";
        String branchCode = "4567";
        String accountNumber = "1234567890";
        String personalKey = "Abcd1234";
        
        boolean bankValid = ValidationUtil.isValidBankCode(bankCode);
        boolean passed = !bankValid;
        
        return createTestResult(
            "TC02",
            "Invalid bank code (only 2 digits)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getBankCodeError(bankCode)
        );
    }
    
    /**
     * TC03: Invalid branch code (5 digits instead of 4)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase03() {
        String bankCode = "123";
        String branchCode = "45678";
        String accountNumber = "1234567890";
        String personalKey = "Abcd1234";
        
        boolean branchValid = ValidationUtil.isValidBranchCode(branchCode);
        boolean passed = !branchValid;
        
        return createTestResult(
            "TC03",
            "Invalid branch code (5 digits instead of 4)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getBranchCodeError(branchCode)
        );
    }
    
    /**
     * TC04: Invalid account number (9 digits instead of 10)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase04() {
        String bankCode = "123";
        String branchCode = "4567";
        String accountNumber = "123456789";
        String personalKey = "Abcd1234";
        
        boolean accountValid = ValidationUtil.isValidAccountNumber(accountNumber);
        boolean passed = !accountValid;
        
        return createTestResult(
            "TC04",
            "Invalid account number (9 digits instead of 10)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getAccountNumberError(accountNumber)
        );
    }
    
    /**
     * TC05: Invalid personal key (less than 8 characters)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase05() {
        String bankCode = "123";
        String branchCode = "4567";
        String accountNumber = "1234567890";
        String personalKey = "Abc123";
        
        boolean keyValid = ValidationUtil.isValidPersonalKey(personalKey);
        boolean passed = !keyValid;
        
        return createTestResult(
            "TC05",
            "Invalid personal key (less than 8 characters)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getPersonalKeyError(personalKey)
        );
    }
    
    /**
     * TC06: Invalid personal key (no uppercase letter)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase06() {
        String bankCode = "123";
        String branchCode = "4567";
        String accountNumber = "1234567890";
        String personalKey = "abcd1234";
        
        boolean keyValid = ValidationUtil.isValidPersonalKey(personalKey);
        boolean passed = !keyValid;
        
        return createTestResult(
            "TC06",
            "Invalid personal key (no uppercase letter)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getPersonalKeyError(personalKey)
        );
    }
    
    /**
     * TC07: Invalid personal key (no lowercase letter)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase07() {
        String bankCode = "123";
        String branchCode = "4567";
        String accountNumber = "1234567890";
        String personalKey = "ABCD1234";
        
        boolean keyValid = ValidationUtil.isValidPersonalKey(personalKey);
        boolean passed = !keyValid;
        
        return createTestResult(
            "TC07",
            "Invalid personal key (no lowercase letter)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getPersonalKeyError(personalKey)
        );
    }
    
    /**
     * TC08: Invalid personal key (no digit)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase08() {
        String bankCode = "123";
        String branchCode = "4567";
        String accountNumber = "1234567890";
        String personalKey = "AbcdEfgh";
        
        boolean keyValid = ValidationUtil.isValidPersonalKey(personalKey);
        boolean passed = !keyValid;
        
        return createTestResult(
            "TC08",
            "Invalid personal key (no digit)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getPersonalKeyError(personalKey)
        );
    }
    
    /**
     * TC09: Invalid bank code (contains letters)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase09() {
        String bankCode = "12A";
        String branchCode = "4567";
        String accountNumber = "1234567890";
        String personalKey = "Abcd1234";
        
        boolean bankValid = ValidationUtil.isValidBankCode(bankCode);
        boolean passed = !bankValid;
        
        return createTestResult(
            "TC09",
            "Invalid bank code (contains letters)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getBankCodeError(bankCode)
        );
    }
    
    /**
     * TC10: Invalid branch code (contains letters)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase10() {
        String bankCode = "123";
        String branchCode = "45AB";
        String accountNumber = "1234567890";
        String personalKey = "Abcd1234";
        
        boolean branchValid = ValidationUtil.isValidBranchCode(branchCode);
        boolean passed = !branchValid;
        
        return createTestResult(
            "TC10",
            "Invalid branch code (contains letters)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getBranchCodeError(branchCode)
        );
    }
    
    /**
     * TC11: Invalid account number (contains letters)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase11() {
        String bankCode = "123";
        String branchCode = "4567";
        String accountNumber = "12345ABC90";
        String personalKey = "Abcd1234";
        
        boolean accountValid = ValidationUtil.isValidAccountNumber(accountNumber);
        boolean passed = !accountValid;
        
        return createTestResult(
            "TC11",
            "Invalid account number (contains letters)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getAccountNumberError(accountNumber)
        );
    }
    
    /**
     * TC12: Invalid personal key (contains special characters)
     * Expected: FAIL (Invalid)
     */
    public Map<String, Object> runTestCase12() {
        String bankCode = "123";
        String branchCode = "4567";
        String accountNumber = "1234567890";
        String personalKey = "Abcd@1234";
        
        boolean keyValid = ValidationUtil.isValidPersonalKey(personalKey);
        boolean passed = !keyValid;
        
        return createTestResult(
            "TC12",
            "Invalid personal key (contains special characters)",
            bankCode, branchCode, accountNumber, personalKey,
            false,
            passed,
            ValidationUtil.getPersonalKeyError(personalKey)
        );
    }
    
    /**
     * Creates a test result map
     */
    private Map<String, Object> createTestResult(String testCase, String description,
                                                 String bankCode, String branchCode,
                                                 String accountNumber, String personalKey,
                                                 boolean expectedValid, boolean passed, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("testCase", testCase);
        result.put("description", description);
        result.put("bankCode", bankCode);
        result.put("branchCode", branchCode);
        result.put("accountNumber", accountNumber);
        result.put("personalKey", personalKey);
        result.put("expectedValid", expectedValid);
        result.put("passed", passed);
        result.put("status", passed ? "PASS" : "FAIL");
        result.put("message", message);
        
        return result;
    }
}
