package com.banksim.controller;

import com.banksim.model.Account;
import com.banksim.service.AccountService;
import com.banksim.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for account operations.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class AccountController implements HttpHandler {
    
    private final AccountService accountService;
    
    public AccountController() {
        this.accountService = new AccountService();
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
            if (path.equals("/api/accounts") && "POST".equals(method)) {
                handleCreateAccount(exchange, token);
            } else if (path.matches("/api/accounts/\\d+") && "GET".equals(method)) {
                handleGetAccount(exchange, path);
            } else if (path.matches("/api/accounts/\\d+/balance") && "GET".equals(method)) {
                handleGetBalance(exchange, path);
            } else if (path.matches("/api/accounts/customer/\\d+") && "GET".equals(method)) {
                handleGetCustomerAccounts(exchange, path);
            } else {
                sendJsonResponse(exchange, 404, createErrorResponse("Endpoint not found"));
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/accounts
     * Request: {"customerId": 1, "accountType": "SAVINGS", "initialDeposit": "1000.00"}
     */
    private void handleCreateAccount(HttpExchange exchange, String token) throws IOException {
        Map<String, String> requestBody = parseJsonRequest(exchange);
        
        try {
            Long customerId = Long.parseLong(requestBody.get("customerId"));
            String accountType = requestBody.get("accountType");
            BigDecimal initialDeposit = new BigDecimal(requestBody.getOrDefault("initialDeposit", "0"));
            
            Account account = accountService.createAccount(customerId, accountType, initialDeposit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("account", accountToMap(account));
            response.put("message", "Account created successfully");
            
            sendJsonResponse(exchange, 201, response);
            
        } catch (AccountService.AccountServiceException e) {
            sendJsonResponse(exchange, 400, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid number format"));
        }
    }
    
    /**
     * GET /api/accounts/{id}
     */
    private void handleGetAccount(HttpExchange exchange, String path) throws IOException {
        try {
            Long accountId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            
            Account account = accountService.getAccountById(accountId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("account", accountToMap(account));
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (AccountService.AccountServiceException e) {
            sendJsonResponse(exchange, 404, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid account ID"));
        }
    }
    
    /**
     * GET /api/accounts/{id}/balance
     */
    private void handleGetBalance(HttpExchange exchange, String path) throws IOException {
        try {
            String accountIdStr = path.split("/")[3];
            Long accountId = Long.parseLong(accountIdStr);
            
            BigDecimal balance = accountService.getBalance(accountId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accountId", accountId);
            response.put("balance", balance.toString());
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (AccountService.AccountServiceException e) {
            sendJsonResponse(exchange, 404, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid account ID"));
        }
    }
    
    /**
     * GET /api/accounts/customer/{customerId}
     */
    private void handleGetCustomerAccounts(HttpExchange exchange, String path) throws IOException {
        try {
            Long customerId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accounts", accounts.stream().map(this::accountToMap).toArray());
            response.put("count", accounts.size());
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid customer ID"));
        }
    }
    
    /**
     * Converts Account to Map
     */
    private Map<String, Object> accountToMap(Account account) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", account.getId());
        map.put("accountNumber", account.getAccountNumber());
        map.put("customerId", account.getCustomerId());
        map.put("accountType", account.getAccountType());
        map.put("balance", account.getBalance().toString());
        map.put("status", account.getStatus());
        map.put("createdAt", account.getCreatedAt().toString());
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
