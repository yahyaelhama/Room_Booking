package com.roombooking.dao;

import com.roombooking.model.Room;
import com.roombooking.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Room-related database operations
 */
public class RoomDAO {
    private static final Logger logger = LoggerFactory.getLogger(RoomDAO.class);
    
    public RoomDAO() {
    }
    
    /**
     * Creates a new room
     * @param name the name of the room
     * @param capacity the capacity of the room
     * @param type the room type
     * @param location the room location
     * @param description the room description
     * @return true if creation successful, false otherwise
     */
    public boolean createRoom(String name, int capacity, String type, String location, String description) {
        String sql = "INSERT INTO rooms (name, capacity, type, location, description, is_active) VALUES (?, ?, ?, ?, ?, true)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, capacity);
            stmt.setString(3, type);
            stmt.setString(4, location);
            stmt.setString(5, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error creating room: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Updates an existing room
     * @param room the room to update
     * @return true if update successful, false otherwise
     */
    public boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET name = ?, capacity = ?, type = ?, location = ?, description = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getName());
            stmt.setInt(2, room.getCapacity());
            stmt.setString(3, room.getType());
            stmt.setString(4, room.getLocation());
            stmt.setString(5, room.getDescription());
            stmt.setBoolean(6, room.isActive());
            stmt.setInt(7, room.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating room: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Deletes a room
     * @param id the ID of the room to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
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
     * Gets all rooms
     * @return List of all rooms
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            logger.info("Executing getAllRooms query: {}", sql);
            int count = 0;
            while (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                rooms.add(room);
                count++;
                logger.debug("Loaded room: ID={}, Name={}, Active={}", room.getId(), room.getName(), room.isActive());
            }
            logger.info("Found {} rooms in total", count);
        } catch (SQLException e) {
            logger.error("Error getting all rooms: {}", e.getMessage(), e);
        }
        return rooms;
    }
    
    /**
     * Gets a room by ID
     * @param id the room ID
     * @return Room object if found, null otherwise
     */
    public Room getRoom(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all available rooms
     * @return List of available rooms
     */
    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_active = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            logger.info("Executing getAvailableRooms query: {}", sql);
            int count = 0;
            while (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                rooms.add(room);
                count++;
                logger.debug("Loaded available room: ID={}, Name={}", room.getId(), room.getName());
            }
            logger.info("Found {} available rooms", count);
        } catch (SQLException e) {
            logger.error("Error getting available rooms: {}", e.getMessage(), e);
        }
        return rooms;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("capacity"),
            rs.getString("type"),
            rs.getString("location"),
            rs.getString("description"),
            rs.getBoolean("is_active")
        );
        logger.debug("Mapped room from ResultSet: {}", room);
        return room;
    }
} 