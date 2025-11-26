package com.banksim.controller;

import com.banksim.model.Transaction;
import com.banksim.service.TransactionService;
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
 * REST controller for transaction operations.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class TransactionController implements HttpHandler {
    
    private final TransactionService transactionService;
    
    public TransactionController() {
        this.transactionService = new TransactionService();
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
            if (path.equals("/api/transactions/deposit") && "POST".equals(method)) {
                handleDeposit(exchange);
            } else if (path.equals("/api/transactions/withdraw") && "POST".equals(method)) {
                handleWithdraw(exchange);
            } else if (path.equals("/api/transactions/transfer") && "POST".equals(method)) {
                handleTransfer(exchange);
            } else if (path.matches("/api/transactions/statement/\\d+") && "GET".equals(method)) {
                handleGetStatement(exchange, path);
            } else if (path.matches("/api/transactions/account/\\d+") && "GET".equals(method)) {
                handleGetAccountTransactions(exchange, path);
            } else {
                sendJsonResponse(exchange, 404, createErrorResponse("Endpoint not found"));
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/transactions/deposit
     * Request: {"accountId": 1, "amount": "100.00", "description": "Deposit"}
     */
    private void handleDeposit(HttpExchange exchange) throws IOException {
        Map<String, String> requestBody = parseJsonRequest(exchange);
        
        try {
            Long accountId = Long.parseLong(requestBody.get("accountId"));
            BigDecimal amount = new BigDecimal(requestBody.get("amount"));
            String description = requestBody.getOrDefault("description", "Deposit");
            
            Transaction transaction = transactionService.deposit(accountId, amount, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transaction", transactionToMap(transaction));
            response.put("message", "Deposit successful");
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (TransactionService.TransactionServiceException e) {
            sendJsonResponse(exchange, 400, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid number format"));
        }
    }
    
    /**
     * POST /api/transactions/withdraw
     * Request: {"accountId": 1, "amount": "50.00", "description": "Withdrawal"}
     */
    private void handleWithdraw(HttpExchange exchange) throws IOException {
        Map<String, String> requestBody = parseJsonRequest(exchange);
        
        try {
            Long accountId = Long.parseLong(requestBody.get("accountId"));
            BigDecimal amount = new BigDecimal(requestBody.get("amount"));
            String description = requestBody.getOrDefault("description", "Withdrawal");
            
            Transaction transaction = transactionService.withdraw(accountId, amount, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transaction", transactionToMap(transaction));
            response.put("message", "Withdrawal successful");
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (TransactionService.TransactionServiceException e) {
            sendJsonResponse(exchange, 400, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid number format"));
        }
    }
    
    /**
     * POST /api/transactions/transfer
     * Request: {"fromAccountId": 1, "toAccountId": 2, "amount": "75.00", "description": "Transfer"}
     */
    private void handleTransfer(HttpExchange exchange) throws IOException {
        Map<String, String> requestBody = parseJsonRequest(exchange);
        
        try {
            Long fromAccountId = Long.parseLong(requestBody.get("fromAccountId"));
            Long toAccountId = Long.parseLong(requestBody.get("toAccountId"));
            BigDecimal amount = new BigDecimal(requestBody.get("amount"));
            String description = requestBody.getOrDefault("description", "Transfer");
            
            Map<String, Transaction> transactions = transactionService.transfer(
                fromAccountId, toAccountId, amount, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("withdrawalTransaction", transactionToMap(transactions.get("withdrawal")));
            response.put("depositTransaction", transactionToMap(transactions.get("deposit")));
            response.put("message", "Transfer successful");
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (TransactionService.TransactionServiceException e) {
            sendJsonResponse(exchange, 400, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid number format"));
        }
    }
    
    /**
     * GET /api/transactions/statement/{accountId}?limit=10&offset=0
     */
    private void handleGetStatement(HttpExchange exchange, String path) throws IOException {
        try {
            Long accountId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            
            // Parse query parameters
            String query = exchange.getRequestURI().getQuery();
            int limit = 10;
            int offset = 0;
            
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] kv = param.split("=");
                    if (kv.length == 2) {
                        if ("limit".equals(kv[0])) limit = Integer.parseInt(kv[1]);
                        if ("offset".equals(kv[0])) offset = Integer.parseInt(kv[1]);
                    }
                }
            }
            
            List<Transaction> transactions = transactionService.getStatement(accountId, limit, offset);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accountId", accountId);
            response.put("transactions", transactions.stream().map(this::transactionToMap).toArray());
            response.put("count", transactions.size());
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (TransactionService.TransactionServiceException e) {
            sendJsonResponse(exchange, 404, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid account ID"));
        }
    }
    
    /**
     * GET /api/transactions/account/{accountId}
     */
    private void handleGetAccountTransactions(HttpExchange exchange, String path) throws IOException {
        try {
            Long accountId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
            
            List<Transaction> transactions = transactionService.getAllTransactions(accountId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accountId", accountId);
            response.put("transactions", transactions.stream().map(this::transactionToMap).toArray());
            response.put("count", transactions.size());
            
            sendJsonResponse(exchange, 200, response);
            
        } catch (TransactionService.TransactionServiceException e) {
            sendJsonResponse(exchange, 404, createErrorResponse(e.getMessage()));
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, createErrorResponse("Invalid account ID"));
        }
    }
    
    /**
     * Converts Transaction to Map
     */
    private Map<String, Object> transactionToMap(Transaction transaction) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", transaction.getId());
        map.put("transactionUuid", transaction.getTransactionUuid());
        map.put("accountId", transaction.getAccountId());
        map.put("transactionType", transaction.getTransactionType());
        map.put("amount", transaction.getAmount().toString());
        map.put("balanceAfter", transaction.getBalanceAfter().toString());
        map.put("description", transaction.getDescription());
        map.put("createdAt", transaction.getCreatedAt().toString());
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
            } else if (value instanceof Map) {
                json.append(mapToJson((Map<String, Object>) value));
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
