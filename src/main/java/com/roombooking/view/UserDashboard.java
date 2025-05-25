package com.roombooking.view;

import com.roombooking.controller.ReservationController;
import com.roombooking.controller.RoomController;
import com.roombooking.model.Reservation;
import com.roombooking.model.Room;
import com.roombooking.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard for regular users
 */
public class UserDashboard extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(UserDashboard.class);
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    private final User currentUser;
    private final ReservationController reservationController;
    private final RoomController roomController;
    private JTabbedPane tabbedPane;
    private JTable reservationsTable;
    private JTable roomsTable;
    private DefaultTableModel reservationsModel;
    private DefaultTableModel roomsModel;
    private Timer refreshTimer;
    private JLabel statusLabel;
    
    public UserDashboard(User user) {
        this.currentUser = user;
        this.reservationController = new ReservationController();
        this.roomController = new RoomController();
        initializeComponents();
        setupRefreshTimer();
        refreshData(); // Initial data load
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(LABEL_FONT);
        
        // My Reservations Tab
        JPanel reservationsPanel = createReservationsPanel();
        tabbedPane.addTab("My Reservations", reservationsPanel);
        
        // Available Rooms Tab
        JPanel roomsPanel = createRoomsPanel();
        tabbedPane.addTab("Available Rooms", roomsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status Bar
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(LABEL_FONT);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName());
        welcomeLabel.setFont(HEADING_FONT);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(LABEL_FONT);
        logoutButton.addActionListener(e -> handleLogout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Create table model
        String[] columns = {"ID", "Room", "Start Time", "End Time", "Status", "Subject"};
        reservationsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(reservationsModel);
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newButton = new JButton("New Reservation");
        JButton cancelButton = new JButton("Cancel Reservation");
        
        newButton.addActionListener(e -> handleNewReservation());
        cancelButton.addActionListener(e -> handleCancelReservation());
        
        buttonsPanel.add(newButton);
        buttonsPanel.add(cancelButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Create table model
        String[] columns = {"ID", "Name", "Type", "Location", "Capacity", "Description"};
        roomsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        roomsTable = new JTable(roomsModel);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add a refresh button
        JButton refreshButton = new JButton("Refresh Rooms");
        refreshButton.addActionListener(e -> refreshData());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupRefreshTimer() {
        refreshTimer = new Timer(30000, e -> refreshData());
        refreshTimer.start();
    }
    
    private void refreshData() {
        logger.info("Refreshing dashboard data");
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                updateReservationsTable();
                updateRoomsTable();
                return null;
            }
            
            @Override
            protected void done() {
                statusLabel.setText("Last updated: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        };
        worker.execute();
    }
    
    private void updateReservationsTable() {
        List<Reservation> reservations = reservationController.getUserReservations(currentUser.getId());
        reservationsModel.setRowCount(0);
        for (Reservation reservation : reservations) {
            reservationsModel.addRow(new Object[]{
                reservation.getId(),
                reservation.getRoomName(),
                reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                reservation.getStatus(),
                reservation.getSubject()
            });
        }
    }
    
    private void updateRoomsTable() {
        logger.info("Updating rooms table");
        List<Room> rooms = roomController.getAvailableRooms();
        logger.debug("Retrieved {} rooms from controller", rooms.size());
        
        roomsModel.setRowCount(0);
        for (Room room : rooms) {
            roomsModel.addRow(new Object[]{
                room.getId(),
                room.getName(),
                room.getType(),
                room.getLocation(),
                room.getCapacity(),
                room.getDescription()
            });
            logger.debug("Added room to table: {}", room);
        }
        logger.info("Updated rooms table with {} rooms", rooms.size());
        
        if (rooms.isEmpty()) {
            logger.warn("No rooms available to display");
            // Show a message in the table
            roomsModel.addRow(new Object[]{"", "No rooms available", "", "", "", ""});
        }
    }
    
    private void handleNewReservation() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ReservationDialog dialog = new ReservationDialog(parentFrame, currentUser);
        dialog.setVisible(true);
        
        if (dialog.isReservationCreated()) {
            refreshData();
        }
    }
    
    private void handleCancelReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to cancel",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reservationId = (int) reservationsTable.getValueAt(selectedRow, 0);
        String status = (String) reservationsTable.getValueAt(selectedRow, 4);
        
        if ("CANCELLED".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "This reservation is already cancelled",
                "Cannot Cancel",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this reservation?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (reservationController.cancelReservation(reservationId)) {
                    refreshData();
                    JOptionPane.showMessageDialog(this,
                        "Reservation cancelled successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to cancel reservation",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error cancelling reservation: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleLogout() {
        refreshTimer.stop();
        MainFrame.getInstance().showPanel("login", new LoginPanel());
    }
    
    public void cleanup() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
} 