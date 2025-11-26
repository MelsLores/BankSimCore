package com.banksim;

import com.banksim.config.DatabaseConfig;
import com.banksim.controller.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main server application for BankSim.
 * Enterprise banking system with REST API.
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 5.0 Enterprise Edition
 * @since 2024
 */
public class BankSimServer {
    
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;
    
    public static void main(String[] args) {
        try {
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║   BankSim Enterprise v5.0                ║");
            System.out.println("║   Banking System with PostgreSQL         ║");
            System.out.println("║   Author: Jorge Pena - REM Consultancy   ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println();
            
            // Initialize database connection pool
            System.out.println("[1/4] Initializing database connection pool...");
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            System.out.println("✓ Database pool initialized (max: 10, min idle: 2)");
            System.out.println();
            
            // Create HTTP server
            System.out.println("[2/4] Creating HTTP server...");
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.setExecutor(Executors.newFixedThreadPool(THREAD_POOL_SIZE));
            System.out.println("✓ HTTP server created on port " + PORT);
            System.out.println();
            
            // Register controllers
            System.out.println("[3/4] Registering REST API controllers...");
            
            // Authentication endpoints
            server.createContext("/api/auth/", new AuthController());
            System.out.println("  → /api/auth/login (POST)");
            System.out.println("  → /api/auth/register (POST)");
            System.out.println("  → /api/auth/refresh (POST)");
            System.out.println("  → /api/auth/change-password (POST)");
            
            // Customer endpoints
            server.createContext("/api/customers", new CustomerController());
            System.out.println("  → /api/customers (GET, POST)");
            System.out.println("  → /api/customers/{id} (GET, PUT)");
            System.out.println("  → /api/customers/{id}/verify-key (POST)");
            System.out.println("  → /api/customers/user/{userId} (GET)");
            
            // Account endpoints
            server.createContext("/api/accounts", new AccountController());
            System.out.println("  → /api/accounts (POST)");
            System.out.println("  → /api/accounts/{id} (GET)");
            System.out.println("  → /api/accounts/{id}/balance (GET)");
            System.out.println("  → /api/accounts/customer/{customerId} (GET)");
            
            // Transaction endpoints
            server.createContext("/api/transactions", new TransactionController());
            System.out.println("  → /api/transactions/deposit (POST)");
            System.out.println("  → /api/transactions/withdraw (POST)");
            System.out.println("  → /api/transactions/transfer (POST)");
            System.out.println("  → /api/transactions/statement/{accountId} (GET)");
            System.out.println("  → /api/transactions/account/{accountId} (GET)");
            
            // Test endpoints
            server.createContext("/api/tests", new TestController());
            System.out.println("  → /api/tests/run-all (GET)");
            
            System.out.println("✓ All controllers registered");
            System.out.println();
            
            // Start server
            System.out.println("[4/4] Starting server...");
            server.start();
            System.out.println("✓ Server started successfully!");
            System.out.println();
            
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║  Server is running on http://localhost:" + PORT + "  ║");
            System.out.println("║  Press Ctrl+C to stop                    ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println();
            System.out.println("API Documentation:");
            System.out.println("  Authentication: http://localhost:8080/api/auth/");
            System.out.println("  Customers:      http://localhost:8080/api/customers");
            System.out.println("  Accounts:       http://localhost:8080/api/accounts");
            System.out.println("  Transactions:   http://localhost:8080/api/transactions");
            System.out.println("  Tests:          http://localhost:8080/api/tests/run-all");
            System.out.println();
            
            // Shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("Shutting down server...");
                server.stop(0);
                try {
                    dbConfig.close();
                    System.out.println("✓ Database connections closed");
                } catch (Exception e) {
                    System.err.println("Error closing database: " + e.getMessage());
                }
                System.out.println("✓ Server stopped");
            }));
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
