package com.roombooking.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final Properties properties = new Properties();
    private static String url;
    private static String username;
    private static String password;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;

    static {
        try {
            // Load database properties
            try (InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database.properties")) {
                if (input == null) {
                    throw new RuntimeException("Unable to find database.properties");
                }
                properties.load(input);
            }

            // Set connection properties
            String host = properties.getProperty("db.host", "localhost");
            String port = properties.getProperty("db.port", "3306");
            String database = properties.getProperty("db.name", "room_booking");
            
            // Build connection URL with additional properties
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(String.format("jdbc:mysql://%s:%s/%s?", host, port, database));
            urlBuilder.append("useSSL=").append(properties.getProperty("db.useSSL", "false"));
            urlBuilder.append("&serverTimezone=").append(properties.getProperty("db.timezone", "UTC"));
            urlBuilder.append("&allowPublicKeyRetrieval=").append(properties.getProperty("db.allowPublicKeyRetrieval", "true"));
            urlBuilder.append("&autoReconnect=").append(properties.getProperty("db.autoReconnect", "true"));
            urlBuilder.append("&connectTimeout=").append(properties.getProperty("db.connectionTimeout", "30000"));
            
            url = urlBuilder.toString();
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");

            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            logger.info("Database configuration loaded successfully");
            
            // Test connection on startup
            testConnection();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error initializing database connection: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    /**
     * Gets a connection to the database with retry mechanism
     * @return Connection object
     * @throws SQLException if connection fails after retries
     */
    public static Connection getConnection() throws SQLException {
        SQLException lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                Connection conn = DriverManager.getConnection(url, username, password);
                if (conn.isValid(1)) {
                    logger.debug("Database connection established");
                    return conn;
                }
            } catch (SQLException e) {
                lastException = e;
                logger.warn("Connection attempt {} failed: {}", attempt, e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("Connection interrupted", ie);
                    }
                }
            }
        }
        
        logger.error("Failed to establish database connection after {} attempts", MAX_RETRIES);
        throw new SQLException("Failed to establish database connection after " + MAX_RETRIES + " attempts", lastException);
    }

    /**
     * Tests the database connection
     * @return true if connection successful
     * @throws RuntimeException if connection fails
     */
    private static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(1);
        } catch (SQLException e) {
            logger.error("Database connection test failed: {}", e.getMessage());
            throw new RuntimeException("Failed to verify database connection", e);
        }
    }

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }
} 