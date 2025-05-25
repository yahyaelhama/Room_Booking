package com.roombooking;

import com.roombooking.util.ThemeManager;
import com.roombooking.view.LoginPanel;
import com.roombooking.view.MainFrame;
import javax.swing.*;
import java.awt.*;

/**
 * Main class to launch the Room Booking Application
 */
public class Main {
    public static void main(String[] args) {
        // Apply application theme
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply theme
                ThemeManager.applyTheme();
                
                // Launch the application
                MainFrame mainFrame = MainFrame.getInstance();
                mainFrame.showPanel("login", new LoginPanel());
                mainFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(), 
                    "Application Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
} 