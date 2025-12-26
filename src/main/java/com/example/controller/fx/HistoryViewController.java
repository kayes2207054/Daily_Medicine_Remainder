package com.example.controller.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistoryViewController {
    @FXML private TableView<?> historyTable;
    @FXML private TableColumn<?, ?> medicineCol;
    @FXML private TableColumn<?, ?> dateCol;
    @FXML private TableColumn<?, ?> statusCol;
    @FXML private Label status;

    @FXML
    public void initialize() {
        if (status != null) status.setText("Loaded History view (FXML)");
    }

    @FXML
    private void handleExportCSV() {
        if (status != null) status.setText("Export CSV (TODO)");
        // TODO: use FileUtils to export via JavaFX
    }
}
