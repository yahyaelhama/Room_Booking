package com.roombooking.view;

import com.roombooking.controller.AuthController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Registration form for new users
 */
public class RegisterForm extends BaseForm {
    private final AuthController authController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JProgressBar passwordStrengthBar;
    private JLabel passwordStrengthLabel;
    
    public RegisterForm() {
        super("Application de Réservation des Salles - Register");
        this.authController = new AuthController();
        initializeComponents();
        setupKeyListeners();
    }
    
    private void initializeComponents() {
        // Main panel
        JPanel mainPanel = createStyledPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel("Create New Account");
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = createStyledPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = createStyledLabel("Username:");
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = createStyledTextField();
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = createStyledLabel("Password:");
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = createStyledPasswordField();
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
        });
        formPanel.add(passwordField, gbc);
        
        // Password strength indicator
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel strengthPanel = createStyledPanel();
        strengthPanel.setLayout(new BorderLayout(5, 0));
        
        passwordStrengthLabel = createStyledLabel("Password Strength: ");
        strengthPanel.add(passwordStrengthLabel, BorderLayout.WEST);
        
        passwordStrengthBar = new JProgressBar(0, 100);
        passwordStrengthBar.setStringPainted(true);
        passwordStrengthBar.setPreferredSize(new Dimension(200, 20));
        strengthPanel.add(passwordStrengthBar, BorderLayout.CENTER);
        
        formPanel.add(strengthPanel, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel confirmLabel = createStyledLabel("Confirm Password:");
        formPanel.add(confirmLabel, gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = createStyledPasswordField();
        formPanel.add(confirmPasswordField, gbc);
        
        // Password requirements
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JTextArea requirementsArea = new JTextArea(
            "Password requirements:\n" +
            "• At least 8 characters long\n" +
            "• Contains at least one uppercase letter\n" +
            "• Contains at least one lowercase letter\n" +
            "• Contains at least one number\n" +
            "• Contains at least one special character"
        );
        requirementsArea.setEditable(false);
        requirementsArea.setBackground(BG_COLOR);
        requirementsArea.setFont(LABEL_FONT);
        requirementsArea.setForeground(TEXT_COLOR);
        formPanel.add(requirementsArea, gbc);
        
        // Buttons panel
        JPanel buttonsPanel = createStyledPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        registerButton = createStyledButton("Register");
        registerButton.addActionListener(e -> handleRegister());
        
        JButton backButton = createStyledButton("Back to Login");
        backButton.addActionListener(e -> backToLogin());
        
        buttonsPanel.add(registerButton);
        buttonsPanel.add(backButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonsPanel, gbc);
        
        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = createStyledPanel();
        footerPanel.setBackground(PRIMARY_COLOR);
        JLabel footerLabel = createStyledLabel("© 2024 Room Booking System. All rights reserved.");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Set minimum size
        setMinimumSize(new Dimension(600, 500));
    }
    
    private void setupKeyListeners() {
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.getSource() == usernameField) {
                        passwordField.requestFocus();
                    } else if (e.getSource() == passwordField) {
                        confirmPasswordField.requestFocus();
                    } else if (e.getSource() == confirmPasswordField) {
                        handleRegister();
                    }
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);
    }
    
    private void updatePasswordStrength() {
        String password = new String(passwordField.getPassword());
        int strength = calculatePasswordStrength(password);
        
        passwordStrengthBar.setValue(strength);
        if (strength < 40) {
            passwordStrengthBar.setForeground(ACCENT_COLOR);
            passwordStrengthLabel.setText("Password Strength: Weak");
        } else if (strength < 70) {
            passwordStrengthBar.setForeground(Color.ORANGE);
            passwordStrengthLabel.setText("Password Strength: Medium");
        } else {
            passwordStrengthBar.setForeground(new Color(46, 204, 113));
            passwordStrengthLabel.setText("Password Strength: Strong");
        }
    }
    
    private int calculatePasswordStrength(String password) {
        int strength = 0;
        
        if (password.length() >= 8) strength += 20;
        if (password.length() >= 12) strength += 10;
        if (password.matches(".*[A-Z].*")) strength += 20;
        if (password.matches(".*[a-z].*")) strength += 20;
        if (password.matches(".*[0-9].*")) strength += 20;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength += 20;
        
        return Math.min(strength, 100);
    }
    
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Disable register button to prevent double submission
        registerButton.setEnabled(false);
        
        try {
            // Validation
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showError("Please fill in all fields.");
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                showError("Passwords do not match.");
                passwordField.setText("");
                confirmPasswordField.setText("");
                return;
            }
            
            if (calculatePasswordStrength(password) < 60) {
                showError("Password is too weak. Please follow the password requirements.");
                return;
            }
            
            // Show loading cursor
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            // Register user
            if (authController.register(username, password, false)) {
                showInfo("Registration successful! Please login.");
                backToLogin();
            } else {
                showError("Username already exists or registration failed.");
                usernameField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
            }
        } finally {
            registerButton.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void backToLogin() {
        new LoginForm().setVisible(true);
        dispose();
    }
} 