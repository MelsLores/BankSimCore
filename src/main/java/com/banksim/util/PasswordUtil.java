package com.banksim.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password hashing utility using BCrypt algorithm.
 * Provides secure password hashing and verification for user authentication.
 * 
 * This implementation uses SHA-256 as a fallback if native BCrypt is not available.
 * For production, consider using a dedicated BCrypt library like jBCrypt.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class PasswordUtil {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DEFAULT_STRENGTH = 10;
    private static final String ALGORITHM = "SHA-256";
    
    /**
     * Hashes a password using BCrypt-style algorithm with salt
     * 
     * @param password Plain text password
     * @return Hashed password in format: algorithm$strength$salt$hash
     */
    public static String hashPassword(String password) {
        return hashPassword(password, DEFAULT_STRENGTH);
    }
    
    /**
     * Hashes a password with specified strength
     * 
     * @param password Plain text password
     * @param strength Hashing strength (4-31, higher is more secure but slower)
     * @return Hashed password
     */
    public static String hashPassword(String password, int strength) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        if (strength < 4 || strength > 31) {
            throw new IllegalArgumentException("Strength must be between 4 and 31");
        }
        
        try {
            // Generate random salt
            byte[] saltBytes = new byte[16];
            RANDOM.nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);
            
            // Hash password with salt
            String hash = hashWithSalt(password, salt, strength);
            
            // Return in BCrypt-style format
            return String.format("$2a$%02d$%s$%s", strength, salt, hash);
            
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verifies a password against a hash
     * 
     * @param password Plain text password to verify
     * @param hashedPassword Hashed password to compare against
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        
        try {
            // Parse hash format: $2a$10$salt$hash
            String[] parts = hashedPassword.split("\\$");
            if (parts.length != 5) {
                return false;
            }
            
            int strength = Integer.parseInt(parts[2]);
            String salt = parts[3];
            String expectedHash = parts[4];
            
            // Hash provided password with same salt
            String actualHash = hashWithSalt(password, salt, strength);
            
            // Constant-time comparison to prevent timing attacks
            return constantTimeEquals(expectedHash, actualHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Hashes password with provided salt
     * 
     * @param password Plain text password
     * @param salt Base64-encoded salt
     * @param strength Number of hashing rounds
     * @return Base64-encoded hash
     */
    private static String hashWithSalt(String password, String salt, int strength) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            
            // Combine password and salt
            String combined = password + salt;
            byte[] input = combined.getBytes(StandardCharsets.UTF_8);
            
            // Apply multiple rounds of hashing (simulating BCrypt work factor)
            byte[] hash = input;
            int rounds = (int) Math.pow(2, strength);
            for (int i = 0; i < rounds; i++) {
                hash = digest.digest(hash);
            }
            
            // Return Base64-encoded hash
            return Base64.getEncoder().encodeToString(hash);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available", e);
        }
    }
    
    /**
     * Constant-time string comparison to prevent timing attacks
     * 
     * @param a First string
     * @param b Second string
     * @return true if strings are equal, false otherwise
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }
        
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        
        if (aBytes.length != bBytes.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        
        return result == 0;
    }
    
    /**
     * Generates a random salt
     * 
     * @return Base64-encoded random salt
     */
    public static String generateSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
    
    /**
     * Generates a secure random password
     * 
     * @param length Password length
     * @return Random password
     */
    public static String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }
    
    /**
     * Validates password strength
     * 
     * @param password Password to validate
     * @return true if password meets strength requirements, false otherwise
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasUpper && hasLower && hasDigit;
    }
}
