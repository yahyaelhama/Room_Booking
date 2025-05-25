package com.roombooking.view;

import com.roombooking.util.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Main application frame that contains all panels
 */
public class MainFrame extends JFrame {
    private static MainFrame instance;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel statusBar;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    private MainFrame() {
        setTitle("Room Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));
        
        // Set application icon
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/app_icon.png"));
            if (icon.getImage() != null) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            // Ignore if icon not found
        }
        
        // Initialize the main layout
        setLayout(new BorderLayout());
        
        // Initialize the card layout for switching between screens
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        add(contentPanel, BorderLayout.CENTER);
        
        // Create status bar
        createStatusBar();
        
        // Add responsive behavior
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Adjust UI based on window size if needed
                revalidate();
            }
        });
        
        // Center the frame on screen
        pack();
        setLocationRelativeTo(null);
    }

    private void createStatusBar() {
        statusBar = new JPanel();
        statusBar.setLayout(new BorderLayout());
        statusBar.setPreferredSize(new Dimension(getWidth(), 25));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(ThemeManager.SMALL_FONT);
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(150, 15));
        progressBar.setVisible(false);
        statusBar.add(progressBar, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
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
    
    public void setStatus(String message) {
        statusLabel.setText(message);
    }
    
    public void showProgress(boolean visible) {
        progressBar.setVisible(visible);
        if (visible) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setIndeterminate(false);
        }
    }
    
    public void showProgress(int value, int max) {
        progressBar.setVisible(true);
        progressBar.setIndeterminate(false);
        progressBar.setMinimum(0);
        progressBar.setMaximum(max);
        progressBar.setValue(value);
    }
} 