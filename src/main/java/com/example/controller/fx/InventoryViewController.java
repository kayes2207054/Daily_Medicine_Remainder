package com.example.controller.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class InventoryViewController {
    @FXML private TableView<?> inventoryTable;
    @FXML private TableColumn<?, ?> medicineCol;
    @FXML private TableColumn<?, ?> quantityCol;
    @FXML private TableColumn<?, ?> expiryCol;
    @FXML private Label status;

    @FXML
    public void initialize() {
        if (status != null) status.setText("Loaded Inventory view (FXML)");
    }

    @FXML private void handleAdd() { if (status != null) status.setText("Add inventory (TODO)"); }
    @FXML private void handleUpdate() { if (status != null) status.setText("Update inventory (TODO)"); }
    @FXML private void handleDelete() { if (status != null) status.setText("Delete inventory (TODO)"); }
}
