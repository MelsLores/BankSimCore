package com.banksim.repository;

import com.banksim.config.DatabaseConfig;
import com.banksim.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Customer entity data access operations.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class CustomerRepository {
    
    private final DatabaseConfig dbConfig;
    
    public CustomerRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Creates a new customer
     * 
     * @param customer Customer entity
     * @return Created customer with ID
     * @throws SQLException if error occurs
     */
    public Customer create(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (user_id, customer_code, first_name, last_name, personal_key_hash, " +
                     "phone, address, date_of_birth, identification_number, is_verified) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING customer_id, created_at, updated_at";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customer.getUserId());
            stmt.setString(2, customer.getCustomerCode());
            stmt.setString(3, customer.getFirstName());
            stmt.setString(4, customer.getLastName());
            stmt.setString(5, customer.getPersonalKeyHash());
            stmt.setString(6, customer.getPhone());
            stmt.setString(7, customer.getAddress());
            
            if (customer.getDateOfBirth() != null) {
                stmt.setDate(8, Date.valueOf(customer.getDateOfBirth()));
            } else {
                stmt.setNull(8, Types.DATE);
            }
            
            stmt.setString(9, customer.getIdentificationNumber());
            stmt.setBoolean(10, customer.isVerified());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                customer.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            
            return customer;
        }
    }
    
    /**
     * Finds customer by ID
     * 
     * @param customerId Customer ID
     * @return Optional with customer
     * @throws SQLException if error occurs
     */
    public Optional<Customer> findById(Integer customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Finds customer by user ID
     * 
     * @param userId User ID
     * @return Optional with customer
     * @throws SQLException if error occurs
     */
    public Optional<Customer> findByUserId(Integer userId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Finds customer by customer code
     * 
     * @param customerCode Customer code
     * @return Optional with customer
     * @throws SQLException if error occurs
     */
    public Optional<Customer> findByCustomerCode(String customerCode) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_code = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerCode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Updates customer information
     * 
     * @param customer Customer with updates
     * @return Updated customer
     * @throws SQLException if error occurs
     */
    public Customer update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, personal_key_hash = ?, " +
                     "phone = ?, address = ?, date_of_birth = ?, identification_number = ?, is_verified = ? " +
                     "WHERE customer_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getPersonalKeyHash());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            
            if (customer.getDateOfBirth() != null) {
                stmt.setDate(6, Date.valueOf(customer.getDateOfBirth()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setString(7, customer.getIdentificationNumber());
            stmt.setBoolean(8, customer.isVerified());
            stmt.setInt(9, customer.getCustomerId());
            
            stmt.executeUpdate();
            return customer;
        }
    }
    
    /**
     * Retrieves all customers
     * 
     * @return List of all customers
     * @throws SQLException if error occurs
     */
    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        
        return customers;
    }
    
    /**
     * Retrieves all customers with pagination
     * 
     * @param limit Records to return
     * @param offset Records to skip
     * @return List of customers
     * @throws SQLException if error occurs
     */
    public List<Customer> findAll(int limit, int offset) throws SQLException {
        String sql = "SELECT * FROM customers ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        
        return customers;
    }
    
    /**
     * Counts total customers
     * 
     * @return Total count
     * @throws SQLException if error occurs
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers";
        
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
     * Checks if customer code exists
     * 
     * @param customerCode Code to check
     * @return true if exists
     * @throws SQLException if error occurs
     */
    public boolean existsByCustomerCode(String customerCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE customer_code = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerCode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
        }
    }
    
    /**
     * Generates next customer code
     * 
     * @return Next customer code (e.g., CUST004)
     * @throws SQLException if error occurs
     */
    public String generateCustomerCode() throws SQLException {
        String sql = "SELECT customer_code FROM customers ORDER BY customer_id DESC LIMIT 1";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String lastCode = rs.getString("customer_code");
                int number = Integer.parseInt(lastCode.substring(4));
                return String.format("CUST%03d", number + 1);
            }
            
            return "CUST001";
        }
    }
    
    /**
     * Maps ResultSet to Customer entity
     * 
     * @param rs ResultSet
     * @return Customer entity
     * @throws SQLException if error occurs
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setUserId(rs.getInt("user_id"));
        customer.setCustomerCode(rs.getString("customer_code"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setPersonalKeyHash(rs.getString("personal_key_hash"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            customer.setDateOfBirth(dob.toLocalDate());
        }
        
        customer.setIdentificationNumber(rs.getString("identification_number"));
        customer.setVerified(rs.getBoolean("is_verified"));
        customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        customer.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        return customer;
    }
}
