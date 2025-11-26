package com.banksim.model;

import java.time.LocalDateTime;

/**
 * User entity representing authenticated users in the system.
 * Supports role-based access control (RBAC) with three roles: ADMIN, CUSTOMER, EMPLOYEE.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class User {
    
    /**
     * User role enumeration
     */
    public enum Role {
        ADMIN,
        CUSTOMER,
        EMPLOYEE
    }
    
    private Integer userId;
    private String username;
    private String email;
    private String passwordHash;
    private String salt;
    private Role role;
    private boolean isActive;
    private boolean isLocked;
    private int failedLoginAttempts;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor
     */
    public User() {
        this.isActive = true;
        this.isLocked = false;
        this.failedLoginAttempts = 0;
        this.role = Role.CUSTOMER;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor for user creation
     * 
     * @param username Unique username
     * @param email User email address
     * @param passwordHash BCrypt hashed password
     * @param salt Password salt
     * @param role User role
     */
    public User(String username, String email, String passwordHash, String salt, Role role) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
    }
    
    // Getters and Setters
    
    /**
     * Gets the user ID
     * @return User ID
     */
    public Integer getUserId() {
        return userId;
    }
    
    /**
     * Sets the user ID
     * @param userId User ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the username
     * @return Username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username
     * @param username Unique username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the email address
     * @return Email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email address
     * @param email User email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the password hash
     * @return BCrypt password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * Sets the password hash
     * @param passwordHash BCrypt hashed password
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    /**
     * Gets the password salt
     * @return Password salt
     */
    public String getSalt() {
        return salt;
    }
    
    /**
     * Sets the password salt
     * @param salt Password salt
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    /**
     * Gets the user role
     * @return User role (ADMIN, CUSTOMER, EMPLOYEE)
     */
    public Role getRole() {
        return role;
    }
    
    /**
     * Sets the user role
     * @param role User role
     */
    public void setRole(Role role) {
        this.role = role;
    }
    
    /**
     * Checks if user is active
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Sets user active status
     * @param active Active status
     */
    public void setActive(boolean active) {
        isActive = active;
    }
    
    /**
     * Checks if user account is locked
     * @return true if locked, false otherwise
     */
    public boolean isLocked() {
        return isLocked;
    }
    
    /**
     * Sets user account locked status
     * @param locked Locked status
     */
    public void setLocked(boolean locked) {
        isLocked = locked;
    }
    
    /**
     * Gets failed login attempts count
     * @return Number of failed attempts
     */
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    /**
     * Sets failed login attempts count
     * @param failedLoginAttempts Number of failed attempts
     */
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    /**
     * Increments failed login attempts
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }
    
    /**
     * Resets failed login attempts to zero
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }
    
    /**
     * Gets last login timestamp
     * @return Last login date/time
     */
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    /**
     * Sets last login timestamp
     * @param lastLogin Last login date/time
     */
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    /**
     * Gets creation timestamp
     * @return Created date/time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets creation timestamp
     * @param createdAt Created date/time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets last update timestamp
     * @return Updated date/time
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets last update timestamp
     * @param updatedAt Updated date/time
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Checks if user has admin privileges
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
    
    /**
     * Checks if user is a customer
     * @return true if user is customer, false otherwise
     */
    public boolean isCustomer() {
        return this.role == Role.CUSTOMER;
    }
    
    /**
     * Checks if user is an employee
     * @return true if user is employee, false otherwise
     */
    public boolean isEmployee() {
        return this.role == Role.EMPLOYEE;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                ", isLocked=" + isLocked +
                '}';
    }
}
