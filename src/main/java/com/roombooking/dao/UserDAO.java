package com.roombooking.dao;

import com.roombooking.model.User;
import com.roombooking.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User-related database operations
 */
public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public User getUserByUsername(String username) {
        String sql = "SELECT u.id, u.username, CONCAT(p.first_name, ' ', p.last_name) as full_name, " +
                    "u.email, u.password_hash as password, u.role = 'ADMIN' as is_admin, " +
                    "u.is_active, u.profile_id " +
                    "FROM users u " +
                    "LEFT JOIN profiles p ON u.profile_id = p.id " +
                    "WHERE u.username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return null;
    }

    public User getUser(int id) {
        String sql = "SELECT u.id, u.username, CONCAT(p.first_name, ' ', p.last_name) as full_name, " +
                    "u.email, u.password_hash as password, u.role = 'ADMIN' as is_admin, " +
                    "u.is_active, u.profile_id " +
                    "FROM users u " +
                    "LEFT JOIN profiles p ON u.profile_id = p.id " +
                    "WHERE u.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return null;
    }

    public List<User> getAllUsers() {
        String sql = "SELECT u.id, u.username, CONCAT(p.first_name, ' ', p.last_name) as full_name, " +
                    "u.email, u.password_hash as password, u.role = 'ADMIN' as is_admin, " +
                    "u.is_active, u.profile_id " +
                    "FROM users u " +
                    "LEFT JOIN profiles p ON u.profile_id = p.id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all users: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return users;
    }

    public boolean createUser(User user) {
        // First create a profile
        String profileSql = "INSERT INTO profiles (first_name, last_name, email) VALUES (?, ?, ?)";
        String userSql = "INSERT INTO users (username, email, password_hash, role, is_active, profile_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert profile first
                int profileId;
                try (PreparedStatement stmt = conn.prepareStatement(profileSql, Statement.RETURN_GENERATED_KEYS)) {
                    String[] names = user.getFullName().split(" ", 2);
                    stmt.setString(1, names[0]);
                    stmt.setString(2, names.length > 1 ? names[1] : "");
                    stmt.setString(3, user.getEmail());
                    
                    if (stmt.executeUpdate() == 0) {
                        throw new SQLException("Creating profile failed, no rows affected.");
                    }
                    
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            profileId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Creating profile failed, no ID obtained.");
                        }
                    }
                }
                
                // Then insert user
                try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
                    stmt.setString(1, user.getUsername());
                    stmt.setString(2, user.getEmail());
                    stmt.setString(3, user.getPassword());
                    stmt.setString(4, user.isAdmin() ? "ADMIN" : "USER");
                    stmt.setBoolean(5, user.isActive());
                    stmt.setInt(6, profileId);
                    
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating user failed, no rows affected.");
                    }
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Error creating user: {}", e.getMessage());
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, role = ?, " +
                    "is_active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.isAdmin() ? "ADMIN" : "USER");
            stmt.setBoolean(4, user.isActive());
            stmt.setInt(5, user.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user: {}", e.getMessage());
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setAdmin(rs.getBoolean("is_admin"));
        user.setActive(rs.getBoolean("is_active"));
        user.setProfileId(rs.getInt("profile_id"));
        return user;
    }
} 