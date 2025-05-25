package com.roombooking.view;

import com.roombooking.controller.ReservationController;
import com.roombooking.model.Reservation;
import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarPanel extends JPanel {
    private static final Color HEADER_COLOR = new Color(41, 128, 185);
    private static final Color TODAY_COLOR = new Color(52, 152, 219);
    private static final Color WEEKEND_COLOR = new Color(236, 240, 241);
    private static final Color RESERVATION_COLOR = new Color(46, 204, 113, 128);
    
    private final ReservationController reservationController;
    private LocalDate currentDate;
    private JLabel monthLabel;
    private JPanel calendarGrid;
    private List<Reservation> monthReservations;
    
    public CalendarPanel(ReservationController controller) {
        this.reservationController = controller;
        this.currentDate = LocalDate.now();
        initializeComponents();
        updateCalendar();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Navigation Panel
        JPanel navigationPanel = new JPanel(new FlowLayout());
        JButton prevButton = new JButton("←");
        JButton nextButton = new JButton("→");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });
        
        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });
        
        navigationPanel.add(prevButton);
        navigationPanel.add(monthLabel);
        navigationPanel.add(nextButton);
        add(navigationPanel, BorderLayout.NORTH);
        
        // Calendar Grid
        calendarGrid = new JPanel(new GridLayout(0, 7, 2, 2));
        add(new JScrollPane(calendarGrid), BorderLayout.CENTER);
        
        // Legend Panel
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel reservationLabel = new JLabel("Reservations");
        reservationLabel.setOpaque(true);
        reservationLabel.setBackground(RESERVATION_COLOR);
        reservationLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        JLabel todayLabel = new JLabel("Today");
        todayLabel.setOpaque(true);
        todayLabel.setBackground(TODAY_COLOR);
        todayLabel.setForeground(Color.WHITE);
        todayLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        legendPanel.add(reservationLabel);
        legendPanel.add(Box.createHorizontalStrut(10));
        legendPanel.add(todayLabel);
        add(legendPanel, BorderLayout.SOUTH);
    }
    
    private void updateCalendar() {
        monthLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        calendarGrid.removeAll();
        
        // Add day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel label = new JLabel(dayName, SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setBackground(HEADER_COLOR);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            calendarGrid.add(label);
        }
        
        // Get reservations for the month
        LocalDateTime monthStart = currentDate.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = currentDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
        monthReservations = reservationController.getReservationsInRange(monthStart, monthEnd);
        
        // Group reservations by date
        Map<LocalDate, List<Reservation>> reservationsByDate = monthReservations.stream()
            .collect(Collectors.groupingBy(r -> r.getStartTime().toLocalDate()));
        
        // Add calendar days
        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        
        // Add empty cells before first day
        for (int i = 0; i < dayOfWeek; i++) {
            calendarGrid.add(new JPanel());
        }
        
        // Add days with reservations
        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            LocalDate date = yearMonth.atDay(i);
            DayPanel dayPanel = new DayPanel(date, reservationsByDate.get(date));
            
            if (date.equals(LocalDate.now())) {
                dayPanel.setBackground(TODAY_COLOR);
                dayPanel.setForeground(Color.WHITE);
            } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY || 
                      date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                dayPanel.setBackground(WEEKEND_COLOR);
            }
            
            calendarGrid.add(dayPanel);
        }
        
        revalidate();
        repaint();
    }
    
    private class DayPanel extends JPanel {
        public DayPanel(LocalDate date, List<Reservation> reservations) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            setPreferredSize(new Dimension(100, 80));
            
            // Day number
            JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
            dayLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            add(dayLabel, BorderLayout.NORTH);
            
            // Reservations
            if (reservations != null && !reservations.isEmpty()) {
                JPanel reservationsPanel = new JPanel();
                reservationsPanel.setLayout(new BoxLayout(reservationsPanel, BoxLayout.Y_AXIS));
                reservationsPanel.setBackground(RESERVATION_COLOR);
                
                for (Reservation reservation : reservations) {
                    String time = reservation.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                    JLabel resLabel = new JLabel(time + " - " + reservation.getSubject());
                    resLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    reservationsPanel.add(resLabel);
                }
                
                add(reservationsPanel, BorderLayout.CENTER);
            }
            
            // Mouse listener for showing details
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (reservations != null && !reservations.isEmpty()) {
                        showDayDetails(date, reservations);
                    }
                }
            });
        }
    }
    
    private void showDayDetails(LocalDate date, List<Reservation> reservations) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Reservations for " + date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
            true);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (Reservation reservation : reservations) {
            JPanel reservationPanel = new JPanel(new BorderLayout());
            reservationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));
            
            String timeRange = reservation.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                " - " + reservation.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            
            JLabel timeLabel = new JLabel(timeRange);
            JLabel subjectLabel = new JLabel(reservation.getSubject());
            JLabel roomLabel = new JLabel(reservation.getRoomName());
            
            JPanel detailsPanel = new JPanel(new GridLayout(3, 1));
            detailsPanel.add(timeLabel);
            detailsPanel.add(subjectLabel);
            detailsPanel.add(roomLabel);
            
            reservationPanel.add(detailsPanel, BorderLayout.CENTER);
            contentPanel.add(reservationPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        dialog.add(scrollPane);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
} 