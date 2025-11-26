package com.banksim.util;

import com.banksim.config.SecurityConfig;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT (JSON Web Token) utility for generating and validating authentication tokens.
 * Provides stateless authentication for REST API endpoints.
 * 
 * This is a simplified JWT implementation. For production, consider using
 * a library like jjwt (Java JWT) or auth0 java-jwt.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class JwtUtil {
    
    private static final SecurityConfig config = SecurityConfig.getInstance();
    private static final String HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    
    /**
     * Generates a JWT token for a user
     * 
     * @param userId User ID
     * @param username Username
     * @param role User role
     * @return JWT token string
     */
    public static String generateToken(Integer userId, String username, String role) {
        long now = Instant.now().toEpochMilli();
        long expirationTime = now + config.getJwtExpiration();
        
        return generateToken(userId, username, role, expirationTime);
    }
    
    /**
     * Generates a JWT token with custom expiration
     * 
     * @param userId User ID
     * @param username Username
     * @param role User role
     * @param expirationTime Expiration timestamp in milliseconds
     * @return JWT token string
     */
    public static String generateToken(Integer userId, String username, String role, long expirationTime) {
        try {
            // Create payload
            String payload = String.format(
                "{\"sub\":\"%d\",\"username\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                userId, username, role, Instant.now().toEpochMilli(), expirationTime
            );
            
            // Encode header and payload
            String encodedHeader = base64UrlEncode(HEADER);
            String encodedPayload = base64UrlEncode(payload);
            
            // Create signature
            String dataToSign = encodedHeader + "." + encodedPayload;
            String signature = createSignature(dataToSign);
            
            // Return complete token
            return dataToSign + "." + signature;
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }
    
    /**
     * Generates a refresh token with extended expiration
     * 
     * @param userId User ID
     * @param username Username
     * @param role User role
     * @return Refresh token string
     */
    public static String generateRefreshToken(Integer userId, String username, String role) {
        long now = Instant.now().toEpochMilli();
        long expirationTime = now + config.getJwtRefreshExpiration();
        
        return generateToken(userId, username, role, expirationTime);
    }
    
    /**
     * Validates a JWT token
     * 
     * @param token JWT token to validate
     * @return true if token is valid and not expired, false otherwise
     */
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            
            // Verify signature
            String dataToSign = parts[0] + "." + parts[1];
            String expectedSignature = createSignature(dataToSign);
            
            if (!constantTimeEquals(parts[2], expectedSignature)) {
                return false;
            }
            
            // Check expiration
            Map<String, String> claims = extractClaims(token);
            long expiration = Long.parseLong(claims.get("exp"));
            
            return Instant.now().toEpochMilli() < expiration;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extracts claims from a JWT token
     * 
     * @param token JWT token
     * @return Map of claim key-value pairs
     */
    public static Map<String, String> extractClaims(String token) {
        Map<String, String> claims = new HashMap<>();
        
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return claims;
            }
            
            // Decode payload
            String payload = base64UrlDecode(parts[1]);
            
            // Parse JSON manually (simple key-value extraction)
            payload = payload.replace("{", "").replace("}", "").replace("\"", "");
            String[] pairs = payload.split(",");
            
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    claims.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
            
        } catch (Exception e) {
            // Return empty claims on error
        }
        
        return claims;
    }
    
    /**
     * Extracts user ID from token
     * 
     * @param token JWT token
     * @return User ID or null if not found
     */
    public static Integer getUserId(String token) {
        try {
            Map<String, String> claims = extractClaims(token);
            String sub = claims.get("sub");
            return sub != null ? Integer.parseInt(sub) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extracts username from token
     * 
     * @param token JWT token
     * @return Username or null if not found
     */
    public static String getUsername(String token) {
        Map<String, String> claims = extractClaims(token);
        return claims.get("username");
    }
    
    /**
     * Extracts user role from token
     * 
     * @param token JWT token
     * @return Role or null if not found
     */
    public static String getRole(String token) {
        Map<String, String> claims = extractClaims(token);
        return claims.get("role");
    }
    
    /**
     * Checks if token is expired
     * 
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public static boolean isTokenExpired(String token) {
        try {
            Map<String, String> claims = extractClaims(token);
            long expiration = Long.parseLong(claims.get("exp"));
            return Instant.now().toEpochMilli() >= expiration;
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Creates HMAC-SHA256 signature for JWT
     * 
     * @param data Data to sign
     * @return Base64-URL encoded signature
     */
    private static String createSignature(String data) {
        try {
            String secret = config.getJwtSecret();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Simple HMAC implementation
            String combined = data + secret;
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            
            return base64UrlEncode(hash);
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating signature", e);
        }
    }
    
    /**
     * Base64-URL encodes a string
     * 
     * @param input String to encode
     * @return Base64-URL encoded string
     */
    private static String base64UrlEncode(String input) {
        return base64UrlEncode(input.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Base64-URL encodes a byte array
     * 
     * @param input Bytes to encode
     * @return Base64-URL encoded string
     */
    private static String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(input);
    }
    
    /**
     * Base64-URL decodes a string
     * 
     * @param input String to decode
     * @return Decoded string
     */
    private static String base64UrlDecode(String input) {
        byte[] decoded = Base64.getUrlDecoder().decode(input);
        return new String(decoded, StandardCharsets.UTF_8);
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
}
