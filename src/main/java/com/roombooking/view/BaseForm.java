package com.roombooking.view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Base form class that all forms will extend
 */
public abstract class BaseForm extends JFrame {
    protected static final int FORM_WIDTH = 1024;
    protected static final int FORM_HEIGHT = 768;
    protected static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    protected static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 20);
    protected static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    protected static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    protected static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    protected static final Color ACCENT_COLOR = new Color(231, 76, 60);
    protected static final Color BG_COLOR = new Color(236, 240, 241);
    protected static final Color TEXT_COLOR = new Color(44, 62, 80);
    protected static final int PADDING = 20;
    
    public BaseForm(String title) {
        setTitle(title);
        setSize(FORM_WIDTH, FORM_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(BG_COLOR);
        
        // Set the look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            customizeUIDefaults();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void customizeUIDefaults() {
        UIManager.put("Panel.background", BG_COLOR);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("Button.focusPainted", false);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("Table.selectionBackground", SECONDARY_COLOR);
        UIManager.put("Table.selectionForeground", Color.WHITE);
    }
    
    /**
     * Creates a styled button
     * @param text the button text
     * @return styled JButton
     */
    protected JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(SECONDARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(SECONDARY_COLOR);
                } else {
                    g2.setColor(PRIMARY_COLOR);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(LABEL_FONT);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        
        return button;
    }
    
    /**
     * Creates a styled label
     * @param text the label text
     * @return styled JLabel
     */
    protected JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    /**
     * Creates a styled text field
     * @return styled JTextField
     */
    protected JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(LABEL_FONT);
        textField.setBorder(createTextBorder());
        textField.setPreferredSize(new Dimension(200, 35));
        return textField;
    }
    
    /**
     * Creates a styled password field
     * @return styled JPasswordField
     */
    protected JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(LABEL_FONT);
        passwordField.setBorder(createTextBorder());
        passwordField.setPreferredSize(new Dimension(200, 35));
        return passwordField;
    }
    
    /**
     * Creates a styled panel with padding
     * @return styled JPanel
     */
    protected JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        return panel;
    }
    
    protected Border createTextBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }
    
    protected JComboBox createStyledComboBox() {
        JComboBox comboBox = new JComboBox();
        comboBox.setFont(LABEL_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(createTextBorder());
        comboBox.setPreferredSize(new Dimension(200, 35));
        return comboBox;
    }
    
    protected JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(LABEL_FONT);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(SECONDARY_COLOR.brighter());
        table.getTableHeader().setFont(LABEL_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        return table;
    }
    
    /**
     * Shows an error message dialog
     * @param message the error message
     */
    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an information message dialog
     * @param message the information message
     */
    protected void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows a confirmation dialog
     * @param message the confirmation message
     * @return true if user confirms, false otherwise
     */
    protected boolean showConfirm(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    protected JPanel createHeaderPanel(String title) {
        JPanel headerPanel = createStyledPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }
} 