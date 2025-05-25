package com.roombooking.model;

/**
 * Represents a room in the system
 */
public class Room {
    private int id;
    private String name;
    private int capacity;
    private String type;
    private String location;
    private String description;
    private boolean active;
    
    public Room() {}
    
    public Room(String name, int capacity, String type, String location) {
        this.name = name;
        this.capacity = capacity;
        this.type = type;
        this.location = location;
        this.active = true;
    }
    
    public Room(int id, String name, int capacity, String type, String location, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.type = type;
        this.location = location;
        this.description = description;
        this.active = active;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s - Capacity: %d)", name, type, capacity);
    }
} 