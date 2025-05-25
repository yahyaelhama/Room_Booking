package com.roombooking.controller;

import com.roombooking.dao.ReservationDAO;
import com.roombooking.dao.RoomDAO;
import com.roombooking.dao.UserDAO;
import com.roombooking.model.Reservation;
import com.roombooking.model.Room;
import com.roombooking.model.User;
import com.roombooking.model.Participant;
import com.roombooking.util.EmailService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for handling reservation operations
 */
public class ReservationController {
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final UserDAO userDAO;
    private final EmailService emailService;
    
    public ReservationController() {
        this.reservationDAO = new ReservationDAO();
        this.roomDAO = new RoomDAO();
        this.userDAO = new UserDAO();
        this.emailService = new EmailService();
    }
    
    /**
     * Creates a new reservation
     * @param userId the user ID
     * @param roomId the room ID
     * @param startTime the start time
     * @param duration the duration in hours
     * @param subject the subject of the reservation
     * @param equipmentIds the IDs of the equipment
     * @return true if creation successful, false otherwise
     */
    public boolean createReservation(int userId, int roomId, LocalDateTime startTime, int duration, String subject, List<Integer> equipmentIds) {
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setRoomId(roomId);
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(duration));
        reservation.setSubject(subject);
        reservation.setStatus("PENDING");

        Room room = roomDAO.getRoom(roomId);
        User user = userDAO.getUser(userId);
        
        boolean success = reservationDAO.save(reservation);
        if (success) {
            try {
                emailService.sendReservationConfirmation(reservation, room, user, new Participant[0]);
            } catch (Exception e) {
                System.err.println("Email sending failed: " + e.getMessage());
                // Continue with the reservation creation even if email fails
            }
        }
        return success;
    }
    
    /**
     * Cancels a reservation
     * @param reservationId the reservation ID
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelReservation(int reservationId) {
        return updateReservationStatus(reservationId, "CANCELLED", "Cancelled by user");
    }
    
    /**
     * Gets all reservations
     * @return List of all reservations
     */
    public List<Reservation> getAllReservations() {
        return reservationDAO.findAll();
    }
    
    /**
     * Gets reservations for a user
     * @param userId the user ID
     * @return List of reservations
     */
    public List<Reservation> getUserReservations(int userId) {
        return reservationDAO.getUserReservations(userId);
    }
    
    public Reservation getReservation(int reservationId) {
        return reservationDAO.getReservation(reservationId);
    }
    
    public List<Reservation> getUpcomingReservations(int userId) {
        return reservationDAO.findUpcomingByUserId(userId);
    }
    
    public List<Reservation> getPastReservations(int userId) {
        return reservationDAO.findPastByUserId(userId);
    }
    
    public List<Reservation> getPendingReservations() {
        return reservationDAO.getReservationsByStatus("PENDING");
    }
    
    public List<Reservation> getReservationsInRange(LocalDateTime start, LocalDateTime end) {
        return reservationDAO.getReservationsInRange(start, end);
    }
    
    public boolean updateReservationStatus(int reservationId, String status, String comments) {
        Reservation reservation = reservationDAO.getReservation(reservationId);
        if (reservation == null) {
            return false;
        }

        Room room = roomDAO.getRoom(reservation.getRoomId());
        User user = userDAO.getUser(reservation.getUserId());
        
        reservation.setStatus(status);
        reservation.setAdminComments(comments);

        boolean success = reservationDAO.save(reservation);
        if (success) {
            try {
                switch (status) {
                    case "APPROVED":
                        emailService.sendReservationApproval(reservation, room, user, new Participant[0]);
                        break;
                    case "REJECTED":
                        emailService.sendReservationRejection(reservation, room, user, new Participant[0]);
                        break;
                    case "CANCELLED":
                        emailService.sendReservationCancellation(reservation, room, user, new Participant[0]);
                        break;
                }
            } catch (Exception e) {
                System.err.println("Email sending failed: " + e.getMessage());
                // Continue with the reservation status update even if email fails
            }
        }
        return success;
    }
    
    public boolean isRoomAvailable(int roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return reservationDAO.isRoomAvailable(roomId, startTime, endTime);
    }
    
    public int calculateTotalHours(int roomId, LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = getReservationsInRange(start, end);
        return reservations.stream()
            .filter(r -> r.getRoomId() == roomId)
            .mapToInt(Reservation::getDurationHours)
            .sum();
    }
    
    public int calculateTotalHoursAllRooms(LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = getReservationsInRange(start, end);
        return reservations.stream()
            .mapToInt(Reservation::getDurationHours)
            .sum();
    }
    
    public double calculateAverageDuration(LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = getReservationsInRange(start, end);
        return reservations.stream()
            .mapToInt(Reservation::getDurationHours)
            .average()
            .orElse(0.0);
    }
    
    public String getMostUsedRoom(LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = getReservationsInRange(start, end);
        return reservations.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Reservation::getRoomName,
                java.util.stream.Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }
    
    public String getMostUsedEquipment(LocalDateTime start, LocalDateTime end) {
        // This is a placeholder - actual implementation would need to join with equipment table
        return "Projector";
    }
    
    public Map<String, Integer> getEquipmentUsageStats(LocalDateTime start, LocalDateTime end) {
        // This is a placeholder - actual implementation would need to join with equipment table
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Projector", 10);
        stats.put("Whiteboard", 8);
        stats.put("Conference Phone", 5);
        return stats;
    }
    
    public double calculateUsagePercentageByTimeSlot(LocalDateTime start, LocalDateTime end, int startHour, int endHour) {
        List<Reservation> reservations = getReservationsInRange(start, end);
        long totalInSlot = reservations.stream()
            .filter(r -> {
                int hour = r.getStartTime().getHour();
                return hour >= startHour && hour < endHour;
            })
            .count();
        return reservations.isEmpty() ? 0 : (double) totalInSlot / reservations.size() * 100;
    }
    
    public void exportUtilizationReport(LocalDateTime start, LocalDateTime end, String filename) {
        // This is a placeholder - actual implementation would use Apache POI to create Excel file
        throw new UnsupportedOperationException("Export functionality not implemented yet");
    }
    
    public int countReservations(int roomId, LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = getReservationsInRange(start, end);
        return (int) reservations.stream()
            .filter(r -> r.getRoomId() == roomId)
            .count();
    }
    
    public int countTotalReservations(LocalDateTime start, LocalDateTime end) {
        return getReservationsInRange(start, end).size();
    }
    
    /**
     * Gets a reservation by ID
     * @param reservationId the reservation ID
     * @return the reservation or null if not found
     */
    public Reservation getReservationById(int reservationId) {
        return reservationDAO.getReservation(reservationId);
    }
    
    /**
     * Gets all active reservations (not cancelled or completed)
     * @return List of active reservations
     */
    public List<Reservation> getActiveReservations() {
        return getAllReservations().stream()
            .filter(r -> !"CANCELLED".equals(r.getStatus()) && !"COMPLETED".equals(r.getStatus()))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets active reservations for a specific user
     * @param userId the user ID
     * @return List of active reservations for the user
     */
    public List<Reservation> getActiveUserReservations(int userId) {
        return getUserReservations(userId).stream()
            .filter(r -> !"CANCELLED".equals(r.getStatus()) && !"COMPLETED".equals(r.getStatus()))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets upcoming reservations for a specific user (future start time)
     * @param userId the user ID
     * @return List of upcoming reservations for the user
     */
    public List<Reservation> getUpcomingUserReservations(int userId) {
        LocalDateTime now = LocalDateTime.now();
        return getUserReservations(userId).stream()
            .filter(r -> r.getStartTime().isAfter(now) && !"CANCELLED".equals(r.getStatus()))
            .collect(Collectors.toList());
    }
} 