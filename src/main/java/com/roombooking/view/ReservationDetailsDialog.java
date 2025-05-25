package com.roombooking.view;

import com.roombooking.model.Reservation;
import com.roombooking.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Dialog for viewing reservation details
 */
public class ReservationDetailsDialog extends JDialog {
    private final Reservation reservation;
    
    public ReservationDetailsDialog(Frame parent, Reservation reservation) {
        super(parent, "Reservation Details", true);
        this.reservation = reservation;
        
        initializeComponents();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(400, 350));
        setResizable(false);
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Header
        JLabel headerLabel = new JLabel("Reservation #" + reservation.getId());
        headerLabel.setFont(ThemeManager.HEADING_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        detailsPanel.add(headerLabel, gbc);
        
        // Status label with color
        JLabel statusLabel = new JLabel(reservation.getStatus());
        statusLabel.setFont(ThemeManager.SUBHEADING_FONT);
        switch (reservation.getStatus()) {
            case "APPROVED":
                statusLabel.setForeground(ThemeManager.SUCCESS_COLOR);
                break;
            case "PENDING":
                statusLabel.setForeground(ThemeManager.WARNING_COLOR);
                break;
            case "CANCELLED":
                statusLabel.setForeground(ThemeManager.ERROR_COLOR);
                break;
            default:
                statusLabel.setForeground(ThemeManager.TEXT_SECONDARY);
                break;
        }
        gbc.gridy = 1;
        detailsPanel.add(statusLabel, gbc);
        
        // Separator
        JSeparator separator = new JSeparator();
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(separator, gbc);
        
        // Room info
        addDetailRow(detailsPanel, "Room:", reservation.getRoomName(), 3);
        
        // User info
        addDetailRow(detailsPanel, "Reserved by:", reservation.getUserName(), 4);
        
        // Subject
        addDetailRow(detailsPanel, "Subject:", reservation.getSubject(), 5);
        
        // Time info
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        addDetailRow(detailsPanel, "Start Time:", reservation.getStartTime().format(formatter), 6);
        addDetailRow(detailsPanel, "End Time:", reservation.getEndTime().format(formatter), 7);
        
        // Duration
        addDetailRow(detailsPanel, "Duration:", reservation.getDurationHours() + " hour(s)", 8);
        
        // Admin comments (if any)
        if (reservation.getAdminComments() != null && !reservation.getAdminComments().isEmpty()) {
            gbc.gridx = 0;
            gbc.gridy = 9;
            gbc.gridwidth = 1;
            detailsPanel.add(new JLabel("Admin Comments:"), gbc);
            
            JTextArea commentsArea = new JTextArea(reservation.getAdminComments());
            commentsArea.setEditable(false);
            commentsArea.setLineWrap(true);
            commentsArea.setWrapStyleWord(true);
            commentsArea.setBackground(detailsPanel.getBackground());
            commentsArea.setFont(ThemeManager.LABEL_FONT);
            
            JScrollPane scrollPane = new JScrollPane(commentsArea);
            scrollPane.setBorder(null);
            scrollPane.setPreferredSize(new Dimension(200, 60));
            
            gbc.gridx = 1;
            gbc.gridy = 9;
            detailsPanel.add(scrollPane, gbc);
        }
        
        add(detailsPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = ThemeManager.createSecondaryButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addDetailRow(JPanel panel, String label, String value, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(ThemeManager.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(labelComponent, gbc);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(ThemeManager.LABEL_FONT);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(valueComponent, gbc);
    }
} 