package com.banksim.repository;

import com.banksim.config.DatabaseConfig;
import com.banksim.model.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity data access operations.
 * Handles all database interactions for user authentication and management.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class UserRepository {
    
    private final DatabaseConfig dbConfig;
    
    public UserRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Creates a new user in the database
     * 
     * @param user User entity to create
     * @return Created user with generated ID
     * @throws SQLException if database error occurs
     */
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, salt, role, is_active, is_locked, failed_login_attempts) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING user_id, created_at, updated_at";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());
            stmt.setString(5, user.getRole().name());
            stmt.setBoolean(6, user.isActive());
            stmt.setBoolean(7, user.isLocked());
            stmt.setInt(8, user.getFailedLoginAttempts());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user.setUserId(rs.getInt("user_id"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            
            return user;
        }
    }
    
    /**
     * Finds user by ID
     * 
     * @param userId User ID
     * @return Optional containing user if found
     * @throws SQLException if database error occurs
     */
    public Optional<User> findById(Integer userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Finds user by username
     * 
     * @param username Username
     * @return Optional containing user if found
     * @throws SQLException if database error occurs
     */
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Finds user by email
     * 
     * @param email Email address
     * @return Optional containing user if found
     * @throws SQLException if database error occurs
     */
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Updates user information
     * 
     * @param user User with updated information
     * @return Updated user
     * @throws SQLException if database error occurs
     */
    public User update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, password_hash = ?, salt = ?, " +
                     "role = ?, is_active = ?, is_locked = ?, failed_login_attempts = ?, last_login = ? " +
                     "WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());
            stmt.setString(5, user.getRole().name());
            stmt.setBoolean(6, user.isActive());
            stmt.setBoolean(7, user.isLocked());
            stmt.setInt(8, user.getFailedLoginAttempts());
            
            if (user.getLastLogin() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(user.getLastLogin()));
            } else {
                stmt.setNull(9, Types.TIMESTAMP);
            }
            
            stmt.setInt(10, user.getUserId());
            
            stmt.executeUpdate();
            return user;
        }
    }
    
    /**
     * Updates last login timestamp
     * 
     * @param userId User ID
     * @throws SQLException if database error occurs
     */
    public void updateLastLogin(Integer userId) throws SQLException {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Increments failed login attempts
     * 
     * @param userId User ID
     * @throws SQLException if database error occurs
     */
    public void incrementFailedLoginAttempts(Integer userId) throws SQLException {
        String sql = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1 WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Resets failed login attempts
     * 
     * @param userId User ID
     * @throws SQLException if database error occurs
     */
    public void resetFailedLoginAttempts(Integer userId) throws SQLException {
        String sql = "UPDATE users SET failed_login_attempts = 0 WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Locks user account
     * 
     * @param userId User ID
     * @throws SQLException if database error occurs
     */
    public void lockAccount(Integer userId) throws SQLException {
        String sql = "UPDATE users SET is_locked = true WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Unlocks user account
     * 
     * @param userId User ID
     * @throws SQLException if database error occurs
     */
    public void unlockAccount(Integer userId) throws SQLException {
        String sql = "UPDATE users SET is_locked = false, failed_login_attempts = 0 WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Finds all users with pagination
     * 
     * @param limit Number of records to return
     * @param offset Number of records to skip
     * @return List of users
     * @throws SQLException if database error occurs
     */
    public List<User> findAll(int limit, int offset) throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    /**
     * Counts total number of users
     * 
     * @return Total user count
     * @throws SQLException if database error occurs
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
        }
    }
    
    /**
     * Checks if username already exists
     * 
     * @param username Username to check
     * @return true if exists, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
        }
    }
    
    /**
     * Checks if user exists by ID
     * 
     * @param userId User ID to check
     * @return true if exists, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean existsById(Integer userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
        }
    }
    
    /**
     * Checks if email already exists
     * 
     * @param email Email to check
     * @return true if exists, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
        }
    }
    
    /**
     * Maps ResultSet to User entity
     * 
     * @param rs ResultSet from query
     * @return User entity
     * @throws SQLException if mapping error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setSalt(rs.getString("salt"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setLocked(rs.getBoolean("is_locked"));
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        return user;
    }
}
