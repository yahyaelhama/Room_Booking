package com.roombooking.dao;

import com.roombooking.model.Participant;
import com.roombooking.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Participant-related database operations
 */
public class ParticipantDAO {
    
    public ParticipantDAO() {
    }
    
    /**
     * Creates a new participant
     * @param reservationId the reservation ID
     * @param name the participant name
     * @param email the participant email
     * @return true if creation successful, false otherwise
     */
    public boolean createParticipant(int reservationId, String name, String email) {
        String sql = "INSERT INTO participants (reservation_id, name, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates an existing participant
     * @param participant the participant to update
     * @return true if update successful, false otherwise
     */
    public boolean updateParticipant(Participant participant) {
        String sql = "UPDATE participants SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, participant.getName());
            stmt.setString(2, participant.getEmail());
            stmt.setInt(3, participant.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a participant
     * @param id the participant ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteParticipant(int id) {
        String sql = "DELETE FROM participants WHERE id = ?";
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
     * Gets all participants for a reservation
     * @param reservationId the reservation ID
     * @return List of participants
     */
    public List<Participant> getReservationParticipants(int reservationId) {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT * FROM participants WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participants.add(new Participant(
                        rs.getInt("id"),
                        rs.getInt("reservation_id"),
                        rs.getString("name"),
                        rs.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }
    
    /**
     * Gets a participant by ID
     * @param id the participant ID
     * @return Participant object if found, null otherwise
     */
    public Participant getParticipant(int id) {
        String sql = "SELECT * FROM participants WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Participant(
                        rs.getInt("id"),
                        rs.getInt("reservation_id"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets a participant by email
     * @param email the participant email
     * @return Participant object if found, null otherwise
     */
    public Participant getParticipantByEmail(String email) {
        String sql = "SELECT * FROM participants WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Participant(
                        rs.getInt("id"),
                        rs.getInt("reservation_id"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes all participants for a reservation
     * @param reservationId the reservation ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteReservationParticipants(int reservationId) {
        String sql = "DELETE FROM participants WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 