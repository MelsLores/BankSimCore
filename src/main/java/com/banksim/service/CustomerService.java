package com.banksim.service;

import com.banksim.model.Customer;
import com.banksim.repository.CustomerRepository;
import com.banksim.repository.UserRepository;
import com.banksim.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for customer management operations.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    
    /**
     * Constructor
     */
    public CustomerService() {
        this.customerRepository = new CustomerRepository();
        this.userRepository = new UserRepository();
    }
    
    /**
     * Creates a new customer
     * 
     * @param userId User ID
     * @param firstName First name
     * @param lastName Last name
     * @param email Email
     * @param phone Phone
     * @param address Address
     * @param dateOfBirth Date of birth
     * @param personalKey Personal key for verification
     * @return Created customer
     * @throws CustomerServiceException if validation fails or customer already exists
     */
    public Customer createCustomer(Integer userId, String firstName, String lastName,
                                   String email, String phone, String address,
                                   LocalDate dateOfBirth, String personalKey) 
            throws CustomerServiceException {
        
        // Validate inputs
        validateCustomerInputs(firstName, lastName, email, phone, personalKey);
        
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new CustomerServiceException("User not found with ID: " + userId);
        }
        
        // Check if customer already exists for this user
        Optional<Customer> existingCustomer = customerRepository.findByUserId(userId);
        if (existingCustomer.isPresent()) {
            throw new CustomerServiceException("Customer already exists for this user");
        }
        
        // Generate customer code
        String customerCode = customerRepository.generateCustomerCode();
        
        // Create customer
        Customer customer = new Customer();
        customer.setUserId(userId);
        customer.setCustomerCode(customerCode);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setDateOfBirth(dateOfBirth);
        customer.setPersonalKeyHash(personalKey); // Store hashed personal key
        customer.setVerified(false); // Default unverified
        
        Customer created = customerRepository.create(customer);
        return created;
    }
    
    /**
     * Gets customer by ID
     * 
     * @param customerId Customer ID
     * @return Customer
     * @throws CustomerServiceException if customer not found
     */
    public Customer getCustomerById(Integer customerId) throws CustomerServiceException {
        Optional<Customer> customer = customerRepository.findById(customerId);
        return customer.orElseThrow(() -> 
            new CustomerServiceException("Customer not found with ID: " + customerId));
    }
    
    /**
     * Gets customer by user ID
     * 
     * @param userId User ID
     * @return Customer
     * @throws CustomerServiceException if customer not found
     */
    public Customer getCustomerByUserId(Integer userId) throws CustomerServiceException {
        Optional<Customer> customer = customerRepository.findByUserId(userId);
        return customer.orElseThrow(() -> 
            new CustomerServiceException("Customer not found for user ID: " + userId));
    }
    
    /**
     * Gets customer by customer code
     * 
     * @param customerCode Customer code (e.g., CUST001)
     * @return Customer
     * @throws CustomerServiceException if customer not found
     */
    public Customer getCustomerByCode(String customerCode) throws CustomerServiceException {
        Optional<Customer> customer = customerRepository.findByCustomerCode(customerCode);
        return customer.orElseThrow(() -> 
            new CustomerServiceException("Customer not found with code: " + customerCode));
    }
    
    /**
     * Gets all customers
     * 
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    /**
     * Updates customer information
     * 
     * @param customerId Customer ID
     * @param firstName First name
     * @param lastName Last name
     * @param email Email
     * @param phone Phone
     * @param address Address
     * @return Updated customer
     * @throws CustomerServiceException if validation fails or customer not found
     */
    public Customer updateCustomer(Integer customerId, String firstName, String lastName,
                                   String email, String phone, String address) 
            throws CustomerServiceException {
        
        // Get existing customer
        Customer customer = getCustomerById(customerId);
        
        // Validate inputs (skip personal key since it's not changed here)
        if (firstName != null && !firstName.isEmpty()) {
            customer.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            customer.setLastName(lastName);
        }
        if (phone != null) {
            customer.setPhone(phone);
        }
        if (address != null) {
            customer.setAddress(address);
        }
        
        customerRepository.update(customer);
        return customer;
    }
    
    /**
     * Updates customer personal key
     * 
     * @param customerId Customer ID
     * @param newPersonalKey New personal key
     * @return Updated customer
     * @throws CustomerServiceException if validation fails
     */
    public Customer updatePersonalKey(Integer customerId, String newPersonalKey) 
            throws CustomerServiceException {
        
        // Validate personal key
        if (!ValidationUtil.isValidPersonalKey(newPersonalKey)) {
            throw new CustomerServiceException(
                ValidationUtil.getPersonalKeyError(newPersonalKey));
        }
        
        // Get customer
        Customer customer = getCustomerById(customerId);
        
        // Update personal key (use hash)
        customer.setPersonalKeyHash(newPersonalKey);
        customerRepository.update(customer);
        
        return customer;
    }
    
    /**
     * Verifies a customer's personal key
     * 
     * @param customerId Customer ID
     * @param personalKey Personal key to verify
     * @return true if personal key matches
     * @throws CustomerServiceException if customer not found
     */
    public boolean verifyPersonalKey(Integer customerId, String personalKey) 
            throws CustomerServiceException {
        
        Customer customer = getCustomerById(customerId);
        return customer.getPersonalKeyHash().equals(personalKey);
    }
    
    /**
     * Marks customer as verified
     * 
     * @param customerId Customer ID
     * @return Updated customer
     * @throws CustomerServiceException if customer not found
     */
    public Customer markAsVerified(Integer customerId) throws CustomerServiceException {
        Customer customer = getCustomerById(customerId);
        customer.setVerified(true);
        customerRepository.update(customer);
        return customer;
    }
    
    /**
     * Deletes a customer
     * 
     * @param customerId Customer ID
     * @throws CustomerServiceException if customer not found
     */
    public void deleteCustomer(Integer customerId) throws CustomerServiceException {
        // Verify customer exists
        getCustomerById(customerId);
        
        // Delete customer
        customerRepository.delete(customerId);
    }
    
    /**
     * Validates customer input fields
     * 
     * @throws CustomerServiceException if validation fails
     */
    private void validateCustomerInputs(String firstName, String lastName,
                                        String email, String phone, String personalKey) 
            throws CustomerServiceException {
        
        // Validate first name
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new CustomerServiceException("First name is required");
        }
        if (firstName.length() < 2 || firstName.length() > 50) {
            throw new CustomerServiceException(
                "First name must be between 2 and 50 characters");
        }
        
        // Validate last name
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new CustomerServiceException("Last name is required");
        }
        if (lastName.length() < 2 || lastName.length() > 50) {
            throw new CustomerServiceException(
                "Last name must be between 2 and 50 characters");
        }
        
        // Validate email
        if (!ValidationUtil.isValidEmail(email)) {
            throw new CustomerServiceException("Invalid email format");
        }
        
        // Validate phone
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new CustomerServiceException("Invalid phone format");
        }
        
        // Validate personal key
        if (!ValidationUtil.isValidPersonalKey(personalKey)) {
            throw new CustomerServiceException(
                ValidationUtil.getPersonalKeyError(personalKey));
        }
    }
    
    /**
     * Custom exception for customer service errors
     */
    public static class CustomerServiceException extends Exception {
        public CustomerServiceException(String message) {
            super(message);
        }
    }
}
