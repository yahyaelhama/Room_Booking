package com.roombooking.model;

/**
 * Represents equipment in the system
 */
public class Equipment {
    private int id;
    private String name;
    private String type;
    private String description;
    private boolean available;
    
    public Equipment() {}
    
    public Equipment(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.available = true;
    }
    
    public Equipment(int id, String name, String type, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.available = available;
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
} 