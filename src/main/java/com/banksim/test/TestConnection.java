package com.banksim.test;

import com.banksim.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== Prueba de Conexión a PostgreSQL ===\n");
        
        try {
            DatabaseConfig config = DatabaseConfig.getInstance();
            System.out.println("✓ DatabaseConfig inicializado");
            
            Connection conn = config.getConnection();
            System.out.println("✓ Conexión establecida");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM users");
            
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("✓ Query ejecutado: " + total + " usuarios en la base de datos");
            }
            
            rs = stmt.executeQuery("SELECT username, email, role FROM users");
            System.out.println("\nUsuarios registrados:");
            System.out.println("----------------------------------------");
            while (rs.next()) {
                System.out.printf("- %s (%s) - %s%n", 
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("role"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("\n✓ Conexión cerrada correctamente");
            System.out.println("\n=== ¡Base de datos funcionando! ===");
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
