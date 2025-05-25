package com.roombooking.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            
            // Test connection
            try (Connection conn = DatabaseConnection.getConnection()) {
                System.out.println("Connection successful!");
                
                // Check tables
                checkTable(conn, "users");
                checkTable(conn, "rooms");
                checkTable(conn, "reservations");
                
                // Check reservations
                System.out.println("\nChecking reservations...");
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM reservations")) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("Found " + count + " reservations in the database");
                        
                        if (count == 0) {
                            System.out.println("Creating a test reservation...");
                            // Create a test reservation if none exist
                            String insertSql = "INSERT INTO reservations (user_id, room_id, start_time, end_time, status, subject) " +
                                              "VALUES (1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 2 HOUR), 'PENDING', 'Test Reservation')";
                            int rows = stmt.executeUpdate(insertSql);
                            System.out.println("Created " + rows + " test reservation(s)");
                        } else {
                            // Show some reservation details
                            try (ResultSet detailsRs = stmt.executeQuery(
                                    "SELECT r.id, u.username, rm.name as room_name, r.start_time, r.end_time, r.status " +
                                    "FROM reservations r " +
                                    "JOIN users u ON r.user_id = u.id " +
                                    "JOIN rooms rm ON r.room_id = rm.id " +
                                    "LIMIT 5")) {
                                System.out.println("\nSample reservations:");
                                System.out.println("ID | User | Room | Start Time | End Time | Status");
                                System.out.println("-----------------------------------------------------");
                                while (detailsRs.next()) {
                                    System.out.println(
                                        detailsRs.getInt("id") + " | " +
                                        detailsRs.getString("username") + " | " +
                                        detailsRs.getString("room_name") + " | " +
                                        detailsRs.getTimestamp("start_time") + " | " +
                                        detailsRs.getTimestamp("end_time") + " | " +
                                        detailsRs.getString("status")
                                    );
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void checkTable(Connection conn, String tableName) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next()) {
                System.out.println("Table '" + tableName + "' exists with " + rs.getInt(1) + " records");
            }
        }
    }
} 