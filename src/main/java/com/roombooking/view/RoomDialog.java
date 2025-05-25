package com.roombooking.view;

import com.roombooking.controller.RoomController;
import com.roombooking.model.Room;
import com.roombooking.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for adding or editing rooms
 */
public class RoomDialog extends JDialog {
    private final RoomController roomController;
    private final Room existingRoom;
    private final boolean isEditMode;
    
    private JTextField nameField;
    private JTextField typeField;
    private JTextField locationField;
    private JSpinner capacitySpinner;
    private JTextArea descriptionArea;
    private JCheckBox activeCheckBox;
    
    private boolean roomSaved = false;
    
    /**
     * Constructor for adding a new room
     */
    public RoomDialog(Frame parent) {
        super(parent, "Add New Room", true);
        this.roomController = new RoomController();
        this.existingRoom = null;
        this.isEditMode = false;
        
        initializeComponents();
        setupLayout();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(400, 400));
    }
    
    /**
     * Constructor for editing an existing room
     */
    public RoomDialog(Frame parent, Room room) {
        super(parent, "Edit Room", true);
        this.roomController = new RoomController();
        this.existingRoom = room;
        this.isEditMode = true;
        
        initializeComponents();
        populateFields();
        setupLayout();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(400, 400));
    }
    
    private void initializeComponents() {
        nameField = new JTextField(20);
        typeField = new JTextField(20);
        locationField = new JTextField(20);
        
        SpinnerNumberModel capacityModel = new SpinnerNumberModel(1, 1, 100, 1);
        capacitySpinner = new JSpinner(capacityModel);
        
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setSelected(true);
    }
    
    private void populateFields() {
        if (existingRoom != null) {
            nameField.setText(existingRoom.getName());
            typeField.setText(existingRoom.getType());
            locationField.setText(existingRoom.getLocation());
            capacitySpinner.setValue(existingRoom.getCapacity());
            descriptionArea.setText(existingRoom.getDescription());
            activeCheckBox.setSelected(existingRoom.isActive());
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Room name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Room Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        
        // Room type
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(typeField, gbc);
        
        // Location
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Location:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(locationField, gbc);
        
        // Capacity
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Capacity:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(capacitySpinner, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridheight = 2;
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);
        
        // Active checkbox
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        formPanel.add(activeCheckBox, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = ThemeManager.createPrimaryButton(isEditMode ? "Update" : "Save");
        JButton cancelButton = ThemeManager.createSecondaryButton("Cancel");
        
        saveButton.addActionListener(e -> handleSaveRoom());
        cancelButton.addActionListener(e -> dispose());
        
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    private void handleSaveRoom() {
        // Validate input
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a room name",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String type = typeField.getText().trim();
        if (type.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a room type",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String location = locationField.getText().trim();
        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a location",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int capacity = (Integer) capacitySpinner.getValue();
        String description = descriptionArea.getText().trim();
        boolean isActive = activeCheckBox.isSelected();
        
        try {
            boolean success;
            
            if (isEditMode && existingRoom != null) {
                // Update existing room
                existingRoom.setName(name);
                existingRoom.setType(type);
                existingRoom.setLocation(location);
                existingRoom.setCapacity(capacity);
                existingRoom.setDescription(description);
                existingRoom.setActive(isActive);
                
                success = roomController.updateRoom(existingRoom);
            } else {
                // Create new room
                success = roomController.createRoom(name, capacity, type, location, description);
            }
            
            if (success) {
                roomSaved = true;
                JOptionPane.showMessageDialog(this,
                    isEditMode ? "Room updated successfully" : "Room created successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    isEditMode ? "Failed to update room" : "Failed to create room",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isRoomSaved() {
        return roomSaved;
    }
} 