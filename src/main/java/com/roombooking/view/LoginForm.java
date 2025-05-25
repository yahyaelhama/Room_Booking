package com.roombooking.view;

import com.roombooking.controller.AuthController;
import com.roombooking.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

/**
 * Login form for user authentication
 */
public class LoginForm extends BaseForm {
    private final AuthController authController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JCheckBox rememberMeCheckbox;
    
    public LoginForm() {
        super("Application de Réservation des Salles - Login");
        this.authController = new AuthController();
        
        initializeComponents();
        setupKeyListeners();
    }
    
    private void initializeComponents() {
        // Main panel with card layout for transition effects
        JPanel mainPanel = createStyledPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel("Welcome to Room Booking System");
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = createStyledPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Logo/Icon (only if available)
        try {
            ImageIcon logo = new ImageIcon(getClass().getResource("/images/logo.png"));
            if (logo.getImageLoadStatus() == MediaTracker.COMPLETE) {
                JLabel logoLabel = new JLabel(logo);
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                formPanel.add(logoLabel, gbc);
                gbc.gridwidth = 1;
            }
        } catch (Exception e) {
            // Logo not available, skip it
            System.out.println("Logo not found, continuing without it.");
        }
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel usernameLabel = createStyledLabel("Username:");
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = createStyledTextField();
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = createStyledLabel("Password:");
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = createStyledPasswordField();
        formPanel.add(passwordField, gbc);
        
        // Remember me checkbox
        gbc.gridx = 1;
        gbc.gridy = 3;
        rememberMeCheckbox = new JCheckBox("Remember me");
        rememberMeCheckbox.setFont(LABEL_FONT);
        rememberMeCheckbox.setBackground(BG_COLOR);
        formPanel.add(rememberMeCheckbox, gbc);
        
        // Buttons panel
        JPanel buttonsPanel = createStyledPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        
        registerButton = createStyledButton("Register");
        registerButton.addActionListener(e -> openRegisterForm());
        
        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
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
        setMinimumSize(new Dimension(600, 400));
    }
    
    private void setupKeyListeners() {
        // Add enter key listener to password field
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
        
        // Add enter key listener to username field
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }
        
        User user = authController.login(username, password);
        if (user != null) {
            if (!user.isActive()) {
                showError("Your account has been deactivated. Please contact an administrator.");
                return;
            }
            
            if (rememberMeCheckbox.isSelected()) {
                // TODO: Implement remember me functionality
            }
            
            if (user.isAdmin()) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new UserDashboard(user).setVisible(true);
            }
            dispose();
        } else {
            showError("Invalid username or password");
            passwordField.setText("");
        }
    }
    
    private void openRegisterForm() {
        new RegisterForm().setVisible(true);
        dispose();
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
} 