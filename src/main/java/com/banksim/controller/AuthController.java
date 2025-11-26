package com.banksim.controller;

import com.banksim.service.AuthenticationService;
import com.banksim.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication operations.
 * Handles login, registration, and JWT token management.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class AuthController implements HttpHandler {
    
    private final AuthenticationService authService;
    
    public AuthController() {
        this.authService = new AuthenticationService();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        // Handle preflight OPTIONS request
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        
        try {
            if (path.equals("/api/auth/login") && "POST".equals(method)) {
                handleLogin(exchange);
            } else if (path.equals("/api/auth/register") && "POST".equals(method)) {
                handleRegister(exchange);
            } else if (path.equals("/api/auth/refresh") && "POST".equals(method)) {
                handleRefreshToken(exchange);
            } else if (path.equals("/api/auth/change-password") && "POST".equals(method)) {
                handleChangePassword(exchange);
            } else {
                sendJsonResponse(exchange, 404, createErrorResponse("Endpoint not found"));
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/auth/login
     * Request: {"username": "user1", "password": "Pass1234"}
     * Response: {"success": true, "token": "jwt-token", "userId": 1}
     */
    private void handleLogin(HttpExchange exchange) throws IOException {
        Map<String, String> requestBody = parseJsonRequest(exchange);
        
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        
        if (username == null || password == null) {
            sendJsonResponse(exchange, 400, createErrorResponse("Username and password are required"));
            return;
        }
        
        try {
            Map<String, Object> loginResult = authService.login(username, password);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessToken", loginResult.get("accessToken"));
            response.put("refreshToken", loginResult.get("refreshToken"));
            response.put("userId", loginResult.get("userId"));
            response.put("username", loginResult.get("username"));
            response.put("email", loginResult.get("email"));
            response.put("role", loginResult.get("role"));
            response.put("expiresIn", loginResult.get("expiresIn"));
            response.put("message", "Login successful");
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (AuthenticationService.AuthenticationException e) {
            sendJsonResponse(exchange, 401, createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * POST /api/auth/register
     * Request: {"username": "newuser", "password": "Pass1234", "email": "user@email.com", 
     *           "firstName": "John", "lastName": "Doe", "phone": "1234567890", 
     *           "address": "123 Main St", "dateOfBirth": "1990-01-01", "personalKey": "Abcd1234"}
     * Response: {"success": true, "token": "jwt-token", "userId": 1}
     */
    private void handleRegister(HttpExchange exchange) throws IOException {
        Map<String, String> requestBody = parseJsonRequest(exchange);
        
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        String email = requestBody.get("email");
        String firstName = requestBody.get("firstName");
        String lastName = requestBody.get("lastName");
        String phone = requestBody.get("phone");
        String address = requestBody.get("address");
        String dateOfBirth = requestBody.get("dateOfBirth");
        String personalKey = requestBody.get("personalKey");
        
        try {
            Map<String, Object> registerResult = authService.register(username, email, password, 
                                               firstName, lastName, personalKey, phone);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessToken", registerResult.get("accessToken"));
            response.put("refreshToken", registerResult.get("refreshToken"));
            response.put("userId", registerResult.get("userId"));
            response.put("username", registerResult.get("username"));
            response.put("email", registerResult.get("email"));
            response.put("role", registerResult.get("role"));
            response.put("customerId", registerResult.get("customerId"));
            response.put("customerCode", registerResult.get("customerCode"));
            response.put("expiresIn", registerResult.get("expiresIn"));
            response.put("message", "Registration successful");
            
            sendJsonResponse(exchange, 201, response);
            
        } catch (AuthenticationService.AuthenticationException e) {
            sendJsonResponse(exchange, 400, createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * POST /api/auth/refresh
     * Request: {"token": "old-jwt-token"}
     * Response: {"success": true, "token": "new-jwt-token"}
     */
    private void handleRefreshToken(HttpExchange exchange) throws IOException {
        Map<String, String> requestBody = parseJsonRequest(exchange);
        
        String oldToken = requestBody.get("token");
        
        if (oldToken == null) {
            sendJsonResponse(exchange, 400, createErrorResponse("Token is required"));
            return;
        }
        
        try {
            Map<String, Object> refreshResult = authService.refreshToken(oldToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessToken", refreshResult.get("accessToken"));
            response.put("expiresIn", refreshResult.get("expiresIn"));
            response.put("message", "Token refreshed successfully");
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (AuthenticationService.AuthenticationException e) {
            sendJsonResponse(exchange, 401, createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * POST /api/auth/change-password
     * Headers: Authorization: Bearer {jwt-token}
     * Request: {"currentPassword": "OldPass1234", "newPassword": "NewPass5678"}
     * Response: {"success": true, "message": "Password changed successfully"}
     */
    private void handleChangePassword(HttpExchange exchange) throws IOException {
        // Extract JWT from Authorization header
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendJsonResponse(exchange, 401, createErrorResponse("Authorization token required"));
            return;
        }
        
        String token = authHeader.substring(7);
        
        // Validate token
        if (!JwtUtil.validateToken(token)) {
            sendJsonResponse(exchange, 401, createErrorResponse("Invalid or expired token"));
            return;
        }
        
        Map<String, String> requestBody = parseJsonRequest(exchange);
        
        String currentPassword = requestBody.get("currentPassword");
        String newPassword = requestBody.get("newPassword");
        
        if (currentPassword == null || newPassword == null) {
            sendJsonResponse(exchange, 400, createErrorResponse("Current and new passwords are required"));
            return;
        }
        
        try {
            Integer userId = JwtUtil.getUserId(token);
            authService.changePassword(userId, currentPassword, newPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password changed successfully");
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (AuthenticationService.AuthenticationException e) {
            sendJsonResponse(exchange, 400, createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Parses JSON request body into a Map
     */
    private Map<String, String> parseJsonRequest(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        
        return parseSimpleJson(body.toString());
    }
    
    /**
     * Simple JSON parser for key-value pairs
     */
    private Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                map.put(key, value);
            }
        }
        
        return map;
    }
    
    /**
     * Sends JSON response
     */
    private void sendJsonResponse(HttpExchange exchange, int statusCode, Map<String, Object> data) 
            throws IOException {
        
        String json = mapToJson(data);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.getBytes(StandardCharsets.UTF_8).length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(json.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
    
    /**
     * Converts Map to JSON string
     */
    private String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        
        int count = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (count > 0) json.append(",");
            
            json.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Boolean || value instanceof Number) {
                json.append(value);
            } else {
                json.append("\"").append(value.toString()).append("\"");
            }
            
            count++;
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Creates error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
