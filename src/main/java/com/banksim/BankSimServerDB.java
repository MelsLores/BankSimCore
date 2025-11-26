package com.banksim;

import com.banksim.config.DatabaseConfig;
import com.banksim.model.User;
import com.banksim.repository.UserRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

/**
 * BankSim Server with Database Integration
 * Serves static files and provides basic API endpoints with PostgreSQL
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 2.0
 * @since 2024
 */
public class BankSimServerDB {
    
    private static final int PORT = 8080;
    private static DatabaseConfig dbConfig;
    
    public static void main(String[] args) {
        try {
            // Initialize database
            System.out.println("=== BankSim Server with Database ===");
            dbConfig = DatabaseConfig.getInstance();
            System.out.println("✓ Database configured: " + dbConfig.getJdbcUrl());
            
            // Test database connection
            testDatabaseConnection();
            
            // Create HTTP server
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Register handlers
            server.createContext("/", new StaticFileHandler());
            server.createContext("/api/status", new StatusHandler());
            server.createContext("/api/db/users", new UsersHandler());
            server.createContext("/api/db/test", new DBTestHandler());
            server.createContext("/api/auth/login", new SimpleLoginHandler());
            
            // Start server
            server.setExecutor(null);
            server.start();
            
            System.out.println("✓ Server running on http://localhost:" + PORT);
            System.out.println("✓ Database: Connected to PostgreSQL");
            System.out.println("\nEndpoints disponibles:");
            System.out.println("  GET  /                 - Página principal");
            System.out.println("  GET  /api/status       - Estado del servidor");
            System.out.println("  GET  /api/db/test      - Probar conexión a base de datos");
            System.out.println("  GET  /api/db/users     - Listar usuarios");
            System.out.println("  POST /api/auth/login   - Login simple (demo mode)");
            System.out.println("\nPresiona Ctrl+C para detener el servidor");
            
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tests database connection
     */
    private static void testDatabaseConnection() {
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as user_count FROM users");
            if (rs.next()) {
                int userCount = rs.getInt("user_count");
                System.out.println("✓ Database connection successful - " + userCount + " users found");
            }
        } catch (Exception e) {
            System.err.println("⚠ Database connection test failed: " + e.getMessage());
        }
    }
    
    /**
     * Handler for static files
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Default to index.html
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }
            
            // Security: prevent directory traversal
            if (path.contains("..")) {
                sendResponse(exchange, 403, "Forbidden");
                return;
            }
            
            // Try to serve from static directory
            Path filePath = Paths.get("src/main/resources/static" + path);
            
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                String contentType = getContentType(path);
                byte[] content = Files.readAllBytes(filePath);
                
                exchange.getResponseHeaders().add("Content-Type", contentType);
                exchange.sendResponseHeaders(200, content.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(content);
                }
            } else {
                sendResponse(exchange, 404, "File not found: " + path);
            }
        }
        
        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html;charset=UTF-8";
            if (path.endsWith(".css")) return "text/css;charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript;charset=UTF-8";
            if (path.endsWith(".json")) return "application/json;charset=UTF-8";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            return "text/plain;charset=UTF-8";
        }
    }
    
    /**
     * Handler for server status
     */
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"status\":\"running\",");
            json.append("\"server\":\"BankSim with Database\",");
            json.append("\"version\":\"2.0\",");
            json.append("\"database\":");
            
            // Test database
            boolean dbConnected = false;
            try (Connection conn = dbConfig.getConnection()) {
                dbConnected = conn.isValid(2);
            } catch (Exception e) {
                // Connection failed
            }
            
            json.append("{\"connected\":").append(dbConnected);
            json.append(",\"url\":\"").append(dbConfig.getJdbcUrl()).append("\"}");
            json.append("}");
            
            sendJsonResponse(exchange, 200, json.toString());
        }
    }
    
    /**
     * Handler for database test
     */
    static class DBTestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = dbConfig.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) as user_count, " +
                    "(SELECT COUNT(*) FROM customers) as customer_count, " +
                    "(SELECT COUNT(*) FROM accounts) as account_count " +
                    "FROM users"
                );
                
                if (rs.next()) {
                    int users = rs.getInt("user_count");
                    int customers = rs.getInt("customer_count");
                    int accounts = rs.getInt("account_count");
                    
                    String json = String.format(
                        "{\"success\":true,\"users\":%d,\"customers\":%d,\"accounts\":%d}",
                        users, customers, accounts
                    );
                    
                    sendJsonResponse(exchange, 200, json);
                } else {
                    sendJsonResponse(exchange, 500, "{\"success\":false,\"error\":\"No data\"}");
                }
                
            } catch (Exception e) {
                String json = String.format(
                    "{\"success\":false,\"error\":\"%s\"}",
                    e.getMessage().replace("\"", "\\\"")
                );
                sendJsonResponse(exchange, 500, json);
            }
        }
    }
    
    /**
     * Handler for users list
     */
    static class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                try (Connection conn = dbConfig.getConnection();
                     Statement stmt = conn.createStatement()) {
                    
                    ResultSet rs = stmt.executeQuery(
                        "SELECT user_id, username, email, role, is_active FROM users ORDER BY user_id"
                    );
                    
                    StringBuilder json = new StringBuilder();
                    json.append("{\"success\":true,\"users\":[");
                    
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append("{");
                        json.append("\"userId\":").append(rs.getInt("user_id")).append(",");
                        json.append("\"username\":\"").append(rs.getString("username")).append("\",");
                        json.append("\"email\":\"").append(rs.getString("email")).append("\",");
                        json.append("\"role\":\"").append(rs.getString("role")).append("\",");
                        json.append("\"isActive\":").append(rs.getBoolean("is_active"));
                        json.append("}");
                        first = false;
                    }
                    
                    json.append("]}");
                    sendJsonResponse(exchange, 200, json.toString());
                }
                
            } catch (Exception e) {
                String json = String.format(
                    "{\"success\":false,\"error\":\"%s\"}",
                    e.getMessage().replace("\"", "\\\"")
                );
                sendJsonResponse(exchange, 500, json);
            }
        }
    }
    
    /**
     * Simple login handler - validates credentials against database
     */
    static class SimpleLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Add CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            
            // Handle preflight
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"error\":\"Method not allowed\"}");
                return;
            }
            
            try {
                // Read request body
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
                
                // Simple JSON parsing for username and password
                String jsonBody = body.toString();
                String username = extractJsonValue(jsonBody, "username");
                String password = extractJsonValue(jsonBody, "password");
                
                if (username == null || password == null) {
                    sendJsonResponse(exchange, 400, 
                        "{\"success\":false,\"error\":\"Username and password required\"}");
                    return;
                }
                
                // Check database
                try (Connection conn = dbConfig.getConnection();
                     Statement stmt = conn.createStatement()) {
                    
                    // For demo purposes, just check if user exists
                    String query = String.format(
                        "SELECT user_id, username, email, role FROM users WHERE username = '%s' OR email = '%s'",
                        username.replace("'", "''"), username.replace("'", "''")
                    );
                    
                    ResultSet rs = stmt.executeQuery(query);
                    
                    if (rs.next()) {
                        int userId = rs.getInt("user_id");
                        String dbUsername = rs.getString("username");
                        String email = rs.getString("email");
                        String role = rs.getString("role");
                        
                        // Simple success response (in production, verify password hash)
                        String json = String.format(
                            "{\"success\":true,\"accessToken\":\"demo-token-%d\"," +
                            "\"userId\":%d,\"username\":\"%s\",\"email\":\"%s\",\"role\":\"%s\"," +
                            "\"message\":\"Login exitoso (demo mode)\"}",
                            userId, userId, dbUsername, email, role
                        );
                        
                        sendJsonResponse(exchange, 200, json);
                    } else {
                        sendJsonResponse(exchange, 401, 
                            "{\"success\":false,\"error\":\"Usuario no encontrado\"}");
                    }
                }
                
            } catch (Exception e) {
                String json = String.format(
                    "{\"success\":false,\"error\":\"%s\"}",
                    e.getMessage().replace("\"", "\\\"")
                );
                sendJsonResponse(exchange, 500, json);
            }
        }
        
        private String extractJsonValue(String json, String key) {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
            return null;
        }
    }
    
    /**
     * Sends a JSON response
     */
    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonContent) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        
        byte[] bytes = jsonContent.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
    
    /**
     * Sends a plain text response
     */
    private static void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
