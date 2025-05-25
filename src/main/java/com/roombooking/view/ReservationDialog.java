package com.roombooking.view;

import com.roombooking.controller.EquipmentController;
import com.roombooking.controller.ReservationController;
import com.roombooking.controller.RoomController;
import com.roombooking.model.Equipment;
import com.roombooking.model.Room;
import com.roombooking.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDialog extends JDialog {
    private final User user;
    private final ReservationController reservationController;
    private final RoomController roomController;
    private final EquipmentController equipmentController;
    private JComboBox<Room> roomComboBox;
    private JSpinner dateSpinner;
    private JSpinner startTimeSpinner;
    private JSpinner durationSpinner;
    private JTextField subjectField;
    private JList<Equipment> equipmentList;
    private boolean reservationCreated = false;

    public ReservationDialog(Frame parent, User user) {
        super(parent, "New Reservation", true);
        this.user = user;
        this.reservationController = new ReservationController();
        this.roomController = new RoomController();
        this.equipmentController = new EquipmentController();
        
        initializeComponents();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Room selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Room:"), gbc);

        gbc.gridx = 1;
        List<Room> availableRooms = roomController.getAvailableRooms();
        roomComboBox = new JComboBox<>(availableRooms.toArray(new Room[0]));
        formPanel.add(roomComboBox, gbc);

        // Date selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Date:"), gbc);

        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        formPanel.add(dateSpinner, gbc);

        // Start time selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Start Time:"), gbc);

        gbc.gridx = 1;
        SpinnerDateModel timeModel = new SpinnerDateModel();
        startTimeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(timeEditor);
        formPanel.add(startTimeSpinner, gbc);

        // Duration selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Duration (hours):"), gbc);

        gbc.gridx = 1;
        SpinnerNumberModel durationModel = new SpinnerNumberModel(1, 1, 8, 1);
        durationSpinner = new JSpinner(durationModel);
        formPanel.add(durationSpinner, gbc);

        // Subject field
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Subject:"), gbc);

        gbc.gridx = 1;
        subjectField = new JTextField(20);
        formPanel.add(subjectField, gbc);

        // Equipment selection
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Equipment:"), gbc);

        gbc.gridx = 1;
        gbc.gridheight = 2;
        List<Equipment> availableEquipment = equipmentController.getAvailableEquipment();
        equipmentList = new JList<>(availableEquipment.toArray(new Equipment[0]));
        equipmentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane equipmentScrollPane = new JScrollPane(equipmentList);
        equipmentScrollPane.setPreferredSize(new Dimension(200, 100));
        formPanel.add(equipmentScrollPane, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.addActionListener(e -> handleCreateReservation());
        cancelButton.addActionListener(e -> dispose());

        buttonsPanel.add(createButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void handleCreateReservation() {
        Room selectedRoom = (Room) roomComboBox.getSelectedItem();
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a room",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String subject = subjectField.getText().trim();
        if (subject.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a subject",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get selected equipment
        List<Equipment> selectedEquipment = equipmentList.getSelectedValuesList();

        // Get date and time components
        java.util.Date dateValue = (java.util.Date) dateSpinner.getValue();
        java.util.Date timeValue = (java.util.Date) startTimeSpinner.getValue();
        LocalDate date = LocalDate.of(
            dateValue.getYear() + 1900,
            dateValue.getMonth() + 1,
            dateValue.getDate()
        );
        LocalTime time = LocalTime.of(
            timeValue.getHours(),
            timeValue.getMinutes()
        );
        
        LocalDateTime startTime = LocalDateTime.of(date, time);
        int duration = (Integer) durationSpinner.getValue();

        // Validate that the start time is in the future
        if (startTime.isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this,
                "Start time must be in the future",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check room and equipment availability
        if (!reservationController.isRoomAvailable(selectedRoom.getId(), startTime, startTime.plusHours(duration))) {
            JOptionPane.showMessageDialog(this,
                "Room is not available for the selected time period",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check equipment availability
        List<Integer> equipmentIds = new ArrayList<>();
        for (Equipment equipment : selectedEquipment) {
            if (!equipmentController.isEquipmentAvailable(equipment.getId(), startTime, startTime.plusHours(duration))) {
                JOptionPane.showMessageDialog(this,
                    "Equipment '" + equipment.getName() + "' is not available for the selected time period",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            equipmentIds.add(equipment.getId());
        }

        // Create the reservation
        try {
            boolean success = reservationController.createReservation(
                user.getId(),
                selectedRoom.getId(),
                startTime,
                duration,
                subject,
                equipmentIds
            );

            if (success) {
                reservationCreated = true;
                JOptionPane.showMessageDialog(this,
                    "Reservation created successfully. Waiting for admin approval.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to create reservation",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error creating reservation: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isReservationCreated() {
        return reservationCreated;
    }
} 