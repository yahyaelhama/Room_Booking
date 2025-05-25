package com.roombooking.view;

import com.roombooking.controller.ReservationController;
import com.roombooking.controller.RoomController;
import com.roombooking.model.Reservation;
import com.roombooking.model.Room;
import com.roombooking.model.User;
import com.roombooking.util.ThemeManager;
import com.roombooking.view.components.DashboardPanel;
import com.roombooking.view.components.StatCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard for regular users
 */
public class UserDashboard extends DashboardPanel {
    private static final Logger logger = LoggerFactory.getLogger(UserDashboard.class);
    
    private final ReservationController reservationController;
    private final RoomController roomController;
    private JTable reservationsTable;
    private JTable roomsTable;
    private DefaultTableModel reservationsModel;
    private DefaultTableModel roomsModel;
    private Timer refreshTimer;
    
    // Dashboard stats
    private StatCard activeReservationsCard;
    private StatCard upcomingReservationsCard;
    private StatCard availableRoomsCard;

    public UserDashboard(User user) {
        super(user);
        
        this.reservationController = new ReservationController();
        this.roomController = new RoomController();
        
        setupNavigation();
        createDashboardPanel();
        createReservationsPanel();
        createRoomsPanel();
        createCalendarPanel();
        
        // Show dashboard panel initially
        showContentPanel("dashboard");
        
        // Set up automatic refresh
        setupRefreshTimer();
        
        // Initial data load
        refreshData();
    }
    
    private void setupNavigation() {
        addNavigationItem("Dashboard", "dashboard.png", "dashboard");
        addNavigationItem("My Reservations", "calendar.png", "reservations");
        addNavigationItem("Available Rooms", "room.png", "rooms");
        addNavigationItem("Calendar View", "calendar-week.png", "calendar");
    }
    
    private void createDashboardPanel() {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BorderLayout(15, 15));
        dashboardPanel.setOpaque(false);
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel welcomeLabel = ThemeManager.createHeaderLabel("User Dashboard");
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        
        dashboardPanel.add(welcomePanel, BorderLayout.NORTH);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        
        activeReservationsCard = new StatCard("Active Reservations", "0", "calendar.png");
        activeReservationsCard.setAccentColor(ThemeManager.PRIMARY_COLOR);
        
        upcomingReservationsCard = new StatCard("Upcoming Reservations", "0", "clock.png");
        upcomingReservationsCard.setAccentColor(ThemeManager.ACCENT_COLOR);
        
        availableRoomsCard = new StatCard("Available Rooms", "0", "room.png");
        availableRoomsCard.setAccentColor(ThemeManager.SUCCESS_COLOR);
        
        statsPanel.add(activeReservationsCard);
        statsPanel.add(upcomingReservationsCard);
        statsPanel.add(availableRoomsCard);
        
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Quick actions panel
        JPanel actionsPanel = ThemeManager.createCardPanel();
        actionsPanel.setLayout(new BorderLayout(0, 10));
        
        JLabel actionsLabel = new JLabel("Quick Actions");
        actionsLabel.setFont(ThemeManager.SUBHEADING_FONT);
        actionsLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        actionsPanel.add(actionsLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonsPanel.setOpaque(false);
        
        JButton newReservationButton = ThemeManager.createPrimaryButton("New Reservation");
        JButton viewRoomsButton = ThemeManager.createSecondaryButton("View Rooms");
        JButton viewCalendarButton = ThemeManager.createSecondaryButton("View Calendar");
        
        newReservationButton.addActionListener(e -> handleNewReservation());
        viewRoomsButton.addActionListener(e -> showContentPanel("rooms"));
        viewCalendarButton.addActionListener(e -> showContentPanel("calendar"));
        
        buttonsPanel.add(newReservationButton);
        buttonsPanel.add(viewRoomsButton);
        buttonsPanel.add(viewCalendarButton);
        
        actionsPanel.add(buttonsPanel, BorderLayout.CENTER);
        centerPanel.add(actionsPanel, BorderLayout.CENTER);
        
        // Recent reservations
        JPanel recentPanel = ThemeManager.createCardPanel();
        recentPanel.setLayout(new BorderLayout());
        
        JLabel recentLabel = new JLabel("My Recent Reservations");
        recentLabel.setFont(ThemeManager.SUBHEADING_FONT);
        recentLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        recentPanel.add(recentLabel, BorderLayout.NORTH);
        
        // Table for recent reservations
        String[] columns = {"ID", "Room", "Start Time", "End Time", "Status"};
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
        
        centerPanel.add(recentPanel, BorderLayout.SOUTH);
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
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("My Reservations");
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
        String[] columns = {"ID", "Room", "Start Time", "End Time", "Status", "Subject"};
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
        
        JButton newButton = ThemeManager.createPrimaryButton("New Reservation");
        JButton cancelButton = ThemeManager.createDangerButton("Cancel Reservation");
        
        newButton.addActionListener(e -> handleNewReservation());
        cancelButton.addActionListener(e -> handleCancelReservation());
        
        buttonsPanel.add(newButton);
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
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("Available Rooms");
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
        String[] columns = {"ID", "Name", "Type", "Location", "Capacity", "Description"};
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
        
        JButton reserveButton = ThemeManager.createPrimaryButton("Reserve Room");
        JButton detailsButton = ThemeManager.createSecondaryButton("View Details");
        
        reserveButton.addActionListener(e -> handleReserveRoom());
        detailsButton.addActionListener(e -> handleViewRoomDetails());
        
        buttonsPanel.add(reserveButton);
        buttonsPanel.add(detailsButton);
        
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
    
    private void createCalendarPanel() {
        JPanel calendarPanel = new JPanel(new BorderLayout(0, 15));
        calendarPanel.setOpaque(false);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = ThemeManager.createHeaderLabel("Calendar View");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Calendar content
        JPanel contentPanel = ThemeManager.createCardPanel();
        contentPanel.setLayout(new BorderLayout());
        
        // For now, just add a placeholder
        JLabel placeholderLabel = new JLabel("Calendar view will be implemented soon");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        placeholderLabel.setFont(ThemeManager.LABEL_FONT);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        calendarPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add to content panel
        addContentPanel(calendarPanel, "calendar");
    }
    
    private void setupRefreshTimer() {
        refreshTimer = new Timer(30000, e -> refreshData());
        refreshTimer.start();
    }
    
    private void refreshData() {
        logger.info("Refreshing dashboard data");
        MainFrame.getInstance().setStatus("Refreshing data...");
        MainFrame.getInstance().showProgress(true);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                updateReservationsTable();
                updateRoomsTable();
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
        List<Reservation> reservations = reservationController.getUserReservations(getCurrentUser().getId());
        System.out.println("UserDashboard: Found " + reservations.size() + " reservations for user ID " + getCurrentUser().getId());
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
        }
    }
    
    private void updateDashboardStats() {
        int userId = getCurrentUser().getId();
        
        // Update active reservations count
        int activeCount = reservationController.getActiveUserReservations(userId).size();
        activeReservationsCard.setValue(String.valueOf(activeCount));
        
        // Update upcoming reservations count
        int upcomingCount = reservationController.getUpcomingUserReservations(userId).size();
        upcomingReservationsCard.setValue(String.valueOf(upcomingCount));
        
        // Update available rooms count
        int availableCount = roomController.getAvailableRooms().size();
        availableRoomsCard.setValue(String.valueOf(availableCount));
    }
    
    private void handleNewReservation() {
        ReservationDialog dialog = new ReservationDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            getCurrentUser()
        );
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
    
    private void handleReserveRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a room to reserve",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int roomId = (int) roomsTable.getValueAt(
            roomsTable.convertRowIndexToModel(selectedRow), 0);
        
        Room room = roomController.getRoomById(roomId);
        if (room != null) {
            // Open reservation dialog
            ReservationDialog dialog = new ReservationDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                getCurrentUser()
            );
            dialog.setVisible(true);
            
            if (dialog.isReservationCreated()) {
                refreshData();
            }
        }
    }
    
    private void handleViewRoomDetails(Room room) {
        // Create a simple dialog to show room details
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Room Details",
            true
        );
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Room name header
        JLabel nameLabel = new JLabel(room.getName());
        nameLabel.setFont(ThemeManager.HEADING_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        detailsPanel.add(nameLabel, gbc);
        
        // Room type
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        detailsPanel.add(new JLabel("Type:"), gbc);
        
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(room.getType()), gbc);
        
        // Location
        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsPanel.add(new JLabel("Location:"), gbc);
        
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(room.getLocation()), gbc);
        
        // Capacity
        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsPanel.add(new JLabel("Capacity:"), gbc);
        
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(room.getCapacity())), gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 4;
        detailsPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(room.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(detailsPanel.getBackground());
        JScrollPane scrollPane = new JScrollPane(descArea);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        scrollPane.setBorder(null);
        detailsPanel.add(scrollPane, gbc);
        
        dialog.add(detailsPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton reserveButton = ThemeManager.createPrimaryButton("Make Reservation");
        JButton closeButton = ThemeManager.createSecondaryButton("Close");
        
        reserveButton.addActionListener(e -> {
            dialog.dispose();
            ReservationDialog resDialog = new ReservationDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                getCurrentUser()
            );
            resDialog.setVisible(true);
            
            if (resDialog.isReservationCreated()) {
                refreshData();
            }
        });
        
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(reserveButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setMinimumSize(new Dimension(400, 300));
        dialog.setVisible(true);
    }
    
    private void handleViewRoomDetails() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a room to view details",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int roomId = (int) roomsTable.getValueAt(
            roomsTable.convertRowIndexToModel(selectedRow), 0);
        
        Room room = roomController.getRoomById(roomId);
        if (room != null) {
            handleViewRoomDetails(room);
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