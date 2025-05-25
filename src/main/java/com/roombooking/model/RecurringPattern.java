package com.roombooking.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecurringPattern {
    public enum RecurrenceType {
        DAILY,
        WEEKLY,
        MONTHLY,
        CUSTOM
    }
    
    private RecurrenceType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<DayOfWeek> daysOfWeek; // For weekly recurrence
    private int dayOfMonth;            // For monthly recurrence
    private int interval;              // Every X days/weeks/months
    
    public RecurringPattern() {
    }
    
    public RecurrenceType getType() {
        return type;
    }
    
    public void setType(RecurrenceType type) {
        this.type = type;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Set<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }
    
    public void setDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
    
    public int getDayOfMonth() {
        return dayOfMonth;
    }
    
    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }
    
    public int getInterval() {
        return interval;
    }
    
    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    public List<LocalDateTime> generateOccurrences(LocalDateTime baseTime) {
        List<LocalDateTime> occurrences = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            switch (type) {
                case DAILY:
                    occurrences.add(baseTime.withDayOfMonth(current.getDayOfMonth())
                        .withMonth(current.getMonthValue())
                        .withYear(current.getYear()));
                    current = current.plusDays(interval);
                    break;
                    
                case WEEKLY:
                    if (daysOfWeek.contains(current.getDayOfWeek())) {
                        occurrences.add(baseTime.withDayOfMonth(current.getDayOfMonth())
                            .withMonth(current.getMonthValue())
                            .withYear(current.getYear()));
                    }
                    current = current.plusDays(1);
                    break;
                    
                case MONTHLY:
                    LocalDate adjustedDate = current.withDayOfMonth(1)
                        .plusMonths(interval)
                        .with(TemporalAdjusters.lastDayOfMonth());
                    
                    int targetDay = Math.min(dayOfMonth, adjustedDate.getDayOfMonth());
                    current = adjustedDate.withDayOfMonth(targetDay);
                    
                    occurrences.add(baseTime.withDayOfMonth(current.getDayOfMonth())
                        .withMonth(current.getMonthValue())
                        .withYear(current.getYear()));
                    break;
                    
                case CUSTOM:
                    if (daysOfWeek.contains(current.getDayOfWeek())) {
                        occurrences.add(baseTime.withDayOfMonth(current.getDayOfMonth())
                            .withMonth(current.getMonthValue())
                            .withYear(current.getYear()));
                    }
                    current = current.plusDays(interval);
                    break;
            }
        }
        
        return occurrences;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recurs ");
        
        switch (type) {
            case DAILY:
                sb.append("daily");
                if (interval > 1) {
                    sb.append(" every ").append(interval).append(" days");
                }
                break;
                
            case WEEKLY:
                sb.append("weekly on ");
                daysOfWeek.forEach(day -> sb.append(day).append(", "));
                if (interval > 1) {
                    sb.append(" every ").append(interval).append(" weeks");
                }
                break;
                
            case MONTHLY:
                sb.append("monthly on day ").append(dayOfMonth);
                if (interval > 1) {
                    sb.append(" every ").append(interval).append(" months");
                }
                break;
                
            case CUSTOM:
                sb.append("custom pattern every ").append(interval).append(" days on ");
                daysOfWeek.forEach(day -> sb.append(day).append(", "));
                break;
        }
        
        sb.append(" from ").append(startDate).append(" to ").append(endDate);
        return sb.toString();
    }
} 