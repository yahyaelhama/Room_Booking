package com.roombooking.view;

import com.roombooking.controller.AuthController;
import com.roombooking.controller.ReservationController;
import com.roombooking.controller.RoomController;
import com.roombooking.model.Reservation;
import com.roombooking.model.Room;
import com.roombooking.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard for administrators
 */
public class AdminDashboard extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    private final User adminUser;
    private final ReservationController reservationController;
    private final RoomController roomController;
    private final AuthController authController;
    private JTabbedPane tabbedPane;
    private JTable reservationsTable;
    private JTable roomsTable;
    private JTable usersTable;
    private DefaultTableModel reservationsModel;
    private DefaultTableModel roomsModel;
    private DefaultTableModel usersModel;
    private Timer refreshTimer;
    private JLabel statusLabel;
    
    public AdminDashboard(User admin) {
        this.adminUser = admin;
        this.reservationController = new ReservationController();
        this.roomController = new RoomController();
        this.authController = new AuthController();
        initializeComponents();
        setupRefreshTimer();
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
        
        // Reservations Tab
        JPanel reservationsPanel = createReservationsPanel();
        tabbedPane.addTab("Reservations", reservationsPanel);
        
        // Rooms Tab
        JPanel roomsPanel = createRoomsPanel();
        tabbedPane.addTab("Rooms", roomsPanel);
        
        // Users Tab
        JPanel usersPanel = createUsersPanel();
        tabbedPane.addTab("Users", usersPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status Bar
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(LABEL_FONT);
        add(statusLabel, BorderLayout.SOUTH);
        
        refreshData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel welcomeLabel = new JLabel("Admin Dashboard - " + adminUser.getFullName());
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
        String[] columns = {"ID", "User", "Room", "Start Time", "End Time", "Status", "Subject"};
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
        JButton viewButton = new JButton("View Details");
        JButton cancelButton = new JButton("Cancel Reservation");
        
        viewButton.addActionListener(e -> handleViewReservation());
        cancelButton.addActionListener(e -> handleCancelReservation());
        
        buttonsPanel.add(viewButton);
        buttonsPanel.add(cancelButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Create table model
        String[] columns = {"ID", "Name", "Type", "Location", "Capacity", "Description", "Active"};
        roomsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        roomsTable = new JTable(roomsModel);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Room");
        JButton editButton = new JButton("Edit Room");
        JButton deleteButton = new JButton("Delete Room");
        
        addButton.addActionListener(e -> handleAddRoom());
        editButton.addActionListener(e -> handleEditRoom());
        deleteButton.addActionListener(e -> handleDeleteRoom());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Create table model
        String[] columns = {"ID", "Username", "Full Name", "Email", "Admin", "Active"};
        usersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable = new JTable(usersModel);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton toggleButton = new JButton("Toggle Active");
        
        addButton.addActionListener(e -> handleAddUser());
        editButton.addActionListener(e -> handleEditUser());
        toggleButton.addActionListener(e -> handleToggleUserActive());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(toggleButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupRefreshTimer() {
        refreshTimer = new Timer(30000, e -> refreshData());
        refreshTimer.start();
    }
    
    private void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                updateReservationsTable();
                updateRoomsTable();
                updateUsersTable();
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
        List<Reservation> reservations = reservationController.getAllReservations();
        reservationsModel.setRowCount(0);
        for (Reservation reservation : reservations) {
            reservationsModel.addRow(new Object[]{
                reservation.getId(),
                reservation.getUsername(),
                reservation.getRoomName(),
                reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                reservation.getStatus(),
                reservation.getSubject()
            });
        }
    }
    
    private void updateRoomsTable() {
        roomsModel.setRowCount(0);
        List<Room> rooms = roomController.getAllRooms();
        for (Room room : rooms) {
            roomsModel.addRow(new Object[]{
                room.getId(),
                room.getName(),
                room.getType(),
                room.getLocation(),
                room.getCapacity(),
                room.getDescription(),
                room.isActive()
            });
        }
    }
    
    private void updateUsersTable() {
        List<User> users = authController.getAllUsers();
        usersModel.setRowCount(0);
        for (User user : users) {
            usersModel.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.isAdmin() ? "Yes" : "No",
                user.isActive() ? "Yes" : "No"
            });
        }
    }
    
    private void handleViewReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to view",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsTable.getValueAt(selectedRow, 0);
        Reservation reservation = reservationController.getReservation(reservationId);
        if (reservation != null) {
            showReservationDetails(reservation);
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
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this reservation?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                reservationController.cancelReservation(reservationId);
                refreshData();
                JOptionPane.showMessageDialog(this,
                    "Reservation cancelled successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error cancelling reservation: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleAddRoom() {
        createRoomDialog();
    }
    
    private void handleEditRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a room to edit",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int roomId = (Integer) roomsModel.getValueAt(selectedRow, 0);
        Room room = roomController.getRoom(roomId);
        if (room == null) {
            JOptionPane.showMessageDialog(this,
                "Failed to load room details",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        editRoomDialog(room);
    }
    
    private void editRoomDialog(Room room) {
        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, "Edit Room", true);
        } else if (window instanceof Dialog) {
            dialog = new JDialog((Dialog) window, "Edit Room", true);
        } else {
            dialog = new JDialog((Frame) null, "Edit Room", true);
        }
        
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField nameField = new JTextField(room.getName(), 20);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(room.getCapacity(), 1, 100, 1));
        JTextField typeField = new JTextField(room.getType(), 20);
        JTextField locationField = new JTextField(room.getLocation(), 20);
        JTextArea descriptionArea = new JTextArea(room.getDescription(), 3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JCheckBox activeCheckbox = new JCheckBox("Active", room.isActive());
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        dialog.add(capacitySpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        dialog.add(locationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JScrollPane(descriptionArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        dialog.add(activeCheckbox, gbc);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            int capacity = (Integer) capacitySpinner.getValue();
            String type = typeField.getText().trim();
            String location = locationField.getText().trim();
            String description = descriptionArea.getText().trim();
            boolean active = activeCheckbox.isSelected();
            
            if (name.isEmpty() || type.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            room.setName(name);
            room.setCapacity(capacity);
            room.setType(type);
            room.setLocation(location);
            room.setDescription(description);
            room.setActive(active);
            
            if (roomController.updateRoom(room)) {
                dialog.dispose();
                updateRoomsTable();
                JOptionPane.showMessageDialog(this,
                    "Room updated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Failed to update room",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void handleDeleteRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a room to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int roomId = (Integer) roomsModel.getValueAt(selectedRow, 0);
        String roomName = (String) roomsModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete room '" + roomName + "'?\n" +
            "This will also delete all reservations for this room.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (roomController.deleteRoom(roomId)) {
                updateRoomsTable();
                JOptionPane.showMessageDialog(this,
                    "Room deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete room. It may be in use by active reservations.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleAddUser() {
        // TODO: Implement user addition dialog
        JOptionPane.showMessageDialog(this, "Add user feature coming soon!");
    }
    
    private void handleEditUser() {
        // TODO: Implement user editing dialog
        JOptionPane.showMessageDialog(this, "Edit user feature coming soon!");
    }
    
    private void handleToggleUserActive() {
        // TODO: Implement user activation toggle
        JOptionPane.showMessageDialog(this, "Toggle user active feature coming soon!");
    }
    
    private void handleLogout() {
        refreshTimer.stop();
        MainFrame.getInstance().showPanel("login", new LoginPanel());
    }
    
    private void showReservationDetails(Reservation reservation) {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Reservation Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add reservation details
        contentPanel.add(new JLabel("ID: " + reservation.getId()), gbc);
        gbc.gridy++;
        contentPanel.add(new JLabel("User: " + reservation.getUsername()), gbc);
        gbc.gridy++;
        contentPanel.add(new JLabel("Room: " + reservation.getRoomName()), gbc);
        gbc.gridy++;
        contentPanel.add(new JLabel("Start Time: " + 
            reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))), gbc);
        gbc.gridy++;
        contentPanel.add(new JLabel("End Time: " + 
            reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))), gbc);
        gbc.gridy++;
        contentPanel.add(new JLabel("Status: " + reservation.getStatus()), gbc);
        gbc.gridy++;
        contentPanel.add(new JLabel("Subject: " + reservation.getSubject()), gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void createRoomDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, "Create Room", true);
        } else if (window instanceof Dialog) {
            dialog = new JDialog((Dialog) window, "Create Room", true);
        } else {
            dialog = new JDialog((Frame) null, "Create Room", true);
        }
        
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField nameField = new JTextField(20);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JTextField typeField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        dialog.add(capacitySpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        dialog.add(locationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JScrollPane(descriptionArea), gbc);
        
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            int capacity = (Integer) capacitySpinner.getValue();
            String type = typeField.getText().trim();
            String location = locationField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            if (name.isEmpty() || type.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (roomController.createRoom(name, capacity, type, location, description)) {
                dialog.dispose();
                updateRoomsTable();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Failed to create room",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void toggleRoomAvailability(int row) {
        int roomId = (Integer) roomsModel.getValueAt(row, 0);
        boolean currentStatus = (Boolean) roomsModel.getValueAt(row, 6);
        if (roomController.updateRoomAvailability(roomId, !currentStatus)) {
            updateRoomsTable();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to update room availability",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void cleanup() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
} 