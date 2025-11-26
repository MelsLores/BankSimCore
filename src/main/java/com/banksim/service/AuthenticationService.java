package com.banksim.service;

import com.banksim.config.SecurityConfig;
import com.banksim.model.Customer;
import com.banksim.model.User;
import com.banksim.repository.CustomerRepository;
import com.banksim.repository.UserRepository;
import com.banksim.util.JwtUtil;
import com.banksim.util.PasswordUtil;
import com.banksim.util.ValidationUtil;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication service for user login, registration, and JWT token management.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final SecurityConfig securityConfig;
    
    public AuthenticationService() {
        this.userRepository = new UserRepository();
        this.customerRepository = new CustomerRepository();
        this.securityConfig = SecurityConfig.getInstance();
    }
    
    /**
     * Authenticates a user and generates JWT token
     * 
     * @param username Username or email
     * @param password Plain text password
     * @return Map containing token and user information
     * @throws AuthenticationException if authentication fails
     */
    public Map<String, Object> login(String username, String password) throws AuthenticationException {
        try {
            // Find user by username or email
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                userOpt = userRepository.findByEmail(username);
            }
            
            if (!userOpt.isPresent()) {
                throw new AuthenticationException("Invalid username or password");
            }
            
            User user = userOpt.get();
            
            // Check if account is locked
            if (user.isLocked()) {
                throw new AuthenticationException("Account is locked. Please contact support.");
            }
            
            // Check if account is active
            if (!user.isActive()) {
                throw new AuthenticationException("Account is inactive");
            }
            
            // Verify password
            boolean passwordValid = PasswordUtil.verifyPassword(password, user.getPasswordHash());
            
            if (!passwordValid) {
                // Increment failed attempts
                userRepository.incrementFailedLoginAttempts(user.getUserId());
                user.incrementFailedLoginAttempts();
                
                // Lock account if max attempts reached
                if (user.getFailedLoginAttempts() >= securityConfig.getMaxFailedAttempts()) {
                    userRepository.lockAccount(user.getUserId());
                    throw new AuthenticationException("Account locked due to too many failed attempts");
                }
                
                throw new AuthenticationException("Invalid username or password");
            }
            
            // Reset failed attempts on successful login
            userRepository.resetFailedLoginAttempts(user.getUserId());
            userRepository.updateLastLogin(user.getUserId());
            
            // Generate JWT token
            String accessToken = JwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole().name());
            String refreshToken = JwtUtil.generateRefreshToken(user.getUserId(), user.getUsername(), user.getRole().name());
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());
            response.put("expiresIn", securityConfig.getJwtExpiration());
            
            return response;
            
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during login: " + e.getMessage());
        }
    }
    
    /**
     * Registers a new user and customer
     * 
     * @param username Username
     * @param email Email address
     * @param password Plain text password
     * @param firstName First name
     * @param lastName Last name
     * @param personalKey Personal key for additional security
     * @param phone Phone number
     * @return Map containing token and user information
     * @throws AuthenticationException if registration fails
     */
    public Map<String, Object> register(String username, String email, String password,
                                        String firstName, String lastName, String personalKey,
                                        String phone) throws AuthenticationException {
        try {
            // Validate inputs
            validateRegistrationInputs(username, email, password, personalKey);
            
            // Check if username exists
            if (userRepository.existsByUsername(username)) {
                throw new AuthenticationException("Username already exists");
            }
            
            // Check if email exists
            if (userRepository.existsByEmail(email)) {
                throw new AuthenticationException("Email already exists");
            }
            
            // Hash password
            String passwordHash = PasswordUtil.hashPassword(password);
            String salt = PasswordUtil.generateSalt();
            
            // Create user
            User user = new User(username, email, passwordHash, salt, User.Role.CUSTOMER);
            user = userRepository.create(user);
            
            // Generate customer code
            String customerCode = customerRepository.generateCustomerCode();
            
            // Hash personal key
            String personalKeyHash = PasswordUtil.hashPassword(personalKey);
            
            // Create customer
            Customer customer = new Customer(user.getUserId(), customerCode, firstName, lastName, personalKeyHash);
            customer.setPhone(phone);
            customer = customerRepository.create(customer);
            
            // Generate JWT token
            String accessToken = JwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole().name());
            String refreshToken = JwtUtil.generateRefreshToken(user.getUserId(), user.getUsername(), user.getRole().name());
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());
            response.put("customerId", customer.getCustomerId());
            response.put("customerCode", customer.getCustomerCode());
            response.put("expiresIn", securityConfig.getJwtExpiration());
            
            return response;
            
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during registration: " + e.getMessage());
        }
    }
    
    /**
     * Refreshes an access token using a refresh token
     * 
     * @param refreshToken Refresh token
     * @return Map containing new access token
     * @throws AuthenticationException if refresh fails
     */
    public Map<String, Object> refreshToken(String refreshToken) throws AuthenticationException {
        try {
            // Validate refresh token
            if (!JwtUtil.validateToken(refreshToken)) {
                throw new AuthenticationException("Invalid or expired refresh token");
            }
            
            // Extract user information
            Integer userId = JwtUtil.getUserId(refreshToken);
            String username = JwtUtil.getUsername(refreshToken);
            String role = JwtUtil.getRole(refreshToken);
            
            if (userId == null || username == null || role == null) {
                throw new AuthenticationException("Invalid token claims");
            }
            
            // Verify user still exists and is active
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                throw new AuthenticationException("User not found");
            }
            
            User user = userOpt.get();
            if (!user.isActive() || user.isLocked()) {
                throw new AuthenticationException("User account is not active");
            }
            
            // Generate new access token
            String newAccessToken = JwtUtil.generateToken(userId, username, role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("expiresIn", securityConfig.getJwtExpiration());
            
            return response;
            
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during token refresh: " + e.getMessage());
        }
    }
    
    /**
     * Validates JWT token
     * 
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);
    }
    
    /**
     * Extracts user ID from token
     * 
     * @param token JWT token
     * @return User ID
     */
    public Integer getUserIdFromToken(String token) {
        return JwtUtil.getUserId(token);
    }
    
    /**
     * Changes user password
     * 
     * @param userId User ID
     * @param oldPassword Current password
     * @param newPassword New password
     * @throws AuthenticationException if password change fails
     */
    public void changePassword(Integer userId, String oldPassword, String newPassword) throws AuthenticationException {
        try {
            // Find user
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                throw new AuthenticationException("User not found");
            }
            
            User user = userOpt.get();
            
            // Verify old password
            if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
                throw new AuthenticationException("Current password is incorrect");
            }
            
            // Validate new password
            if (!PasswordUtil.isStrongPassword(newPassword)) {
                throw new AuthenticationException("New password does not meet strength requirements");
            }
            
            // Hash new password
            String newPasswordHash = PasswordUtil.hashPassword(newPassword);
            String newSalt = PasswordUtil.generateSalt();
            
            user.setPasswordHash(newPasswordHash);
            user.setSalt(newSalt);
            
            userRepository.update(user);
            
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during password change: " + e.getMessage());
        }
    }
    
    /**
     * Validates registration inputs
     * 
     * @param username Username
     * @param email Email
     * @param password Password
     * @param personalKey Personal key
     * @throws AuthenticationException if validation fails
     */
    private void validateRegistrationInputs(String username, String email, String password, String personalKey) 
            throws AuthenticationException {
        
        if (!ValidationUtil.isValidUsername(username)) {
            throw new AuthenticationException("Invalid username format. Must be 3-50 alphanumeric characters.");
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            throw new AuthenticationException("Invalid email format");
        }
        
        if (!PasswordUtil.isStrongPassword(password)) {
            throw new AuthenticationException(
                "Password must be at least 8 characters with uppercase, lowercase, and digit"
            );
        }
        
        if (!ValidationUtil.isValidPersonalKey(personalKey)) {
            throw new AuthenticationException(ValidationUtil.getPersonalKeyError(personalKey));
        }
    }
    
    /**
     * Custom exception for authentication errors
     */
    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}
