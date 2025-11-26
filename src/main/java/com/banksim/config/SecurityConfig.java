package com.banksim.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Security configuration for JWT, BCrypt, and session management.
 * Provides centralized access to security-related configuration parameters.
 * 
 * Thread-safe singleton implementation.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class SecurityConfig {
    
    private static SecurityConfig instance;
    private Properties properties;
    
    // JWT Configuration
    private String jwtSecret;
    private long jwtExpiration;
    private long jwtRefreshExpiration;
    
    // BCrypt Configuration
    private int bcryptStrength;
    
    // Session Configuration
    private long sessionTimeout;
    
    // Security Configuration
    private int maxFailedAttempts;
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private SecurityConfig() {
        loadProperties();
        initializeSecuritySettings();
    }
    
    /**
     * Gets the singleton instance of SecurityConfig
     * Thread-safe lazy initialization
     * 
     * @return SecurityConfig instance
     */
    public static synchronized SecurityConfig getInstance() {
        if (instance == null) {
            instance = new SecurityConfig();
        }
        return instance;
    }
    
    /**
     * Loads security configuration from application.properties file
     */
    private void loadProperties() {
        properties = new Properties();
        
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
            
            if (input == null) {
                input = new FileInputStream("src/main/resources/application.properties");
            }
            
            properties.load(input);
            System.out.println("[SecurityConfig] Security properties loaded successfully");
            
        } catch (IOException e) {
            System.err.println("[SecurityConfig] Error loading properties: " + e.getMessage());
            System.err.println("[SecurityConfig] Using default security configuration");
            
            // Set default properties
            setDefaultProperties();
        }
    }
    
    /**
     * Sets default security properties
     */
    private void setDefaultProperties() {
        properties.setProperty("jwt.secret", "DefaultSecretKey_ChangeInProduction_BankSim2024!");
        properties.setProperty("jwt.expiration", "3600000"); // 1 hour
        properties.setProperty("jwt.refresh.expiration", "86400000"); // 24 hours
        properties.setProperty("security.bcrypt.strength", "10");
        properties.setProperty("security.session.timeout", "1800000"); // 30 minutes
        properties.setProperty("security.max.failed.attempts", "5");
    }
    
    /**
     * Initializes security settings from loaded properties
     */
    private void initializeSecuritySettings() {
        try {
            jwtSecret = properties.getProperty("jwt.secret");
            jwtExpiration = Long.parseLong(properties.getProperty("jwt.expiration", "3600000"));
            jwtRefreshExpiration = Long.parseLong(properties.getProperty("jwt.refresh.expiration", "86400000"));
            bcryptStrength = Integer.parseInt(properties.getProperty("security.bcrypt.strength", "10"));
            sessionTimeout = Long.parseLong(properties.getProperty("security.session.timeout", "1800000"));
            maxFailedAttempts = Integer.parseInt(properties.getProperty("security.max.failed.attempts", "5"));
            
            // Validate settings
            validateSettings();
            
            System.out.println("[SecurityConfig] Security settings initialized:");
            System.out.println("  - JWT Expiration: " + (jwtExpiration / 1000 / 60) + " minutes");
            System.out.println("  - BCrypt Strength: " + bcryptStrength);
            System.out.println("  - Session Timeout: " + (sessionTimeout / 1000 / 60) + " minutes");
            System.out.println("  - Max Failed Attempts: " + maxFailedAttempts);
            
        } catch (NumberFormatException e) {
            System.err.println("[SecurityConfig] Error parsing security settings: " + e.getMessage());
            throw new RuntimeException("Invalid security configuration", e);
        }
    }
    
    /**
     * Validates security settings
     */
    private void validateSettings() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters long");
        }
        
        if (bcryptStrength < 4 || bcryptStrength > 31) {
            throw new IllegalStateException("BCrypt strength must be between 4 and 31");
        }
        
        if (jwtExpiration <= 0) {
            throw new IllegalStateException("JWT expiration must be positive");
        }
        
        if (sessionTimeout <= 0) {
            throw new IllegalStateException("Session timeout must be positive");
        }
    }
    
    /**
     * Gets the JWT secret key
     * 
     * @return JWT secret key
     */
    public String getJwtSecret() {
        return jwtSecret;
    }
    
    /**
     * Gets the JWT token expiration time in milliseconds
     * 
     * @return JWT expiration time
     */
    public long getJwtExpiration() {
        return jwtExpiration;
    }
    
    /**
     * Gets the JWT refresh token expiration time in milliseconds
     * 
     * @return JWT refresh expiration time
     */
    public long getJwtRefreshExpiration() {
        return jwtRefreshExpiration;
    }
    
    /**
     * Gets the BCrypt hashing strength (number of rounds)
     * 
     * @return BCrypt strength (4-31)
     */
    public int getBcryptStrength() {
        return bcryptStrength;
    }
    
    /**
     * Gets the session timeout in milliseconds
     * 
     * @return Session timeout
     */
    public long getSessionTimeout() {
        return sessionTimeout;
    }
    
    /**
     * Gets the maximum number of failed login attempts before account lock
     * 
     * @return Max failed attempts
     */
    public int getMaxFailedAttempts() {
        return maxFailedAttempts;
    }
    
    /**
     * Gets a configuration property value
     * 
     * @param key Property key
     * @return Property value or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Gets a configuration property value with default
     * 
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Checks if a feature is enabled
     * 
     * @param featureName Feature name
     * @return true if enabled, false otherwise
     */
    public boolean isFeatureEnabled(String featureName) {
        String value = properties.getProperty("feature." + featureName + ".enabled", "true");
        return Boolean.parseBoolean(value);
    }
}
