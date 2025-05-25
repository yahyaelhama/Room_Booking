package com.roombooking.dao;

import com.roombooking.model.Reservation;
import com.roombooking.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Reservation-related database operations
 */
public class ReservationDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReservationDAO.class);

    public List<Reservation> findAll() {
        String sql = "SELECT r.*, u.username, rm.name as room_name " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN rooms rm ON r.room_id = rm.id " +
                    "ORDER BY r.start_time DESC";
        
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all reservations: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return reservations;
    }

    public Reservation findById(int id) {
        String sql = "SELECT r.*, u.username, rm.name as room_name " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN rooms rm ON r.room_id = rm.id " +
                    "WHERE r.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReservation(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding reservation by ID: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return null;
    }

    public List<Reservation> findByUserId(int userId) {
        String sql = "SELECT r.*, u.username, rm.name as room_name " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN rooms rm ON r.room_id = rm.id " +
                    "WHERE r.user_id = ? " +
                    "ORDER BY r.start_time DESC";
        
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding reservations by user ID: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return reservations;
    }

    public List<Reservation> findUpcomingByUserId(int userId) {
        String sql = "SELECT r.*, u.username, rm.name as room_name " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN rooms rm ON r.room_id = rm.id " +
                    "WHERE r.user_id = ? AND r.start_time > NOW() " +
                    "ORDER BY r.start_time ASC";
        
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding upcoming reservations: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return reservations;
    }

    public List<Reservation> findPastByUserId(int userId) {
        String sql = "SELECT r.*, u.username, rm.name as room_name " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN rooms rm ON r.room_id = rm.id " +
                    "WHERE r.user_id = ? AND r.end_time < NOW() " +
                    "ORDER BY r.start_time DESC";
        
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding past reservations: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return reservations;
    }

    public boolean save(Reservation reservation) {
        if (reservation.getId() == 0) {
            return insert(reservation);
        } else {
            return update(reservation);
        }
    }

    private boolean insert(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, room_id, start_time, end_time, status, subject) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reservation.getUserId());
            stmt.setInt(2, reservation.getRoomId());
            stmt.setTimestamp(3, Timestamp.valueOf(reservation.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(reservation.getEndTime()));
            stmt.setString(5, reservation.getStatus());
            stmt.setString(6, reservation.getSubject());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                reservation.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error inserting reservation: {}", e.getMessage());
            return false;
        }
    }

    private boolean update(Reservation reservation) {
        String sql = "UPDATE reservations SET user_id = ?, room_id = ?, start_time = ?, end_time = ?, " +
                    "status = ?, subject = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservation.getUserId());
            stmt.setInt(2, reservation.getRoomId());
            stmt.setTimestamp(3, Timestamp.valueOf(reservation.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(reservation.getEndTime()));
            stmt.setString(5, reservation.getStatus());
            stmt.setString(6, reservation.getSubject());
            stmt.setInt(7, reservation.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating reservation: {}", e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting reservation: {}", e.getMessage());
            return false;
        }
    }

    public boolean isRoomAvailable(int roomId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                    "WHERE room_id = ? AND status != 'CANCELLED' AND " +
                    "((start_time <= ? AND end_time > ?) OR " +
                    "(start_time < ? AND end_time >= ?) OR " +
                    "(start_time >= ? AND end_time <= ?))";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            stmt.setTimestamp(2, Timestamp.valueOf(endTime));
            stmt.setTimestamp(3, Timestamp.valueOf(startTime));
            stmt.setTimestamp(4, Timestamp.valueOf(endTime));
            stmt.setTimestamp(5, Timestamp.valueOf(startTime));
            stmt.setTimestamp(6, Timestamp.valueOf(startTime));
            stmt.setTimestamp(7, Timestamp.valueOf(endTime));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking room availability: {}", e.getMessage());
            return false;
        }
        return false;
    }

    public List<Reservation> getReservationsByStatus(String status) {
        String sql = "SELECT r.*, u.username, rm.name as room_name " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN rooms rm ON r.room_id = rm.id " +
                    "WHERE r.status = ? " +
                    "ORDER BY r.start_time DESC";
        
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding reservations by status: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return reservations;
    }
    
    public List<Reservation> getReservationsInRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT r.*, u.username, rm.name as room_name " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN rooms rm ON r.room_id = rm.id " +
                    "WHERE r.start_time >= ? AND r.end_time <= ? " +
                    "ORDER BY r.start_time ASC";
        
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding reservations in range: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return reservations;
    }
    
    public List<Reservation> getUserReservations(int userId) {
        String sql = "SELECT r.*, u.username, rm.name as room_name " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN rooms rm ON r.room_id = rm.id " +
                    "WHERE r.user_id = ? " +
                    "ORDER BY r.start_time DESC";
        
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user reservations: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return reservations;
    }
    
    public Reservation getReservation(int id) {
        String sql = "SELECT r.*, u.username, rm.name as room_name " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN rooms rm ON r.room_id = rm.id " +
                    "WHERE r.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReservation(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding reservation by id: {}", e.getMessage());
            throw new RuntimeException("Database error occurred", e);
        }
        return null;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setUserId(rs.getInt("user_id"));
        reservation.setRoomId(rs.getInt("room_id"));
        reservation.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        reservation.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        reservation.setStatus(rs.getString("status"));
        reservation.setSubject(rs.getString("subject"));
        reservation.setUsername(rs.getString("username"));
        reservation.setRoomName(rs.getString("room_name"));
        return reservation;
    }
} 