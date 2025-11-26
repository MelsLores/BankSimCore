package com.banksim.model;

import java.time.LocalDateTime;

/**
 * AuditLog entity for tracking security and compliance events.
 * Records all significant actions performed by users in the system.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class AuditLog {
    
    private Integer logId;
    private Integer userId;
    private String action;
    private String entityType;
    private Integer entityId;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor
     */
    public AuditLog() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor for audit log creation
     * 
     * @param userId User who performed the action
     * @param action Action performed
     * @param entityType Type of entity affected
     * @param entityId ID of the affected entity
     */
    public AuditLog(Integer userId, String action, String entityType, Integer entityId) {
        this();
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    // Getters and Setters
    
    /**
     * Gets the log ID
     * @return Log ID
     */
    public Integer getLogId() {
        return logId;
    }
    
    /**
     * Sets the log ID
     * @param logId Log ID
     */
    public void setLogId(Integer logId) {
        this.logId = logId;
    }
    
    /**
     * Gets the user ID
     * @return User ID who performed the action
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
     * Gets the action performed
     * @return Action name (e.g., "LOGIN", "CREATE_ACCOUNT", "TRANSFER")
     */
    public String getAction() {
        return action;
    }
    
    /**
     * Sets the action performed
     * @param action Action name
     */
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * Gets the entity type
     * @return Entity type (e.g., "USER", "ACCOUNT", "TRANSACTION")
     */
    public String getEntityType() {
        return entityType;
    }
    
    /**
     * Sets the entity type
     * @param entityType Entity type
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    /**
     * Gets the entity ID
     * @return Entity ID
     */
    public Integer getEntityId() {
        return entityId;
    }
    
    /**
     * Sets the entity ID
     * @param entityId Entity ID
     */
    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }
    
    /**
     * Gets the old value (before change)
     * @return Old value as JSON string
     */
    public String getOldValue() {
        return oldValue;
    }
    
    /**
     * Sets the old value
     * @param oldValue Old value as JSON string
     */
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    
    /**
     * Gets the new value (after change)
     * @return New value as JSON string
     */
    public String getNewValue() {
        return newValue;
    }
    
    /**
     * Sets the new value
     * @param newValue New value as JSON string
     */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
    
    /**
     * Gets the IP address
     * @return Client IP address
     */
    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
     * Sets the IP address
     * @param ipAddress Client IP address
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    /**
     * Gets the user agent
     * @return Browser/client user agent string
     */
    public String getUserAgent() {
        return userAgent;
    }
    
    /**
     * Sets the user agent
     * @param userAgent Browser/client user agent string
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    /**
     * Gets the creation timestamp
     * @return Created date/time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp
     * @param createdAt Created date/time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "AuditLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", action='" + action + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", ipAddress='" + ipAddress + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
