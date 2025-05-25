package com.roombooking.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    
    @Test
    public void testDatabaseConnection() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            assertNotNull(conn, "Database connection should not be null");
            assertTrue(conn.isValid(1), "Database connection should be valid");
            assertFalse(conn.isClosed(), "Database connection should not be closed");
        } catch (SQLException e) {
            fail("Database connection test failed: " + e.getMessage());
        }
    }
} 