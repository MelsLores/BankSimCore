package com.banksim.controller;

import com.banksim.service.ValidationTestService;
import com.banksim.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for test case validation.
 * Runs the 12 test cases from PDF specification.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 1.0
 * @since 2024
 */
public class TestController implements HttpHandler {
    
    private final ValidationTestService validationTestService;
    
    public TestController() {
        this.validationTestService = new ValidationTestService();
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
        
        // Validate JWT (optional for test endpoint)
        String token = extractToken(exchange);
        if (token == null || !JwtUtil.validateToken(token)) {
            sendJsonResponse(exchange, 401, createErrorResponse("Unauthorized"));
            return;
        }
        
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        
        try {
            if (path.equals("/api/tests/run-all") && "GET".equals(method)) {
                handleRunAllTests(exchange);
            } else {
                sendJsonResponse(exchange, 404, createErrorResponse("Endpoint not found"));
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/tests/run-all
     * Runs all 12 test cases
     */
    private void handleRunAllTests(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> results = validationTestService.runAllTests();
        
        // Calculate summary
        long passCount = results.stream().filter(r -> (Boolean) r.get("passed")).count();
        long failCount = results.size() - passCount;
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalTests", results.size());
        response.put("passed", passCount);
        response.put("failed", failCount);
        response.put("results", results.toArray());
        
        sendJsonResponse(exchange, 200, response);
    }
    
    private String extractToken(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
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
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            } else if (value instanceof Boolean || value instanceof Number) {
                json.append(value);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                json.append("[");
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) json.append(",");
                    if (list.get(i) instanceof Map) {
                        json.append(mapToJson((Map<String, Object>) list.get(i)));
                    } else {
                        json.append("\"").append(list.get(i)).append("\"");
                    }
                }
                json.append("]");
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
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            }
            
            count++;
        }
        
        json.append("}");
        return json.toString();
    }
    
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
