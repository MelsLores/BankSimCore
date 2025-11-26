package com.banksim.controller;

import com.banksim.model.Customer;
import com.banksim.service.CustomerService;
import com.banksim.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for customer operations.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class CustomerController implements HttpHandler {
    
    private final CustomerService customerService;
    
    public CustomerController() {
        this.customerService = new CustomerService();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        
        // Validate JWT
        String token = extractToken(exchange);
        if (token == null || !JwtUtil.validateToken(token)) {
            sendJsonResponse(exchange, 401, createErrorResponse("Unauthorized"));
            return;
        }
        
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        
        try {
            if (path.equals("/api/customers") && "POST".equals(method)) {
                handleCreateCustomer(exchange);
            } else if (path.equals("/api/customers") && "GET".equals(method)) {
                handleGetAllCustomers(exchange);
            } else if (path.matches("/api/customers/\\d+") && "GET".equals(method)) {
                handleGetCustomer(exchange, path);
            } else if (path.matches("/api/customers/\\d+") && "PUT".equals(method)) {
                handleUpdateCustomer(exchange, path);
            } else if (path.matches("/api/customers/\\d+/verify-key") && "POST".equals(method)) {
                handleVerifyPersonalKey(exchange, path);
            } else if (path.matches("/api/customers/user/\\d+") && "GET".equals(method)) {
                handleGetCustomerByUserId(exchange, path);
            } else {
                sendJsonResponse(exchange, 404, createErrorResponse("Endpoint not found"));
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/customers
     * Request: {"userId": 1, "firstName": "John", "lastName": "Doe", "email": "john@email.com", 
     *           "phone": "1234567890", "address": "123 Main St", "dateOfBirth": "1990-01-01", 
     *           "personalKey": "Abcd1234"}
     */
    private void handleCreateCustomer(HttpExchange exchange) throws IOException {
        Map<String, String> requestBody = parseJsonRequest(exchange);
        
        try {
            Long userId = Long.parseLong(requestBody.get("userId"));
            String firstName = requestBody.get("firstName");
            String lastName = requestBody.get("lastName");
            String email = requestBody.get("email");
            String phone = requestBody.get("phone");
            String address = requestBody.get("address");
            LocalDate dateOfBirth = LocalDate.parse(requestBody.get("dateOfBirth"));
            String personalKey = requestBody.get("personalKey");
            
            Customer customer = customerService.createCustomer(
                userId, firstName, lastName, email, phone, address, dateOfBirth, personalKey);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customer", customerToMap(customer));
            response.put("message", "Customer created successfully");
            
            sendJsonResponse(exchange, 201, response);
            
        } catch (CustomerService.CustomerServiceException e) {
            sendJsonResponse(exchange, 400, createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid input: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/customers
     */
    private void handleGetAllCustomers(HttpExchange exchange) throws IOException {
        List<Customer> customers = customerService.getAllCustomers();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("customers", customers.stream().map(this::customerToMap).toArray());
        response.put("count", customers.size());
        
        sendJsonResponse(exchange, 200, response);
    }
    
    /**
     * GET /api/customers/{id}
     */
    private void handleGetCustomer(HttpExchange exchange, String path) throws IOException {
        try {
            Long customerId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            
            Customer customer = customerService.getCustomerById(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customer", customerToMap(customer));
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (CustomerService.CustomerServiceException e) {
            sendJsonResponse(exchange, 404, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid customer ID"));
        }
    }
    
    /**
     * GET /api/customers/user/{userId}
     */
    private void handleGetCustomerByUserId(HttpExchange exchange, String path) throws IOException {
        try {
            Long userId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            
            Customer customer = customerService.getCustomerByUserId(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customer", customerToMap(customer));
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (CustomerService.CustomerServiceException e) {
            sendJsonResponse(exchange, 404, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid user ID"));
        }
    }
    
    /**
     * PUT /api/customers/{id}
     * Request: {"firstName": "John", "lastName": "Doe", "email": "john@email.com", 
     *           "phone": "1234567890", "address": "123 Main St"}
     */
    private void handleUpdateCustomer(HttpExchange exchange, String path) throws IOException {
        try {
            Long customerId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            
            Map<String, String> requestBody = parseJsonRequest(exchange);
            
            String firstName = requestBody.get("firstName");
            String lastName = requestBody.get("lastName");
            String email = requestBody.get("email");
            String phone = requestBody.get("phone");
            String address = requestBody.get("address");
            
            Customer customer = customerService.updateCustomer(
                customerId, firstName, lastName, email, phone, address);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customer", customerToMap(customer));
            response.put("message", "Customer updated successfully");
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (CustomerService.CustomerServiceException e) {
            sendJsonResponse(exchange, 400, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid customer ID"));
        }
    }
    
    /**
     * POST /api/customers/{id}/verify-key
     * Request: {"personalKey": "Abcd1234"}
     * Response: {"success": true, "verified": true}
     */
    private void handleVerifyPersonalKey(HttpExchange exchange, String path) throws IOException {
        try {
            String customerId = path.split("/")[3];
            Long id = Long.parseLong(customerId);
            
            Map<String, String> requestBody = parseJsonRequest(exchange);
            String personalKey = requestBody.get("personalKey");
            
            boolean verified = customerService.verifyPersonalKey(id, personalKey);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("verified", verified);
            response.put("message", verified ? "Personal key verified" : "Personal key does not match");
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (CustomerService.CustomerServiceException e) {
            sendJsonResponse(exchange, 404, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid customer ID"));
        }
    }
    
    /**
     * Converts Customer to Map
     */
    private Map<String, Object> customerToMap(Customer customer) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", customer.getId());
        map.put("userId", customer.getUserId());
        map.put("customerCode", customer.getCustomerCode());
        map.put("firstName", customer.getFirstName());
        map.put("lastName", customer.getLastName());
        map.put("email", customer.getEmail());
        map.put("phone", customer.getPhone());
        map.put("address", customer.getAddress());
        map.put("dateOfBirth", customer.getDateOfBirth().toString());
        map.put("verified", customer.isVerified());
        map.put("createdAt", customer.getCreatedAt().toString());
        return map;
    }
    
    private String extractToken(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
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
    
    private void sendJsonResponse(HttpExchange exchange, int statusCode, Map<String, Object> data) 
            throws IOException {
        
        String json = mapToJson(data);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.getBytes(StandardCharsets.UTF_8).length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(json.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
    
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
            } else if (value.getClass().isArray()) {
                json.append("[");
                Object[] arr = (Object[]) value;
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) json.append(",");
                    if (arr[i] instanceof Map) {
                        json.append(mapToJson((Map<String, Object>) arr[i]));
                    } else {
                        json.append("\"").append(arr[i]).append("\"");
                    }
                }
                json.append("]");
            } else {
                json.append("\"").append(value.toString()).append("\"");
            }
            
            count++;
        }
        
        json.append("}");
        return json.toString();
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
