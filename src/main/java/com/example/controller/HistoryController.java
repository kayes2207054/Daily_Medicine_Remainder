package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.DoseHistory;
import com.example.utils.DataChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HistoryController {
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
    private DatabaseManager dbManager;
    private List<DoseHistory> historyList;
    private List<DataChangeListener> listeners = new ArrayList<>();

    public HistoryController() {
        this.dbManager = DatabaseManager.getInstance();
        this.historyList = new ArrayList<>();
        loadHistory();
    }

    public void loadHistory() {
        this.historyList = dbManager.getAllDoseHistory();
    }

    public int addHistory(DoseHistory history) {
        int id = dbManager.addDoseHistory(history);
        if (id > 0) {
            history.setId(id);
            historyList.add(0, history); // Add to top
            notifyDataChanged();
        }
        return id;
    }

    public List<DoseHistory> getHistoryList() {
        return new ArrayList<>(historyList);
    }
    
    public void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
    }
    
    private void notifyDataChanged() {
        for (DataChangeListener l : listeners) l.onHistoryDataChanged();
    }
    
    public long getTakenTodayCount() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return historyList.stream()
            .filter(h -> h.getScheduledTime() != null && h.getScheduledTime().toLocalDate().equals(today))
            .filter(h -> com.example.model.DoseHistory.STATUS_TAKEN.equals(h.getStatus()))
            .count();
    }
    
    public long getMissedTodayCount() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return historyList.stream()
            .filter(h -> h.getScheduledTime() != null && h.getScheduledTime().toLocalDate().equals(today))
            .filter(h -> com.example.model.DoseHistory.STATUS_MISSED.equals(h.getStatus()))
            .count();
    }
    
    public List<DoseHistory> getRecentHistory(int limit) {
        return historyList.stream().limit(limit).collect(java.util.stream.Collectors.toList());
    }
}
