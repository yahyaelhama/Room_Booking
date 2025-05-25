package com.roombooking.controller;

import com.roombooking.dao.EquipmentDAO;
import com.roombooking.model.Equipment;
import com.roombooking.model.Reservation;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Controller for handling equipment operations
 */
public class EquipmentController {
    private final EquipmentDAO equipmentDAO;
    
    public EquipmentController() {
        this.equipmentDAO = new EquipmentDAO();
    }
    
    public List<Equipment> getAllEquipment() {
        return equipmentDAO.getAllEquipment();
    }
    
    public List<Equipment> getAvailableEquipment() {
        return equipmentDAO.getAvailableEquipment();
    }
    
    public Equipment getEquipment(int id) {
        return equipmentDAO.getEquipment(id);
    }
    
    public List<Equipment> getReservationEquipment(int reservationId) {
        return equipmentDAO.getReservationEquipment(reservationId);
    }
    
    public boolean createEquipment(String name, String type, String description) {
        return equipmentDAO.createEquipment(name, type, description);
    }
    
    public boolean updateEquipment(Equipment equipment) {
        return equipmentDAO.updateEquipment(equipment);
    }
    
    public boolean deleteEquipment(int id) {
        return equipmentDAO.deleteEquipment(id);
    }
    
    public boolean assignEquipment(int reservationId, int equipmentId) {
        return equipmentDAO.assignEquipment(reservationId, equipmentId);
    }
    
    public boolean removeEquipment(int reservationId, int equipmentId) {
        return equipmentDAO.removeEquipment(reservationId, equipmentId);
    }
    
    public boolean isEquipmentAvailable(int equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        Equipment equipment = equipmentDAO.getEquipment(equipmentId);
        if (equipment == null || !equipment.isAvailable()) {
            return false;
        }
        
        // Check if equipment is not already reserved for the given time period
        List<Reservation> reservations = equipmentDAO.getEquipmentReservations(equipmentId);
        return reservations.stream().noneMatch(r -> 
            (startTime.isBefore(r.getEndTime()) && endTime.isAfter(r.getStartTime())));
    }
} 