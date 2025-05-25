package com.roombooking.util;

import com.roombooking.model.Participant;
import com.roombooking.model.Reservation;
import com.roombooking.model.Room;
import com.roombooking.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service class for handling email notifications
 */
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "your.email@gmail.com"; // Replace with actual email
    private static final String SMTP_PASSWORD = "your-app-password"; // Replace with actual app password
    
    private Properties getEmailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        return props;
    }
    
    private Session getEmailSession() {
        return Session.getInstance(getEmailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
    }
    
    /**
     * Sends a reservation confirmation email to all participants
     * @param reservation the reservation
     * @param room the room
     * @param organizer the user who made the reservation
     * @param participants list of participants
     */
    public void sendReservationConfirmation(Reservation reservation, Room room, 
            User organizer, Participant[] participants) {
        try {
            Session session = getEmailSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            
            // Add participants as recipients
            for (Participant participant : participants) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(participant.getEmail()));
            }
            
            // Add organizer as CC
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(organizer.getEmail()));
            
            message.setSubject("Room Reservation Confirmation");
            
            String emailBody = String.format(
                "Dear Participant,\n\n" +
                "You have been invited to a meeting with the following details:\n\n" +
                "Room: %s\n" +
                "Location: %s\n" +
                "Date: %s\n" +
                "Time: %s - %s\n" +
                "Organizer: %s\n" +
                "Subject: %s\n\n" +
                "Please confirm your attendance.\n\n" +
                "Best regards,\n" +
                "Room Booking System",
                room.getName(),
                room.getLocation(),
                reservation.getStartTime().toLocalDate(),
                reservation.getStartTime().toLocalTime(),
                reservation.getEndTime().toLocalTime(),
                organizer.getFullName(),
                reservation.getSubject()
            );
            
            message.setText(emailBody);
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send confirmation email: {}", e.getMessage(), e);
            // Log error but don't throw - email sending should not block the reservation process
        }
    }
    
    /**
     * Sends a reservation cancellation email to all participants
     * @param reservation the reservation
     * @param room the room
     * @param organizer the user who made the reservation
     * @param participants list of participants
     */
    public void sendReservationCancellation(Reservation reservation, Room room, 
            User organizer, Participant[] participants) {
        try {
            Session session = getEmailSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            
            // Add participants as recipients
            for (Participant participant : participants) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(participant.getEmail()));
            }
            
            // Add organizer as CC
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(organizer.getEmail()));
            
            message.setSubject("Room Reservation Cancellation");
            
            String emailBody = String.format(
                "Dear Participant,\n\n" +
                "The following meeting has been cancelled:\n\n" +
                "Room: %s\n" +
                "Location: %s\n" +
                "Date: %s\n" +
                "Time: %s - %s\n" +
                "Organizer: %s\n" +
                "Subject: %s\n\n" +
                "Best regards,\n" +
                "Room Booking System",
                room.getName(),
                room.getLocation(),
                reservation.getStartTime().toLocalDate(),
                reservation.getStartTime().toLocalTime(),
                reservation.getEndTime().toLocalTime(),
                organizer.getFullName(),
                reservation.getSubject()
            );
            
            message.setText(emailBody);
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send cancellation email: {}", e.getMessage(), e);
            // Log error but don't throw - email sending should not block the cancellation process
        }
    }
    
    /**
     * Sends a reminder email to all participants
     * @param reservation the reservation
     * @param room the room
     * @param organizer the user who made the reservation
     * @param participants list of participants
     */
    public void sendReservationReminder(Reservation reservation, Room room, 
            User organizer, Participant[] participants) {
        try {
            Session session = getEmailSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            
            // Add participants as recipients
            for (Participant participant : participants) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(participant.getEmail()));
            }
            
            // Add organizer as CC
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(organizer.getEmail()));
            
            message.setSubject("Room Reservation Reminder");
            
            String emailBody = String.format(
                "Dear Participant,\n\n" +
                "This is a reminder for your upcoming meeting:\n\n" +
                "Room: %s\n" +
                "Location: %s\n" +
                "Date: %s\n" +
                "Time: %s - %s\n" +
                "Organizer: %s\n" +
                "Subject: %s\n\n" +
                "Best regards,\n" +
                "Room Booking System",
                room.getName(),
                room.getLocation(),
                reservation.getStartTime().toLocalDate(),
                reservation.getStartTime().toLocalTime(),
                reservation.getEndTime().toLocalTime(),
                organizer.getFullName(),
                reservation.getSubject()
            );
            
            message.setText(emailBody);
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send reminder email: {}", e.getMessage(), e);
            // Log error but don't throw - email sending should not block the reminder process
        }
    }
    
    /**
     * Sends a reservation approval email to the organizer
     * @param reservation the reservation
     * @param room the room
     * @param organizer the user who made the reservation
     * @param participants list of participants
     */
    public void sendReservationApproval(Reservation reservation, Room room, 
            User organizer, Participant[] participants) {
        try {
            Session session = getEmailSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            
            // Add organizer as main recipient
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(organizer.getEmail()));
            
            // Add participants as CC
            for (Participant participant : participants) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(participant.getEmail()));
            }
            
            message.setSubject("Room Reservation Approved");
            
            String comments = reservation.getAdminComments() != null && !reservation.getAdminComments().isEmpty() 
                ? "\nAdmin Comments: " + reservation.getAdminComments() + "\n" 
                : "";
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your room reservation has been APPROVED:\n\n" +
                "Room: %s\n" +
                "Location: %s\n" +
                "Date: %s\n" +
                "Time: %s - %s\n" +
                "Subject: %s\n" +
                "%s\n" +
                "Best regards,\n" +
                "Room Booking System",
                organizer.getFullName(),
                room.getName(),
                room.getLocation(),
                reservation.getStartTime().toLocalDate(),
                reservation.getStartTime().toLocalTime(),
                reservation.getEndTime().toLocalTime(),
                reservation.getSubject(),
                comments
            );
            
            message.setText(emailBody);
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send approval email: {}", e.getMessage(), e);
            // Log error but don't throw - email sending should not block the approval process
        }
    }
    
    /**
     * Sends a reservation rejection email to the organizer
     * @param reservation the reservation
     * @param room the room
     * @param organizer the user who made the reservation
     * @param participants list of participants
     */
    public void sendReservationRejection(Reservation reservation, Room room, 
            User organizer, Participant[] participants) {
        try {
            Session session = getEmailSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            
            // Add organizer as main recipient
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(organizer.getEmail()));
            
            message.setSubject("Room Reservation Rejected");
            
            String comments = reservation.getAdminComments() != null && !reservation.getAdminComments().isEmpty() 
                ? "\nReason for rejection: " + reservation.getAdminComments() + "\n" 
                : "";
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Unfortunately, your room reservation has been REJECTED:\n\n" +
                "Room: %s\n" +
                "Location: %s\n" +
                "Date: %s\n" +
                "Time: %s - %s\n" +
                "Subject: %s\n" +
                "%s\n" +
                "Please contact the administrator for more information.\n\n" +
                "Best regards,\n" +
                "Room Booking System",
                organizer.getFullName(),
                room.getName(),
                room.getLocation(),
                reservation.getStartTime().toLocalDate(),
                reservation.getStartTime().toLocalTime(),
                reservation.getEndTime().toLocalTime(),
                reservation.getSubject(),
                comments
            );
            
            message.setText(emailBody);
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send rejection email: {}", e.getMessage(), e);
            // Log error but don't throw - email sending should not block the rejection process
        }
    }
} 