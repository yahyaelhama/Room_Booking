package com.roombooking.controller;

import com.roombooking.dao.RoomDAO;
import com.roombooking.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Controller for handling room management operations
 */
public class RoomController {
    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);
    private final RoomDAO roomDAO;
    
    public RoomController() {
        this.roomDAO = new RoomDAO();
        logger.debug("RoomController initialized");
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
        logger.debug("Creating room: name={}, capacity={}, type={}, location={}", name, capacity, type, location);
        return roomDAO.createRoom(name, capacity, type, location, description);
    }
    
    /**
     * Gets all rooms
     * @return List of all rooms
     */
    public List<Room> getAllRooms() {
        logger.debug("Getting all rooms");
        List<Room> rooms = roomDAO.getAllRooms();
        logger.debug("Retrieved {} rooms", rooms.size());
        return rooms;
    }
    
    /**
     * Gets all available rooms
     * @return List of available rooms
     */
    public List<Room> getAvailableRooms() {
        logger.debug("Getting available rooms");
        List<Room> rooms = roomDAO.getAvailableRooms();
        logger.debug("Retrieved {} available rooms", rooms.size());
        return rooms;
    }
    
    /**
     * Gets a room by ID
     * @param id the room ID
     * @return Room object if found, null otherwise
     */
    public Room getRoom(int id) {
        logger.debug("Getting room with ID: {}", id);
        return roomDAO.getRoom(id);
    }
    
    /**
     * Updates a room's availability
     * @param id the room ID
     * @param active whether the room is active
     * @return true if update successful, false otherwise
     */
    public boolean updateRoomAvailability(int id, boolean active) {
        logger.debug("Updating room {} availability to {}", id, active);
        Room room = roomDAO.getRoom(id);
        if (room != null) {
            room.setActive(active);
            return roomDAO.updateRoom(room);
        }
        return false;
    }
    
    /**
     * Updates a room
     * @param room the room to update
     * @return true if update successful, false otherwise
     */
    public boolean updateRoom(Room room) {
        logger.debug("Updating room: {}", room);
        return roomDAO.updateRoom(room);
    }
    
    /**
     * Deletes a room
     * @param id the room ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteRoom(int id) {
        logger.debug("Deleting room with ID: {}", id);
        return roomDAO.deleteRoom(id);
    }
} 