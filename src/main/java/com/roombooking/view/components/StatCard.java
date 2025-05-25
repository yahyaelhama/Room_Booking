package com.roombooking.view.components;

import com.roombooking.util.ThemeManager;
import javax.swing.*;
import java.awt.*;

/**
 * A card component for displaying statistics in the dashboard
 */
public class StatCard extends JPanel {
    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel iconLabel;
    private final JPanel contentPanel;
    private Color cardColor = ThemeManager.CARD_COLOR;
    private Color accentColor = ThemeManager.PRIMARY_COLOR;
    
    /**
     * Creates a new stat card with the specified title and value
     */
    public StatCard(String title, String value, String iconName) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setBackground(cardColor);
        
        // Create icon
        iconLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + iconName));
            Image scaledImage = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Use text fallback
            iconLabel.setText("📊");
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        }
        iconLabel.setForeground(accentColor);
        iconLabel.setVerticalAlignment(SwingConstants.TOP);
        add(iconLabel, BorderLayout.WEST);
        
        // Create content panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // Title
        titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.LABEL_FONT);
        titleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Value
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(ThemeManager.HEADING_FONT.getName(), Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(valueLabel);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Sets the accent color of the card
     */
    public void setAccentColor(Color color) {
        this.accentColor = color;
        valueLabel.setForeground(color);
        iconLabel.setForeground(color);
        repaint();
    }
    
    /**
     * Updates the value displayed on the card
     */
    public void setValue(String value) {
        valueLabel.setText(value);
    }
    
    /**
     * Updates the title displayed on the card
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    /**
     * Adds a subtitle to the card
     */
    public void addSubtitle(String subtitle) {
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(ThemeManager.SMALL_FONT);
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(subtitleLabel);
    }
} 