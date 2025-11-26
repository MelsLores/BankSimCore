package com.banksim.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Customer entity representing bank customers.
 * Each customer is linked to a user account and can have multiple bank accounts.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class Customer {
    
    private Integer customerId;
    private Integer userId;
    private String customerCode;
    private String firstName;
    private String lastName;
    private String personalKeyHash;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String identificationNumber;
    private boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor
     */
    public Customer() {
        this.isVerified = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor for customer creation
     * 
     * @param userId Associated user ID
     * @param customerCode Unique customer code
     * @param firstName Customer first name
     * @param lastName Customer last name
     * @param personalKeyHash Hashed personal key
     */
    public Customer(Integer userId, String customerCode, String firstName, String lastName, String personalKeyHash) {
        this();
        this.userId = userId;
        this.customerCode = customerCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalKeyHash = personalKeyHash;
    }
    
    // Getters and Setters
    
    /**
     * Gets the customer ID
     * @return Customer ID
     */
    public Integer getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets the customer ID
     * @param customerId Customer ID
     */
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Gets the associated user ID
     * @return User ID
     */
    public Integer getUserId() {
        return userId;
    }
    
    /**
     * Sets the associated user ID
     * @param userId User ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the customer code
     * @return Customer code (e.g., CUST001)
     */
    public String getCustomerCode() {
        return customerCode;
    }
    
    /**
     * Sets the customer code
     * @param customerCode Unique customer code
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
    
    /**
     * Gets the first name
     * @return First name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the first name
     * @param firstName Customer first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gets the last name
     * @return Last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the last name
     * @param lastName Customer last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Gets the full name (first name + last name)
     * @return Full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Gets the personal key hash
     * @return BCrypt hashed personal key
     */
    public String getPersonalKeyHash() {
        return personalKeyHash;
    }
    
    /**
     * Sets the personal key hash
     * @param personalKeyHash Hashed personal key
     */
    public void setPersonalKeyHash(String personalKeyHash) {
        this.personalKeyHash = personalKeyHash;
    }
    
    /**
     * Gets the phone number
     * @return Phone number
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets the phone number
     * @param phone Customer phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * Gets the address
     * @return Customer address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Sets the address
     * @param address Customer address
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Gets the date of birth
     * @return Date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    /**
     * Sets the date of birth
     * @param dateOfBirth Customer date of birth
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    /**
     * Gets the identification number
     * @return Identification number (ID, passport, etc.)
     */
    public String getIdentificationNumber() {
        return identificationNumber;
    }
    
    /**
     * Sets the identification number
     * @param identificationNumber Government-issued ID number
     */
    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }
    
    /**
     * Checks if customer is verified
     * @return true if verified, false otherwise
     */
    public boolean isVerified() {
        return isVerified;
    }
    
    /**
     * Sets customer verified status
     * @param verified Verification status
     */
    public void setVerified(boolean verified) {
        isVerified = verified;
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
    
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerCode='" + customerCode + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", phone='" + phone + '\'' +
                ", isVerified=" + isVerified +
                '}';
    }
}
