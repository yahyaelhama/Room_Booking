package com.roombooking.dao;

import com.roombooking.model.Profile;
import com.roombooking.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Profile-related database operations
 */
public class ProfileDAO {
    
    public ProfileDAO() {
    }
    
    /**
     * Creates a new profile
     * @param name the profile name
     * @param description the profile description
     * @return true if creation successful, false otherwise
     */
    public boolean createProfile(String name, String description) {
        String sql = "INSERT INTO profiles (name, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates an existing profile
     * @param profile the profile to update
     * @return true if update successful, false otherwise
     */
    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profiles SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getDescription());
            stmt.setInt(3, profile.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a profile
     * @param id the profile ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteProfile(int id) {
        String sql = "DELETE FROM profiles WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets all profiles
     * @return List of all profiles
     */
    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                profiles.add(new Profile(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
    
    /**
     * Gets a profile by ID
     * @param id the profile ID
     * @return Profile object if found, null otherwise
     */
    public Profile getProfile(int id) {
        String sql = "SELECT * FROM profiles WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Profile(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets a profile by name
     * @param name the profile name
     * @return Profile object if found, null otherwise
     */
    public Profile getProfileByName(String name) {
        String sql = "SELECT * FROM profiles WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Profile(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
} 