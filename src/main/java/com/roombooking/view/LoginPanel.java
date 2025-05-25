package com.roombooking.view;

import com.roombooking.controller.AuthController;
import com.roombooking.model.User;
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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Système de Réservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(titleLabel, gbc);

        // Reset insets for other components
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordField, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(200, 35));
        buttonsPanel.add(loginButton);

        // Register button
        JButton registerButton = new JButton("Register New Account");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registerButton.setBackground(new Color(240, 240, 240));
        registerButton.setForeground(new Color(0, 120, 215));
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(200, 35));
        buttonsPanel.add(registerButton);

        // Add buttons panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 5, 5);
        add(buttonsPanel, gbc);

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
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "An error occurred during login: " + ex.getMessage(),
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
            loginButton.setEnabled(true);
            passwordField.setText("");
        }
    }

    private void handleRegister() {
        RegistrationDialog dialog = new RegistrationDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        // If registration was successful, pre-fill the username
        if (dialog.isRegistrationSuccessful()) {
            usernameField.setText(dialog.getRegisteredUsername());
            passwordField.requestFocus();
        }
    }
} 