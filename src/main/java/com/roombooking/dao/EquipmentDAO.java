package com.roombooking.dao;

import com.roombooking.model.Equipment;
import com.roombooking.model.Reservation;
import com.roombooking.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Equipment-related database operations
 */
public class EquipmentDAO {
    
    public EquipmentDAO() {
    }
    
    /**
     * Creates new equipment
     * @param name the equipment name
     * @param type the equipment type
     * @param description the equipment description
     * @return true if creation successful, false otherwise
     */
    public boolean createEquipment(String name, String type, String description) {
        String sql = "INSERT INTO equipment (name, type, description, is_available) VALUES (?, ?, ?, true)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setString(3, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates existing equipment
     * @param equipment the equipment to update
     * @return true if update successful, false otherwise
     */
    public boolean updateEquipment(Equipment equipment) {
        String sql = "UPDATE equipment SET name = ?, type = ?, description = ?, is_available = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, equipment.getName());
            stmt.setString(2, equipment.getType());
            stmt.setString(3, equipment.getDescription());
            stmt.setBoolean(4, equipment.isAvailable());
            stmt.setInt(5, equipment.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes equipment
     * @param id the equipment ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteEquipment(int id) {
        String sql = "DELETE FROM equipment WHERE id = ?";
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
     * Gets all equipment
     * @return List of all equipment
     */
    public List<Equipment> getAllEquipment() {
        List<Equipment> equipment = new ArrayList<>();
        String sql = "SELECT * FROM equipment";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                equipment.add(new Equipment(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("description"),
                    rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipment;
    }
    
    /**
     * Gets equipment by ID
     * @param id the equipment ID
     * @return Equipment object if found, null otherwise
     */
    public Equipment getEquipment(int id) {
        String sql = "SELECT * FROM equipment WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Equipment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getBoolean("is_available")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all available equipment
     * @return List of available equipment
     */
    public List<Equipment> getAvailableEquipment() {
        List<Equipment> equipment = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE is_available = true";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                equipment.add(new Equipment(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("description"),
                    true
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipment;
    }
    
    /**
     * Gets equipment for a reservation
     * @param reservationId the reservation ID
     * @return List of equipment assigned to the reservation
     */
    public List<Equipment> getReservationEquipment(int reservationId) {
        List<Equipment> equipment = new ArrayList<>();
        String sql = "SELECT e.* FROM equipment e " +
                    "JOIN reservation_equipment re ON e.id = re.equipment_id " +
                    "WHERE re.reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipment.add(new Equipment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getBoolean("is_available")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipment;
    }
    
    /**
     * Assigns equipment to a reservation
     * @param reservationId the reservation ID
     * @param equipmentId the equipment ID
     * @return true if assignment successful, false otherwise
     */
    public boolean assignEquipment(int reservationId, int equipmentId) {
        String sql = "INSERT INTO reservation_equipment (reservation_id, equipment_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            stmt.setInt(2, equipmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Removes equipment from a reservation
     * @param reservationId the reservation ID
     * @param equipmentId the equipment ID
     * @return true if removal successful, false otherwise
     */
    public boolean removeEquipment(int reservationId, int equipmentId) {
        String sql = "DELETE FROM reservation_equipment WHERE reservation_id = ? AND equipment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            stmt.setInt(2, equipmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Reservation> getEquipmentReservations(int equipmentId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.* FROM reservations r " +
                    "JOIN reservation_equipment re ON r.id = re.reservation_id " +
                    "WHERE re.equipment_id = ? AND r.status != 'CANCELLED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, equipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(rs.getInt("id"));
                    reservation.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    reservation.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
} 