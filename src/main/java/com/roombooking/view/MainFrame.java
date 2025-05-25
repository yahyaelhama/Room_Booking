package com.roombooking.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static MainFrame instance;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private MainFrame() {
        setTitle("Système de Réservation des Salles et Équipements");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 768));
        
        // Initialize the card layout for switching between screens
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel);
        
        // Set application icon if available
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/app_icon.png"));
            if (icon.getImage() != null) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            // Ignore if icon not found
        }
        
        // Center the frame on screen
        pack();
        setLocationRelativeTo(null);
    }

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    public void showPanel(String name, JPanel panel) {
        contentPanel.add(panel, name);
        cardLayout.show(contentPanel, name);
        validate();
        repaint();
    }

    public void showCard(String name) {
        cardLayout.show(contentPanel, name);
    }
} 