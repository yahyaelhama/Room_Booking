package com.roombooking.view;

import com.roombooking.controller.AuthController;
import com.roombooking.model.User;
import com.roombooking.util.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final AuthController authController;

    public LoginPanel() {
        authController = new AuthController();
        
        // Set panel properties
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.BACKGROUND_COLOR);
        
        // Create the login card
        JPanel loginCard = ThemeManager.createCardPanel();
        loginCard.setLayout(new BorderLayout(0, 20));
        loginCard.setMaximumSize(new Dimension(400, 450));
        
        // Create header panel with logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Add logo if available
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logo.png"));
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            headerPanel.add(logoLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            // If no logo, just use text
            JLabel titleLabel = new JLabel("Room Booking System");
            titleLabel.setFont(ThemeManager.TITLE_FONT);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            headerPanel.add(titleLabel, BorderLayout.NORTH);
        }
        
        // Add welcome text
        JLabel welcomeLabel = new JLabel("Welcome! Please sign in");
        welcomeLabel.setFont(ThemeManager.SUBHEADING_FONT);
        welcomeLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(welcomeLabel, BorderLayout.SOUTH);
        
        loginCard.add(headerPanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Username field
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(0, 35));
        JPanel usernamePanel = ThemeManager.createFormField("Username", usernameField);
        usernamePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        formPanel.add(usernamePanel);
        
        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(0, 35));
        JPanel passwordPanel = ThemeManager.createFormField("Password", passwordField);
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        formPanel.add(passwordPanel);
        
        // Login button
        loginButton = ThemeManager.createPrimaryButton("Sign In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        formPanel.add(loginButton);
        
        // Add spacing
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Register button
        JButton registerButton = ThemeManager.createSecondaryButton("Create New Account");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        formPanel.add(registerButton);
        
        loginCard.add(formPanel, BorderLayout.CENTER);
        
        // Footer with version info
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setFont(ThemeManager.SMALL_FONT);
        versionLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        footerPanel.add(versionLabel);
        
        loginCard.add(footerPanel, BorderLayout.SOUTH);
        
        // Add the card to the main panel
        add(loginCard);
        
        // Add action listeners
        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(e -> handleRegister());
        
        // Add key listener for Enter key
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "login");
        actionMap.put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin(e);
            }
        });
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loginButton.setEnabled(false);
        MainFrame.getInstance().showProgress(true);
        MainFrame.getInstance().setStatus("Logging in...");

        try {
            User user = authController.login(username, password);
            if (user != null) {
                if (!user.isActive()) {
                    JOptionPane.showMessageDialog(this,
                        "Your account has been deactivated. Please contact an administrator.",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (user.isAdmin()) {
                    MainFrame.getInstance().showPanel("admin", new AdminDashboard(user));
                } else {
                    MainFrame.getInstance().showPanel("user", new UserDashboard(user));
                }
                MainFrame.getInstance().setStatus("Logged in as " + user.getUsername());
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
                MainFrame.getInstance().setStatus("Login failed");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "An error occurred during login: " + ex.getMessage(),
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            MainFrame.getInstance().setStatus("Error: " + ex.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
            loginButton.setEnabled(true);
            passwordField.setText("");
            MainFrame.getInstance().showProgress(false);
        }
    }

    private void handleRegister() {
        RegistrationDialog dialog = new RegistrationDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        // If registration was successful, pre-fill the username
        if (dialog.isRegistrationSuccessful()) {
            usernameField.setText(dialog.getRegisteredUsername());
            passwordField.requestFocus();
            MainFrame.getInstance().setStatus("Registration successful. Please sign in.");
        }
    }
} 