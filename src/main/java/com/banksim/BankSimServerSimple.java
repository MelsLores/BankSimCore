package com.banksim;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Simple HTTP server for BankSim Enterprise.
 * Serves static files and provides basic API endpoints.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 5.0 Simple Edition
 * @since 2024
 */
public class BankSimServerSimple {
    
    private static final int PORT = 8080;
    
    public static void main(String[] args) {
        try {
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║   BankSim Enterprise v5.0 (Simple)       ║");
            System.out.println("║   Banking System - Static Server         ║");
            System.out.println("║   Author: Jorge Pena - REM Consultancy   ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println();
            
            // Create HTTP server
            System.out.println("[1/2] Creating HTTP server...");
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            System.out.println("✓ HTTP server created on port " + PORT);
            System.out.println();
            
            // Register static file handler
            System.out.println("[2/2] Registering handlers...");
            server.createContext("/", new StaticFileHandler());
            server.createContext("/api/status", new StatusHandler());
            System.out.println("  → / (Static files)");
            System.out.println("  → /api/status (Server status)");
            System.out.println("✓ All handlers registered");
            System.out.println();
            
            // Start server
            server.setExecutor(null); // creates a default executor
            server.start();
            
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║  Server is running on http://localhost:" + PORT + "  ║");
            System.out.println("║  Press Ctrl+C to stop                    ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Available pages:");
            System.out.println("  Login:    http://localhost:8080/login.html");
            System.out.println("  Register: http://localhost:8080/register.html");
            System.out.println("  Status:   http://localhost:8080/api/status");
            System.out.println();
            System.out.println("Note: Full API requires database setup.");
            System.out.println("      Run: .\\scripts\\setup_database.ps1");
            System.out.println();
            
            // Shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("Shutting down server...");
                server.stop(0);
                System.out.println("✓ Server stopped");
            }));
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Handler for static files (HTML, CSS, JS)
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Default to index.html
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            // Serve static files
            String filePath = "src/main/resources/static" + path;
            
            try {
                byte[] response = Files.readAllBytes(Paths.get(filePath));
                
                // Set content type
                String contentType = "text/html";
                if (path.endsWith(".css")) contentType = "text/css";
                if (path.endsWith(".js")) contentType = "application/javascript";
                if (path.endsWith(".json")) contentType = "application/json";
                
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, response.length);
                
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
                
            } catch (IOException e) {
                // File not found
                String notFound = "<h1>404 Not Found</h1><p>File not found: " + path + "</p>";
                byte[] response = notFound.getBytes(StandardCharsets.UTF_8);
                
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(404, response.length);
                
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }
        }
    }
    
    /**
     * Handler for server status API
     */
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\":\"running\",\"version\":\"5.0\",\"message\":\"BankSim Enterprise Server is running. Database setup required for full API.\"}";
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }
}
