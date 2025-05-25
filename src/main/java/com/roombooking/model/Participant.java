package com.roombooking.model;

/**
 * Represents a participant in a room reservation
 */
public class Participant {
    private int id;
    private int reservationId;
    private String name;
    private String email;
    
    public Participant() {}
    
    public Participant(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    public Participant(int id, int reservationId, String name, String email) {
        this.id = id;
        this.reservationId = reservationId;
        this.name = name;
        this.email = email;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return name + " <" + email + ">";
    }
} 