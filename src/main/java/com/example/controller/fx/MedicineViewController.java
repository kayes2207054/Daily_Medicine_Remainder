package com.example.controller.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MedicineViewController {

    @FXML private TableView<?> medicineTable;
    @FXML private TableColumn<?, ?> nameCol;
    @FXML private TableColumn<?, ?> dosageCol;
    @FXML private TableColumn<?, ?> scheduleCol;
    @FXML private Label status;

    @FXML
    public void initialize() {
        // TODO: Bind to MedicineController and populate table
        if (status != null) status.setText("Loaded Medicines view (FXML)");
    }

    @FXML
    private void handleAdd() {
        if (status != null) status.setText("Add medicine (TODO)");
    }

    @FXML
    private void handleEdit() {
        if (status != null) status.setText("Edit medicine (TODO)");
    }

    @FXML
    private void handleDelete() {
        if (status != null) status.setText("Delete medicine (TODO)");
    }
}
