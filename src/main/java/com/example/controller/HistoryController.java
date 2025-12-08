package com.example.controller;

import com.example.model.DoseHistory;
import java.util.ArrayList;

public class HistoryController {
    private ArrayList<DoseHistory> historyList;

    public HistoryController() {
        this.historyList = new ArrayList<>();
    }

    public void add(DoseHistory history) {
        // TODO: Implement add logic
    }

    public void delete(String date) {
        // TODO: Implement delete logic
    }

    public void edit(String date, DoseHistory updatedHistory) {
        // TODO: Implement edit logic
    }

    public ArrayList<DoseHistory> getAll() {
        return historyList;
    }
}
