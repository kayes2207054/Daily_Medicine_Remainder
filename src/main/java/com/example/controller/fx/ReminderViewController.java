package com.example.controller.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ReminderViewController {
    @FXML private TableView<?> reminderTable;
    @FXML private TableColumn<?, ?> medicineCol;
    @FXML private TableColumn<?, ?> timeCol;
    @FXML private TableColumn<?, ?> statusCol;
    @FXML private Label status;

    @FXML
    public void initialize() {
        if (status != null) status.setText("Loaded Reminders view (FXML)");
    }

    @FXML private void handleAdd() { if (status != null) status.setText("Add reminder (TODO)"); }
    @FXML private void handleDelete() { if (status != null) status.setText("Delete reminder (TODO)"); }
    @FXML private void handleMarkTaken() { if (status != null) status.setText("Mark taken (TODO)"); }
    @FXML private void handleMarkMissed() { if (status != null) status.setText("Mark missed (TODO)"); }
}
