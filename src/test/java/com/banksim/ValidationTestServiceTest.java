package com.banksim;

import com.banksim.service.ValidationTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Tests for ValidationTestService
 * Covers all 12 equivalence class test cases
 * 
 * @author BankSim Team
 */
@DisplayName("Validation Test Service - 12 Test Cases")
class ValidationTestServiceTest {
    
    private ValidationTestService service;
    
    @BeforeEach
    void setUp() {
        service = new ValidationTestService();
    }
    
    @Test
    @DisplayName("Should run all 12 test cases successfully")
    void testRunAllTests() {
        List<Map<String, Object>> results = service.runAllTests();
        
        assertNotNull(results);
        assertEquals(12, results.size(), "Should have exactly 12 test results");
        
        // Verify all tests have required fields
        results.forEach(result -> {
            assertThat(result).containsKeys(
                "testCase", "description", "bankCode", "branchCode",
                "accountNumber", "personalKey", "expectedValid", 
                "passed", "status", "message"
            );
        });
    }
    
    @Test
    @DisplayName("TC01: Valid bank code, branch code, account number, and personal key")
    void testCase01_AllValid() {
        Map<String, Object> result = service.runTestCase01();
        
        assertEquals("TC01", result.get("testCase"));
        assertTrue((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("123", result.get("bankCode"));
        assertEquals("4567", result.get("branchCode"));
        assertEquals("1234567890", result.get("accountNumber"));
        assertEquals("Abcd1234", result.get("personalKey"));
    }
    
    @Test
    @DisplayName("TC02: Invalid bank code (only 2 digits)")
    void testCase02_InvalidBankCode() {
        Map<String, Object> result = service.runTestCase02();
        
        assertEquals("TC02", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"), "Test should pass because it correctly detects invalid bank code");
        assertEquals("PASS", result.get("status"));
        assertEquals("12", result.get("bankCode"));
    }
    
    @Test
    @DisplayName("TC03: Invalid branch code (5 digits instead of 4)")
    void testCase03_InvalidBranchCode() {
        Map<String, Object> result = service.runTestCase03();
        
        assertEquals("TC03", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("45678", result.get("branchCode"));
    }
    
    @Test
    @DisplayName("TC04: Invalid account number (9 digits instead of 10)")
    void testCase04_InvalidAccountNumber() {
        Map<String, Object> result = service.runTestCase04();
        
        assertEquals("TC04", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("123456789", result.get("accountNumber"));
    }
    
    @Test
    @DisplayName("TC05: Invalid personal key (less than 8 characters)")
    void testCase05_ShortPersonalKey() {
        Map<String, Object> result = service.runTestCase05();
        
        assertEquals("TC05", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("Abc123", result.get("personalKey"));
    }
    
    @Test
    @DisplayName("TC06: Invalid personal key (no uppercase letter)")
    void testCase06_NoUppercase() {
        Map<String, Object> result = service.runTestCase06();
        
        assertEquals("TC06", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("abcd1234", result.get("personalKey"));
    }
    
    @Test
    @DisplayName("TC07: Invalid personal key (no lowercase letter)")
    void testCase07_NoLowercase() {
        Map<String, Object> result = service.runTestCase07();
        
        assertEquals("TC07", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("ABCD1234", result.get("personalKey"));
    }
    
    @Test
    @DisplayName("TC08: Invalid personal key (no digit)")
    void testCase08_NoDigit() {
        Map<String, Object> result = service.runTestCase08();
        
        assertEquals("TC08", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("AbcdEfgh", result.get("personalKey"));
    }
    
    @Test
    @DisplayName("TC09: Invalid bank code (contains letters)")
    void testCase09_BankCodeWithLetters() {
        Map<String, Object> result = service.runTestCase09();
        
        assertEquals("TC09", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("12A", result.get("bankCode"));
    }
    
    @Test
    @DisplayName("TC10: Invalid branch code (contains letters)")
    void testCase10_BranchCodeWithLetters() {
        Map<String, Object> result = service.runTestCase10();
        
        assertEquals("TC10", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("45AB", result.get("branchCode"));
    }
    
    @Test
    @DisplayName("TC11: Invalid account number (contains letters)")
    void testCase11_AccountNumberWithLetters() {
        Map<String, Object> result = service.runTestCase11();
        
        assertEquals("TC11", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("12345ABC90", result.get("accountNumber"));
    }
    
    @Test
    @DisplayName("TC12: Invalid personal key (contains special characters)")
    void testCase12_SpecialCharacters() {
        Map<String, Object> result = service.runTestCase12();
        
        assertEquals("TC12", result.get("testCase"));
        assertFalse((Boolean) result.get("expectedValid"));
        assertTrue((Boolean) result.get("passed"));
        assertEquals("PASS", result.get("status"));
        assertEquals("Abcd@1234", result.get("personalKey"));
    }
    
    @ParameterizedTest(name = "Test Case {0} should have status {1}")
    @CsvSource({
        "TC01, PASS",
        "TC02, PASS",
        "TC03, PASS",
        "TC04, PASS",
        "TC05, PASS",
        "TC06, PASS",
        "TC07, PASS",
        "TC08, PASS",
        "TC09, PASS",
        "TC10, PASS",
        "TC11, PASS",
        "TC12, PASS"
    })
    @DisplayName("All test cases should pass")
    void testAllCasesPass(String testId, String expectedStatus) {
        List<Map<String, Object>> results = service.runAllTests();
        
        Map<String, Object> testResult = results.stream()
            .filter(r -> r.get("testCase").equals(testId))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Test case " + testId + " not found"));
        
        assertEquals(expectedStatus, testResult.get("status"));
    }
    
    @Test
    @DisplayName("All tests should have meaningful descriptions")
    void testDescriptions() {
        List<Map<String, Object>> results = service.runAllTests();
        
        results.forEach(result -> {
            String description = (String) result.get("description");
            assertNotNull(description);
            assertFalse(description.isEmpty());
            assertTrue(description.length() > 10, "Description should be meaningful");
        });
    }
    
    @Test
    @DisplayName("All tests should have meaningful messages")
    void testMessages() {
        List<Map<String, Object>> results = service.runAllTests();
        
        results.forEach(result -> {
            String message = (String) result.get("message");
            assertNotNull(message);
            assertFalse(message.isEmpty());
        });
    }
}
