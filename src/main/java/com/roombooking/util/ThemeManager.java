package com.roombooking.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Manages application-wide theme settings and styling
 */
public class ThemeManager {
    // Theme colors
    public static final Color PRIMARY_COLOR = new Color(25, 118, 210);     // Material Blue
    public static final Color PRIMARY_DARK = new Color(21, 101, 192);      // Darker Blue
    public static final Color ACCENT_COLOR = new Color(255, 152, 0);       // Orange
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);      // Green
    public static final Color ERROR_COLOR = new Color(244, 67, 54);        // Red
    public static final Color WARNING_COLOR = new Color(255, 193, 7);      // Amber
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light Gray
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);        // Dark Gray
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);   // Medium Gray

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // Borders and padding
    public static final int PADDING = 15;
    public static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING);
    public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
    );

    // Apply theme to components
    public static void applyTheme() {
        try {
            // Set system look and feel as base
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Customize UI elements
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Button.font", BUTTON_FONT);
            UIManager.put("Label.font", LABEL_FONT);
            UIManager.put("TextField.font", LABEL_FONT);
            UIManager.put("PasswordField.font", LABEL_FONT);
            UIManager.put("ComboBox.font", LABEL_FONT);
            UIManager.put("TabbedPane.font", LABEL_FONT);
            UIManager.put("Table.font", LABEL_FONT);
            UIManager.put("TableHeader.font", SUBHEADING_FONT);
            
            // Table styling
            UIManager.put("Table.gridColor", new Color(224, 224, 224));
            UIManager.put("Table.selectionBackground", PRIMARY_COLOR);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("TableHeader.background", new Color(240, 240, 240));
            
            // Tab styling
            UIManager.put("TabbedPane.selected", PRIMARY_COLOR);
            UIManager.put("TabbedPane.contentAreaColor", BACKGROUND_COLOR);
            
        } catch (Exception e) {
            System.err.println("Failed to apply theme: " + e.getMessage());
        }
    }
    
    /**
     * Creates a styled primary button
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true); // Enable content area filling
        button.setOpaque(true);
        return button;
    }
    
    /**
     * Creates a styled secondary button
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true); // Enable content area filling
        button.setOpaque(true);
        return button;
    }
    
    /**
     * Creates a styled danger button
     */
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(ERROR_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true); // Enable content area filling
        button.setOpaque(true);
        return button;
    }
    
    /**
     * Creates a styled success button
     */
    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(SUCCESS_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true); // Enable content area filling
        button.setOpaque(true);
        return button;
    }
    
    /**
     * Creates a styled card panel with shadow effect
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(CARD_BORDER);
        return panel;
    }
    
    /**
     * Creates a styled header label
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADING_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Creates a styled form field with label
     */
    public static JPanel createFormField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_SECONDARY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
} 