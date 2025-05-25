package com.roombooking.view;

import com.roombooking.controller.ReservationController;
import com.roombooking.controller.RoomController;
import com.roombooking.model.Room;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomUtilizationReport extends JDialog {
    private final ReservationController reservationController;
    private final RoomController roomController;
    private JTabbedPane tabbedPane;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    public RoomUtilizationReport(Frame parent) {
        super(parent, "Room Utilization Report", true);
        this.reservationController = new ReservationController();
        this.roomController = new RoomController();
        
        // Default to last 30 days
        this.endDate = LocalDateTime.now();
        this.startDate = endDate.minusDays(30);
        
        initializeComponents();
        setSize(800, 600);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Date range panel
        JPanel datePanel = new JPanel(new FlowLayout());
        JButton lastMonthBtn = new JButton("Last 30 Days");
        JButton last3MonthsBtn = new JButton("Last 3 Months");
        JButton lastYearBtn = new JButton("Last Year");
        
        lastMonthBtn.addActionListener(e -> {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
            updateReports();
        });
        
        last3MonthsBtn.addActionListener(e -> {
            endDate = LocalDateTime.now();
            startDate = endDate.minusMonths(3);
            updateReports();
        });
        
        lastYearBtn.addActionListener(e -> {
            endDate = LocalDateTime.now();
            startDate = endDate.minusYears(1);
            updateReports();
        });
        
        datePanel.add(lastMonthBtn);
        datePanel.add(last3MonthsBtn);
        datePanel.add(lastYearBtn);
        add(datePanel, BorderLayout.NORTH);
        
        // Tabbed pane for different charts
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);
        
        updateReports();
        
        // Export button
        JButton exportButton = new JButton("Export to Excel");
        exportButton.addActionListener(e -> exportReport());
        add(exportButton, BorderLayout.SOUTH);
    }
    
    private void updateReports() {
        tabbedPane.removeAll();
        
        // Room usage by hours chart
        JPanel usagePanel = createUsageByHoursChart();
        tabbedPane.addTab("Usage by Hours", usagePanel);
        
        // Room usage by reservations chart
        JPanel reservationsPanel = createUsageByReservationsChart();
        tabbedPane.addTab("Usage by Reservations", reservationsPanel);
        
        // Equipment usage pie chart
        JPanel equipmentPanel = createEquipmentUsageChart();
        tabbedPane.addTab("Equipment Usage", equipmentPanel);
        
        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        tabbedPane.addTab("Summary", summaryPanel);
        
        revalidate();
        repaint();
    }
    
    private JPanel createUsageByHoursChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Room> rooms = roomController.getAllRooms();
        
        for (Room room : rooms) {
            int hours = reservationController.calculateTotalHours(room.getId(), startDate, endDate);
            dataset.addValue(hours, "Hours Used", room.getName());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Room Usage by Hours",
            "Room",
            "Hours",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
    
    private JPanel createUsageByReservationsChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Room> rooms = roomController.getAllRooms();
        
        for (Room room : rooms) {
            int count = reservationController.countReservations(room.getId(), startDate, endDate);
            dataset.addValue(count, "Number of Reservations", room.getName());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Room Usage by Number of Reservations",
            "Room",
            "Reservations",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
    
    private JPanel createEquipmentUsageChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> equipmentUsage = reservationController.getEquipmentUsageStats(startDate, endDate);
        
        for (Map.Entry<String, Integer> entry : equipmentUsage.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Equipment Usage Distribution",
            dataset,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Statistics
        Map<String, Object> stats = calculateStatistics();
        
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='width: 300px'>");
        html.append("<h2>Utilization Summary</h2>");
        html.append("<p>Period: ").append(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .append(" to ").append(endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("</p>");
        html.append("<hr>");
        
        html.append("<h3>Overall Statistics</h3>");
        html.append("<ul>");
        html.append("<li>Total Reservations: ").append(stats.get("totalReservations")).append("</li>");
        html.append("<li>Total Hours Used: ").append(stats.get("totalHours")).append("</li>");
        html.append("<li>Average Duration: ").append(stats.get("avgDuration")).append(" hours</li>");
        html.append("<li>Most Used Room: ").append(stats.get("mostUsedRoom")).append("</li>");
        html.append("<li>Most Used Equipment: ").append(stats.get("mostUsedEquipment")).append("</li>");
        html.append("</ul>");
        
        html.append("<h3>Usage by Time of Day</h3>");
        html.append("<ul>");
        html.append("<li>Morning (8-12): ").append(stats.get("morningUsage")).append("%</li>");
        html.append("<li>Afternoon (12-17): ").append(stats.get("afternoonUsage")).append("%</li>");
        html.append("<li>Evening (17-22): ").append(stats.get("eveningUsage")).append("%</li>");
        html.append("</ul>");
        
        html.append("</body></html>");
        
        JLabel summaryLabel = new JLabel(html.toString());
        JScrollPane scrollPane = new JScrollPane(summaryLabel);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Map<String, Object> calculateStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // These would be calculated using the ReservationController
        stats.put("totalReservations", reservationController.countTotalReservations(startDate, endDate));
        stats.put("totalHours", reservationController.calculateTotalHoursAllRooms(startDate, endDate));
        stats.put("avgDuration", reservationController.calculateAverageDuration(startDate, endDate));
        stats.put("mostUsedRoom", reservationController.getMostUsedRoom(startDate, endDate));
        stats.put("mostUsedEquipment", reservationController.getMostUsedEquipment(startDate, endDate));
        stats.put("morningUsage", reservationController.calculateUsagePercentageByTimeSlot(startDate, endDate, 8, 12));
        stats.put("afternoonUsage", reservationController.calculateUsagePercentageByTimeSlot(startDate, endDate, 12, 17));
        stats.put("eveningUsage", reservationController.calculateUsagePercentageByTimeSlot(startDate, endDate, 17, 22));
        
        return stats;
    }
    
    private void exportReport() {
        try {
            String filename = "RoomUtilization_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            reservationController.exportUtilizationReport(startDate, endDate, filename);
            
            JOptionPane.showMessageDialog(this,
                "Report exported successfully to " + filename,
                "Export Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error exporting report: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 