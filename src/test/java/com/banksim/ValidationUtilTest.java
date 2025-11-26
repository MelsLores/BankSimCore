package com.banksim;

import com.banksim.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil
 */
@DisplayName("Validation Utility Tests")
class ValidationUtilTest {
    
    // Bank Code Tests
    @ParameterizedTest(name = "Valid bank code: {0}")
    @ValueSource(strings = {"123", "456", "789", "000", "999"})
    @DisplayName("Should accept valid bank codes (3 digits)")
    void testValidBankCodes(String bankCode) {
        assertTrue(ValidationUtil.isValidBankCode(bankCode));
    }
    
    @ParameterizedTest(name = "Invalid bank code: {0}")
    @ValueSource(strings = {"12", "1234", "AB", "12A", "", "  "})
    @DisplayName("Should reject invalid bank codes")
    void testInvalidBankCodes(String bankCode) {
        assertFalse(ValidationUtil.isValidBankCode(bankCode));
    }
    
    // Branch Code Tests
    @ParameterizedTest(name = "Valid branch code: {0}")
    @ValueSource(strings = {"4567", "0000", "9999", "1234"})
    @DisplayName("Should accept valid branch codes (4 digits)")
    void testValidBranchCodes(String branchCode) {
        assertTrue(ValidationUtil.isValidBranchCode(branchCode));
    }
    
    @ParameterizedTest(name = "Invalid branch code: {0}")
    @ValueSource(strings = {"456", "45678", "ABCD", "45AB", "", "   "})
    @DisplayName("Should reject invalid branch codes")
    void testInvalidBranchCodes(String branchCode) {
        assertFalse(ValidationUtil.isValidBranchCode(branchCode));
    }
    
    // Account Number Tests
    @ParameterizedTest(name = "Valid account number: {0}")
    @ValueSource(strings = {"1234567890", "0000000000", "9999999999"})
    @DisplayName("Should accept valid account numbers (10 digits)")
    void testValidAccountNumbers(String accountNumber) {
        assertTrue(ValidationUtil.isValidAccountNumber(accountNumber));
    }
    
    @ParameterizedTest(name = "Invalid account number: {0}")
    @ValueSource(strings = {"123456789", "12345678901", "123456ABC0", "", "          "})
    @DisplayName("Should reject invalid account numbers")
    void testInvalidAccountNumbers(String accountNumber) {
        assertFalse(ValidationUtil.isValidAccountNumber(accountNumber));
    }
    
    // Personal Key Tests
    @ParameterizedTest(name = "Valid personal key: {0}")
    @ValueSource(strings = {"Abcd1234", "Password1", "Test123Pass", "MyKey999"})
    @DisplayName("Should accept valid personal keys")
    void testValidPersonalKeys(String personalKey) {
        assertTrue(ValidationUtil.isValidPersonalKey(personalKey));
    }
    
    @ParameterizedTest(name = "Invalid personal key: {0}")
    @ValueSource(strings = {
        "Abc123",        // Too short
        "abcd1234",      // No uppercase
        "ABCD1234",      // No lowercase
        "AbcdEfgh",      // No digit
        "Abcd@1234",     // Special character
        "",              // Empty
        "       "        // Whitespace
    })
    @DisplayName("Should reject invalid personal keys")
    void testInvalidPersonalKeys(String personalKey) {
        assertFalse(ValidationUtil.isValidPersonalKey(personalKey));
    }
    
    // Username Tests
    @Test
    @DisplayName("Should accept valid usernames")
    void testValidUsernames() {
        assertTrue(ValidationUtil.isValidUsername("melany"));
        assertTrue(ValidationUtil.isValidUsername("john123"));
        assertTrue(ValidationUtil.isValidUsername("user_name"));
        assertTrue(ValidationUtil.isValidUsername("a".repeat(50))); // Max length
    }
    
    @Test
    @DisplayName("Should reject invalid usernames")
    void testInvalidUsernames() {
        assertFalse(ValidationUtil.isValidUsername("ab")); // Too short
        assertFalse(ValidationUtil.isValidUsername("a".repeat(51))); // Too long
        assertFalse(ValidationUtil.isValidUsername("user@name")); // Invalid char
        assertFalse(ValidationUtil.isValidUsername("")); // Empty
        assertFalse(ValidationUtil.isValidUsername(null)); // Null
    }
    
    // Email Tests
    @Test
    @DisplayName("Should accept valid emails")
    void testValidEmails() {
        assertTrue(ValidationUtil.isValidEmail("melslores@outlook.es"));
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@domain.co.uk"));
    }
    
    @Test
    @DisplayName("Should reject invalid emails")
    void testInvalidEmails() {
        assertFalse(ValidationUtil.isValidEmail("invalid"));
        assertFalse(ValidationUtil.isValidEmail("@example.com"));
        assertFalse(ValidationUtil.isValidEmail("user@"));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail(null));
    }
    
    // Error Message Tests
    @Test
    @DisplayName("Should provide error messages for invalid inputs")
    void testErrorMessages() {
        assertThat(ValidationUtil.getBankCodeError("12"))
            .isNotEmpty()
            .contains("3");
        
        assertThat(ValidationUtil.getBranchCodeError("456"))
            .isNotEmpty()
            .contains("4");
        
        assertThat(ValidationUtil.getAccountNumberError("123456789"))
            .isNotEmpty()
            .contains("10");
        
        assertThat(ValidationUtil.getPersonalKeyError("abc123"))
            .isNotEmpty();
    }
}
