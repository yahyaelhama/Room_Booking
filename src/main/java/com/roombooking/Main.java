package com.roombooking;

import com.roombooking.view.LoginPanel;
import com.roombooking.view.MainFrame;
import javax.swing.*;
import java.awt.*;

/**
 * Main class to launch the Room Booking Application
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize custom colors and fonts
        initializeUIDefaults();

        // Launch the application
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = MainFrame.getInstance();
            mainFrame.showPanel("login", new LoginPanel());
            mainFrame.setVisible(true);
        });
    }

    private static void initializeUIDefaults() {
        // Set default font
        Font defaultFont = new Font("Arial", Font.PLAIN, 12);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("PasswordField.font", defaultFont);
        
        // Set default colors
        Color primaryColor = new Color(0, 120, 215);
        Color backgroundColor = new Color(240, 240, 240);
        
        UIManager.put("Panel.background", backgroundColor);
        UIManager.put("Button.background", primaryColor);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.select", primaryColor.darker());
    }
} 