package com.banksim.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database configuration using direct JDBC connections.
 * Singleton pattern for application-wide database access.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class DatabaseConfig {
    
    private static DatabaseConfig instance;
    private Properties properties;
    private String jdbcUrl;
    private String username;
    private String password;
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private DatabaseConfig() {
        try {
            loadProperties();
            initializeDatabase();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database configuration", e);
        }
    }
    
    /**
     * Gets the singleton instance
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    /**
     * Loads database configuration from application.properties
     */
    private void loadProperties() throws IOException {
        properties = new Properties();
        
        // Try to load from classpath
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                return;
            }
        }
        
        // Try to load from file system
        try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        }
    }
    
    /**
     * Initializes database connection parameters
     */
    private void initializeDatabase() {
        jdbcUrl = properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/banksim_db");
        username = properties.getProperty("db.username", "postgres");
        password = properties.getProperty("db.password", "postgres");
        
        // Load PostgreSQL driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL Driver not found. Add postgresql jar to classpath.");
        }
    }
    
    /**
     * Gets a database connection
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
    
    /**
     * Gets database URL
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }
    
    /**
     * Gets database username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Closes all resources
     */
    public void close() {
        // Nothing to close with DriverManager
        System.out.println("Database configuration closed");
    }
}
