package com.example.controller.fx;

// JavaFX + FXML Migration — Phase 2
// Skeleton controller for MainDashboard.fxml

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * MainDashboardController: Minimal navigation handlers.
 * TODO: Load child FXMLs into contentArea on button actions.
 * TODO: Add Live clock via JavaFX Timeline in later phase.
 */
public class MainDashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        // Load default view
        loadIntoContent("/fxml/HomeView.fxml");
        status("Home");
    }

    @FXML private void showHome()      { loadIntoContent("/fxml/HomeView.fxml"); status("Home"); }
    @FXML private void showMedicines() { loadIntoContent("/fxml/MedicineView.fxml"); status("Medicines"); }
    @FXML private void showReminders() { loadIntoContent("/fxml/ReminderView.fxml"); status("Reminders"); }
    @FXML private void showInventory() { loadIntoContent("/fxml/InventoryView.fxml"); status("Inventory"); }
    @FXML private void showHistory()   { loadIntoContent("/fxml/HistoryView.fxml"); status("History"); }

    @FXML private void showSettings()  { status("Settings"); }
    @FXML private void showHelp()      { status("Help"); }
    @FXML private void handleLogout()  { status("Logout"); }

    private void status(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    private void loadIntoContent(String resourcePath) {
        try {
            Node view = FXMLLoader.load(this.getClass().getResource(resourcePath));
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            // Minimal handling: show basic error text; improve later
            Label error = new Label("Failed to load view: " + resourcePath);
            contentArea.getChildren().setAll(error);
        }
    }
}
