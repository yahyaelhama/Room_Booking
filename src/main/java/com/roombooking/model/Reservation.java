package com.roombooking.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a room reservation in the system
 */
public class Reservation {
    private int id;
    private int userId;
    private String userName;
    private int roomId;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String subject;
    private String status;
    private String adminComments;
    private List<Equipment> equipment;
    private List<Participant> participants;
    
    public Reservation() {
        this.equipment = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.status = "PENDING";
    }
    
    public Reservation(int userId, int roomId, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.userId = userId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    // Alias for backward compatibility
    public String getUsername() {
        return userName;
    }
    
    // Alias for backward compatibility
    public void setUsername(String username) {
        this.userName = username;
    }
    
    public int getRoomId() {
        return roomId;
    }
    
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    
    public String getRoomName() {
        return roomName;
    }
    
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAdminComments() {
        return adminComments;
    }
    
    public void setAdminComments(String adminComments) {
        this.adminComments = adminComments;
    }
    
    public List<Equipment> getEquipment() {
        return equipment;
    }
    
    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }
    
    public List<Participant> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
    
    public int getDurationHours() {
        return (int) ChronoUnit.HOURS.between(startTime, endTime);
    }
    
    public String getEquipmentList() {
        if (equipment == null || equipment.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (Equipment e : equipment) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(e.getName());
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + id +
            ", userId=" + userId +
            ", userName='" + userName + '\'' +
            ", roomId=" + roomId +
            ", roomName='" + roomName + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", subject='" + subject + '\'' +
            ", status='" + status + '\'' +
            ", adminComments='" + adminComments + '\'' +
            ", equipment=" + getEquipmentList() +
            '}';
    }
} 