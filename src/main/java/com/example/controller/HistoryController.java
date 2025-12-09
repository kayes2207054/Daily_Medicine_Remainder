package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.DoseHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HistoryController Class
 * Manages dose history tracking and adherence reporting.
 * Provides analytics on medication compliance.
 */
public class HistoryController {
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
    private DatabaseManager dbManager;
    private List<DoseHistory> historyList;

    public HistoryController() {
        this.dbManager = DatabaseManager.getInstance();
        this.historyList = new ArrayList<>();
        loadHistory();
    }

    /**
     * Load all dose history from database
     */
    public void loadHistory() {
        this.historyList = dbManager.getAllDoseHistory();
        logger.info("Loaded " + historyList.size() + " history records from database");
    }

    /**
     * Add dose history record
     */
    public int addHistory(DoseHistory history) {
        if (history == null || history.getMedicineName() == null) {
            logger.warn("Cannot add invalid history record");
            return -1;
        }

        int id = dbManager.addDoseHistory(history);
        if (id > 0) {
            history.setId(id);
            historyList.add(history);
            logger.info("History record added for: " + history.getMedicineName());
        }
        return id;
    }

    /**
     * Update dose history
     */
    public boolean updateHistory(DoseHistory history) {
        if (history == null || history.getId() <= 0) {
            logger.warn("Invalid history record for update");
            return false;
        }

        boolean success = dbManager.updateDoseHistory(history);
        if (success) {
            for (int i = 0; i < historyList.size(); i++) {
                if (historyList.get(i).getId() == history.getId()) {
                    historyList.set(i, history);
                    break;
                }
            }
            logger.info("History record updated");
        }
        return success;
    }

    /**
     * Delete history record by ID
     */
    public boolean deleteHistory(int historyId) {
        boolean success = dbManager.deleteDoseHistory(historyId);
        if (success) {
            historyList.removeIf(h -> h.getId() == historyId);
            logger.info("History record deleted with ID: " + historyId);
        }
        return success;
    }

    /**
     * Get all history records
     */
    public List<DoseHistory> getAllHistory() {
        return new ArrayList<>(historyList);
    }

    /**
     * Get history for specific medicine
     */
    public List<DoseHistory> getHistoryByMedicineId(int medicineId) {
        return dbManager.getHistoryByMedicineId(medicineId);
    }

    /**
     * Get history for specific date
     */
    public List<DoseHistory> getHistoryByDate(LocalDate date) {
        return historyList.stream()
                .filter(h -> h.getDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Get history for date range
     */
    public List<DoseHistory> getHistoryByDateRange(LocalDate startDate, LocalDate endDate) {
        return dbManager.getHistoryByDateRange(startDate, endDate);
    }

    /**
     * Get history by status (Taken, Missed, Pending)
     */
    public List<DoseHistory> getHistoryByStatus(String status) {
        return historyList.stream()
                .filter(h -> h.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    /**
     * Calculate adherence percentage for medicine in date range
     */
    public double getAdherencePercentage(int medicineId, LocalDate startDate, LocalDate endDate) {
        return dbManager.getAdherencePercentage(medicineId, startDate, endDate);
    }

    /**
     * Get today's history
     */
    public List<DoseHistory> getTodayHistory() {
        return getHistoryByDate(LocalDate.now());
    }

    /**
     * Get this week's history
     */
    public List<DoseHistory> getThisWeekHistory() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        return getHistoryByDateRange(weekAgo, today);
    }

    /**
     * Get this month's history
     */
    public List<DoseHistory> getThisMonthHistory() {
        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusMonths(1);
        return getHistoryByDateRange(monthAgo, today);
    }

    /**
     * Count doses taken today
     */
    public long getTakenTodayCount() {
        return getTodayHistory().stream()
                .filter(h -> DoseHistory.STATUS_TAKEN.equals(h.getStatus()))
                .count();
    }

    /**
     * Count doses missed today
     */
    public long getMissedTodayCount() {
        return getTodayHistory().stream()
                .filter(h -> DoseHistory.STATUS_MISSED.equals(h.getStatus()))
                .count();
    }

    /**
     * Count pending doses today
     */
    public long getPendingTodayCount() {
        return getTodayHistory().stream()
                .filter(h -> DoseHistory.STATUS_PENDING.equals(h.getStatus()))
                .count();
    }

    /**
     * Get doses grouped by medicine for a date
     */
    public Map<String, List<DoseHistory>> getGroupedByMedicine(LocalDate date) {
        return getHistoryByDate(date).stream()
                .collect(Collectors.groupingBy(DoseHistory::getMedicineName));
    }

    /**
     * Get daily statistics for a date range
     */
    public Map<LocalDate, Map<String, Long>> getDailyStatistics(LocalDate startDate, LocalDate endDate) {
        return getHistoryByDateRange(startDate, endDate).stream()
                .collect(Collectors.groupingBy(
                        DoseHistory::getDate,
                        Collectors.groupingBy(DoseHistory::getStatus, Collectors.counting())
                ));
    }

    /**
     * Mark a dose as taken
     */
    public boolean markDoseAsTaken(int historyId) {
        DoseHistory history = historyList.stream()
                .filter(h -> h.getId() == historyId)
                .findFirst()
                .orElse(null);
        
        if (history != null) {
            history.setStatus(DoseHistory.STATUS_TAKEN);
            return updateHistory(history);
        }
        return false;
    }

    /**
     * Mark a dose as missed
     */
    public boolean markDoseAsMissed(int historyId) {
        DoseHistory history = historyList.stream()
                .filter(h -> h.getId() == historyId)
                .findFirst()
                .orElse(null);
        
        if (history != null) {
            history.setStatus(DoseHistory.STATUS_MISSED);
            return updateHistory(history);
        }
        return false;
    }

    /**
     * Mark a dose as pending
     */
    public boolean markDoseAsPending(int historyId) {
        DoseHistory history = historyList.stream()
                .filter(h -> h.getId() == historyId)
                .findFirst()
                .orElse(null);
        
        if (history != null) {
            history.setStatus(DoseHistory.STATUS_PENDING);
            return updateHistory(history);
        }
        return false;
    }

    /**
     * Refresh history from database
     */
    public void refresh() {
        loadHistory();
        logger.info("History list refreshed");
    }
}
