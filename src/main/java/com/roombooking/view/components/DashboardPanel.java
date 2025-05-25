package com.roombooking.view.components;

import com.roombooking.model.User;
import com.roombooking.util.ThemeManager;
import com.roombooking.view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Reusable dashboard panel with sidebar navigation
 */
public class DashboardPanel extends JPanel {
    private final User currentUser;
    private final JPanel contentPanel;
    private final JPanel sidebarPanel;
    private final CardLayout contentLayout;
    private final JLabel userNameLabel;
    private final JLabel userRoleLabel;
    private JLabel timeLabel;
    private Timer timeUpdateTimer;
    
    public DashboardPanel(User user) {
        this.currentUser = user;
        
        // Set up the main layout
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BACKGROUND_COLOR);
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(ThemeManager.PRIMARY_DARK);
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        
        // User info panel in sidebar
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);
        userInfoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 80)));
        userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // User avatar
        JLabel avatarLabel = new JLabel();
        try {
            ImageIcon avatarIcon = new ImageIcon(getClass().getResource("/images/user_avatar.png"));
            Image scaledImage = avatarIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            avatarLabel.setText("👤");
            avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        }
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(avatarLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // User name
        userNameLabel = new JLabel(currentUser.getFullName());
        userNameLabel.setFont(ThemeManager.SUBHEADING_FONT);
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(userNameLabel);
        
        // User role
        userRoleLabel = new JLabel(currentUser.isAdmin() ? "Administrator" : "User");
        userRoleLabel.setFont(ThemeManager.SMALL_FONT);
        userRoleLabel.setForeground(new Color(255, 255, 255, 180));
        userRoleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(userRoleLabel);
        
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(userInfoPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Navigation menu will be added by subclasses
        
        // Add logout button at bottom of sidebar
        sidebarPanel.add(Box.createVerticalGlue());
        JButton logoutButton = new JButton("Logout");
        logoutButton.setIcon(createIcon("logout.png", "➡"));
        logoutButton.setFont(ThemeManager.LABEL_FONT);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setOpaque(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> handleLogout());
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(logoutButton);
        
        add(sidebarPanel, BorderLayout.WEST);
        
        // Create content panel with card layout
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(contentPanel, BorderLayout.CENTER);
        
        // Start time update timer
        startTimeUpdateTimer();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // App title
        JLabel titleLabel = new JLabel("Room Booking System");
        titleLabel.setFont(ThemeManager.HEADING_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Current time
        timeLabel = new JLabel();
        timeLabel.setFont(ThemeManager.LABEL_FONT);
        timeLabel.setForeground(Color.WHITE);
        updateTimeLabel();
        headerPanel.add(timeLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Adds a navigation item to the sidebar
     */
    protected JButton addNavigationItem(String title, String iconName, String cardName) {
        JButton navButton = new JButton(title);
        navButton.setFont(ThemeManager.LABEL_FONT);
        navButton.setForeground(Color.WHITE);
        navButton.setBorderPainted(false);
        navButton.setContentAreaFilled(true);
        navButton.setOpaque(false);
        navButton.setFocusPainted(false);
        navButton.setHorizontalAlignment(SwingConstants.LEFT);
        navButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        navButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        // Set icon if available
        navButton.setIcon(createIcon(iconName, "•"));
        
        // Add action to show the corresponding panel
        navButton.addActionListener(e -> {
            contentLayout.show(contentPanel, cardName);
        });
        
        sidebarPanel.add(navButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        return navButton;
    }
    
    /**
     * Adds a content panel to the card layout
     */
    protected void addContentPanel(JPanel panel, String name) {
        contentPanel.add(panel, name);
    }
    
    /**
     * Shows a specific content panel
     */
    protected void showContentPanel(String name) {
        contentLayout.show(contentPanel, name);
    }
    
    private Icon createIcon(String iconName, String fallbackText) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + iconName));
            return icon;
        } catch (Exception e) {
            // Return a text-based fallback icon
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("SansSerif", Font.BOLD, 16));
                    g.drawString(fallbackText, x + 5, y + 16);
                }
                
                @Override
                public int getIconWidth() {
                    return 24;
                }
                
                @Override
                public int getIconHeight() {
                    return 24;
                }
            };
        }
    }
    
    private void updateTimeLabel() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy • HH:mm");
        timeLabel.setText(now.format(formatter));
    }
    
    private void startTimeUpdateTimer() {
        timeUpdateTimer = new Timer(60000, e -> updateTimeLabel()); // Update every minute
        timeUpdateTimer.start();
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            cleanup();
            MainFrame.getInstance().showPanel("login", new com.roombooking.view.LoginPanel());
            MainFrame.getInstance().setStatus("Logged out successfully");
        }
    }
    
    /**
     * Clean up resources before closing
     */
    public void cleanup() {
        if (timeUpdateTimer != null) {
            timeUpdateTimer.stop();
        }
    }
    
    /**
     * Get the current user
     */
    protected User getCurrentUser() {
        return currentUser;
    }
} 