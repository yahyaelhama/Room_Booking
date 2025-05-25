package com.roombooking.view;

import com.roombooking.controller.AuthController;
import com.roombooking.controller.ReservationController;
import com.roombooking.controller.RoomController;
import com.roombooking.model.Reservation;
import com.roombooking.model.Room;
import com.roombooking.model.User;
import com.roombooking.util.ThemeManager;
import com.roombooking.view.components.DashboardPanel;
import com.roombooking.view.components.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard for administrators
 */
public class AdminDashboard extends DashboardPanel {
    private final ReservationController reservationController;
    private final RoomController roomController;
    private final AuthController authController;
    
    private JTable reservationsTable;
    private JTable roomsTable;
    private JTable usersTable;
    private DefaultTableModel reservationsModel;
    private DefaultTableModel roomsModel;
    private DefaultTableModel usersModel;
    private Timer refreshTimer;
    
    // Dashboard stats
    private StatCard totalRoomsCard;
    private StatCard activeReservationsCard;
    private StatCard totalUsersCard;
    
    public AdminDashboard(User admin) {
        super(admin);
        
        this.reservationController = new ReservationController();
        this.roomController = new RoomController();
        this.authController = new AuthController();
        
        setupNavigation();
        createDashboardPanel();
        createReservationsPanel();
        createRoomsPanel();
        createUsersPanel();
        
        // Show dashboard panel initially
        showContentPanel("dashboard");
        
        // Set up automatic refresh
        setupRefreshTimer();
        
        // Initial data load
        refreshData();
    }
    
    private void setupNavigation() {
        addNavigationItem("Dashboard", "dashboard.png", "dashboard");
        addNavigationItem("Reservations", "calendar.png", "reservations");
        addNavigationItem("Rooms", "room.png", "rooms");
        addNavigationItem("Users", "users.png", "users");
        addNavigationItem("Reports", "chart.png", "reports");
    }
    
    private void createDashboardPanel() {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BorderLayout(15, 15));
        dashboardPanel.setOpaque(false);
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel welcomeLabel = ThemeManager.createHeaderLabel("Admin Dashboard");
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        
        dashboardPanel.add(welcomePanel, BorderLayout.NORTH);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        
        totalRoomsCard = new StatCard("Total Rooms", "0", "room.png");
        activeReservationsCard = new StatCard("Active Reservations", "0", "calendar.png");
        activeReservationsCard.setAccentColor(ThemeManager.SUCCESS_COLOR);
        totalUsersCard = new StatCard("Registered Users", "0", "users.png");
        totalUsersCard.setAccentColor(ThemeManager.ACCENT_COLOR);
        
        statsPanel.add(totalRoomsCard);
        statsPanel.add(activeReservationsCard);
        statsPanel.add(totalUsersCard);
        
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Recent reservations
        JPanel recentPanel = ThemeManager.createCardPanel();
        recentPanel.setLayout(new BorderLayout());
        
        JLabel recentLabel = new JLabel("Recent Reservations");
        recentLabel.setFont(ThemeManager.SUBHEADING_FONT);
        recentLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        recentPanel.add(recentLabel, BorderLayout.NORTH);
        
        // Table for recent reservations
        String[] columns = {"ID", "User", "Room", "Start Time", "End Time", "Status"};
        DefaultTableModel recentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable recentTable = new JTable(recentModel);
        recentTable.setRowHeight(30);
        recentTable.getTableHeader().setFont(ThemeManager.LABEL_FONT);
        JScrollPane scrollPane = new JScrollPane(recentTable);
        recentPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerPanel.add(recentPanel, BorderLayout.CENTER);
        dashboardPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add dashboard to content panel
        addContentPanel(dashboardPanel, "dashboard");
    }
    
    private void createReservationsPanel() {
        JPanel reservationsPanel = new JPanel(new BorderLayout(0, 15));
        reservationsPanel.setOpaque(false);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("Reservations");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        JButton searchButton = ThemeManager.createSecondaryButton("Search");
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        reservationsPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Reservations table
        String[] columns = {"ID", "User", "Room", "Start Time", "End Time", "Status", "Subject"};
        reservationsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(reservationsModel);
        reservationsTable.setRowHeight(30);
        reservationsTable.getTableHeader().setFont(ThemeManager.LABEL_FONT);
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(reservationsModel);
        reservationsTable.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        JPanel tablePanel = ThemeManager.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        reservationsPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        JButton viewButton = ThemeManager.createPrimaryButton("View Details");
        JButton approveButton = ThemeManager.createSuccessButton("Approve Reservation");
        JButton rejectButton = ThemeManager.createSecondaryButton("Reject Reservation");
        JButton cancelButton = ThemeManager.createDangerButton("Cancel Reservation");
        
        viewButton.addActionListener(e -> handleViewReservation());
        approveButton.addActionListener(e -> handleApproveReservation());
        rejectButton.addActionListener(e -> handleRejectReservation());
        cancelButton.addActionListener(e -> handleCancelReservation());
        
        buttonsPanel.add(viewButton);
        buttonsPanel.add(approveButton);
        buttonsPanel.add(rejectButton);
        buttonsPanel.add(cancelButton);
        
        reservationsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Add to content panel
        addContentPanel(reservationsPanel, "reservations");
        
        // Add search functionality
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            if (searchText.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        });
    }
    
    private void createRoomsPanel() {
        JPanel roomsPanel = new JPanel(new BorderLayout(0, 15));
        roomsPanel.setOpaque(false);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("Rooms");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        JButton searchButton = ThemeManager.createSecondaryButton("Search");
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        roomsPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Rooms table
        String[] columns = {"ID", "Name", "Type", "Location", "Capacity", "Description", "Active"};
        roomsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        roomsTable = new JTable(roomsModel);
        roomsTable.setRowHeight(30);
        roomsTable.getTableHeader().setFont(ThemeManager.LABEL_FONT);
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(roomsModel);
        roomsTable.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        JPanel tablePanel = ThemeManager.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        roomsPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        JButton addButton = ThemeManager.createPrimaryButton("Add Room");
        JButton editButton = ThemeManager.createSecondaryButton("Edit Room");
        JButton deleteButton = ThemeManager.createDangerButton("Delete Room");
        
        addButton.addActionListener(e -> handleAddRoom());
        editButton.addActionListener(e -> handleEditRoom());
        deleteButton.addActionListener(e -> handleDeleteRoom());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        
        roomsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Add to content panel
        addContentPanel(roomsPanel, "rooms");
        
        // Add search functionality
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            if (searchText.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        });
    }
    
    private void createUsersPanel() {
        JPanel usersPanel = new JPanel(new BorderLayout(0, 15));
        usersPanel.setOpaque(false);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("Users");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        JButton searchButton = ThemeManager.createSecondaryButton("Search");
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        usersPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Users table
        String[] columns = {"ID", "Username", "Full Name", "Email", "Admin", "Active"};
        usersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable = new JTable(usersModel);
        usersTable.setRowHeight(30);
        usersTable.getTableHeader().setFont(ThemeManager.LABEL_FONT);
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(usersModel);
        usersTable.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        JPanel tablePanel = ThemeManager.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        usersPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        JButton addButton = ThemeManager.createPrimaryButton("Add User");
        JButton editButton = ThemeManager.createSecondaryButton("Edit User");
        JButton toggleButton = ThemeManager.createDangerButton("Toggle Active");
        
        addButton.addActionListener(e -> handleAddUser());
        editButton.addActionListener(e -> handleEditUser());
        toggleButton.addActionListener(e -> handleToggleUserActive());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(toggleButton);
        
        usersPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Add to content panel
        addContentPanel(usersPanel, "users");
        
        // Add search functionality
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            if (searchText.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        });
    }
    
    private void setupRefreshTimer() {
        refreshTimer = new Timer(30000, e -> refreshData());
        refreshTimer.start();
    }
    
    private void refreshData() {
        MainFrame.getInstance().setStatus("Refreshing data...");
        MainFrame.getInstance().showProgress(true);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                updateReservationsTable();
                updateRoomsTable();
                updateUsersTable();
                updateDashboardStats();
                return null;
            }
            
            @Override
            protected void done() {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                MainFrame.getInstance().setStatus("Last updated: " + now.format(formatter));
                MainFrame.getInstance().showProgress(false);
            }
        };
        worker.execute();
    }
    
    private void updateReservationsTable() {
        List<Reservation> reservations = reservationController.getAllReservations();
        System.out.println("AdminDashboard: Found " + reservations.size() + " reservations");
        reservationsModel.setRowCount(0);
        
        for (Reservation reservation : reservations) {
            reservationsModel.addRow(new Object[]{
                reservation.getId(),
                reservation.getUserName(),
                reservation.getRoomName(),
                reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                reservation.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                reservation.getStatus(),
                reservation.getSubject()
            });
        }
    }
    
    private void updateRoomsTable() {
        List<Room> rooms = roomController.getAllRooms();
        roomsModel.setRowCount(0);
        
        for (Room room : rooms) {
            roomsModel.addRow(new Object[]{
                room.getId(),
                room.getName(),
                room.getType(),
                room.getLocation(),
                room.getCapacity(),
                room.getDescription(),
                room.isActive() ? "Yes" : "No"
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
    
    private void updateDashboardStats() {
        // Update stats cards
        int totalRooms = roomController.getAllRooms().size();
        totalRoomsCard.setValue(String.valueOf(totalRooms));
        
        int activeReservations = reservationController.getActiveReservations().size();
        activeReservationsCard.setValue(String.valueOf(activeReservations));
        
        int totalUsers = authController.getAllUsers().size();
        totalUsersCard.setValue(String.valueOf(totalUsers));
    }
    
    private void handleViewReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to view",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsTable.getValueAt(
            reservationsTable.convertRowIndexToModel(selectedRow), 0);
        
        Reservation reservation = reservationController.getReservationById(reservationId);
        if (reservation != null) {
            showReservationDetails(reservation);
        }
    }
    
    private void handleApproveReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to approve",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsTable.getValueAt(
            reservationsTable.convertRowIndexToModel(selectedRow), 0);
        String status = (String) reservationsTable.getValueAt(
            reservationsTable.convertRowIndexToModel(selectedRow), 5);
            
        if (!"PENDING".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "Only pending reservations can be approved",
                "Invalid Status",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to approve this reservation?",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Show comment dialog
            String comment = JOptionPane.showInputDialog(this,
                "Add any comments (optional):",
                "Approval Comments",
                JOptionPane.PLAIN_MESSAGE);
            
            MainFrame.getInstance().showProgress(true);
            MainFrame.getInstance().setStatus("Approving reservation...");
            
            try {
                boolean success = reservationController.updateReservationStatus(
                    reservationId, "APPROVED", comment);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Reservation approved successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to approve reservation",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                MainFrame.getInstance().showProgress(false);
                MainFrame.getInstance().setStatus("Ready");
            }
        }
    }
    
    private void handleRejectReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to reject",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsTable.getValueAt(
            reservationsTable.convertRowIndexToModel(selectedRow), 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reject this reservation?",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            MainFrame.getInstance().showProgress(true);
            MainFrame.getInstance().setStatus("Rejecting reservation...");
            
            try {
                boolean success = reservationController.updateReservationStatus(
                    reservationId, "REJECTED", "Rejected by administrator");
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Reservation rejected successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to reject reservation",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                MainFrame.getInstance().showProgress(false);
                MainFrame.getInstance().setStatus("Ready");
            }
        }
    }
    
    private void handleCancelReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a reservation to cancel",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsTable.getValueAt(
            reservationsTable.convertRowIndexToModel(selectedRow), 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this reservation?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            MainFrame.getInstance().showProgress(true);
            MainFrame.getInstance().setStatus("Cancelling reservation...");
            
            try {
                boolean success = reservationController.cancelReservation(reservationId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Reservation cancelled successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to cancel reservation",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                MainFrame.getInstance().showProgress(false);
                MainFrame.getInstance().setStatus("Ready");
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
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int roomId = (int) roomsTable.getValueAt(
            roomsTable.convertRowIndexToModel(selectedRow), 0);
        
        Room room = roomController.getRoomById(roomId);
        if (room != null) {
            editRoomDialog(room);
        }
    }
    
    private void handleDeleteRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a room to delete",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int roomId = (int) roomsTable.getValueAt(
            roomsTable.convertRowIndexToModel(selectedRow), 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this room?\nThis will also delete all associated reservations.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            MainFrame.getInstance().showProgress(true);
            MainFrame.getInstance().setStatus("Deleting room...");
            
            try {
                boolean success = roomController.deleteRoom(roomId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Room deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete room",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                MainFrame.getInstance().showProgress(false);
                MainFrame.getInstance().setStatus("Ready");
            }
        }
    }
    
    private void handleAddUser() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField fullNameField = new JTextField(20);
        formPanel.add(fullNameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Admin checkbox
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JCheckBox adminCheckBox = new JCheckBox("Administrator");
        formPanel.add(adminCheckBox, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = ThemeManager.createPrimaryButton("Create User");
        JButton cancelButton = ThemeManager.createSecondaryButton("Cancel");
        
        saveButton.addActionListener(e -> {
            // Validate input
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            boolean isAdmin = adminCheckBox.isSelected();
            
            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "All fields are required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Create user with admin status if checkbox is selected
                boolean success;
                if (isAdmin) {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setFullName(fullName);
                    newUser.setEmail(email);
                    newUser.setAdmin(true);
                    newUser.setActive(true);
                    success = authController.register(username, password, true);
                } else {
                    success = authController.registerUser(username, fullName, email, password);
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                        "User created successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to create user. Username may already exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error creating user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setMinimumSize(new Dimension(400, 300));
        dialog.setVisible(true);
    }
    
    private void handleEditUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to edit",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int userId = (int) usersTable.getValueAt(
            usersTable.convertRowIndexToModel(selectedRow), 0);
        
        User user = authController.getUser(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                "User not found",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField usernameField = new JTextField(user.getUsername(), 20);
        usernameField.setEditable(false);
        formPanel.add(usernameField, gbc);
        
        // Admin checkbox
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JCheckBox adminCheckBox = new JCheckBox("Administrator");
        adminCheckBox.setSelected(user.isAdmin());
        formPanel.add(adminCheckBox, gbc);
        
        // Active checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JCheckBox activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setSelected(user.isActive());
        formPanel.add(activeCheckBox, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = ThemeManager.createPrimaryButton("Save Changes");
        JButton cancelButton = ThemeManager.createSecondaryButton("Cancel");
        
        saveButton.addActionListener(e -> {
            boolean isAdmin = adminCheckBox.isSelected();
            boolean isActive = activeCheckBox.isSelected();
            
            try {
                boolean success = authController.updateUser(userId, isAdmin, isActive);
                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                        "User updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to update user",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error updating user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setMinimumSize(new Dimension(400, 200));
        dialog.setVisible(true);
    }
    
    private void handleToggleUserActive() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to toggle active status",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int userId = (int) usersTable.getValueAt(
            usersTable.convertRowIndexToModel(selectedRow), 0);
        String username = (String) usersTable.getValueAt(
            usersTable.convertRowIndexToModel(selectedRow), 1);
        boolean isCurrentlyActive = "Yes".equals(usersTable.getValueAt(
            usersTable.convertRowIndexToModel(selectedRow), 5));
        
        // Don't allow deactivating your own account
        if (userId == getCurrentUser().getId()) {
            JOptionPane.showMessageDialog(this,
                "You cannot deactivate your own account",
                "Operation Not Allowed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String action = isCurrentlyActive ? "deactivate" : "activate";
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to " + action + " user '" + username + "'?",
            "Confirm " + (isCurrentlyActive ? "Deactivation" : "Activation"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            User user = authController.getUser(userId);
            if (user != null) {
                try {
                    boolean success = authController.updateUser(userId, user.isAdmin(), !isCurrentlyActive);
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "User " + (isCurrentlyActive ? "deactivated" : "activated") + " successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        refreshData();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to " + action + " user",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showReservationDetails(Reservation reservation) {
        ReservationDetailsDialog dialog = new ReservationDetailsDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            reservation
        );
        dialog.setVisible(true);
    }
    
    private void createRoomDialog() {
        RoomDialog dialog = new RoomDialog(
            (Frame) SwingUtilities.getWindowAncestor(this)
        );
        dialog.setVisible(true);
        
        if (dialog.isRoomSaved()) {
            refreshData();
        }
    }
    
    private void editRoomDialog(Room room) {
        RoomDialog dialog = new RoomDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            room
        );
        dialog.setVisible(true);
        
        if (dialog.isRoomSaved()) {
            refreshData();
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
} 