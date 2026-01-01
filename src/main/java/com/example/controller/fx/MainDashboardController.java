package com.example.controller.fx;

// JavaFX + FXML Migration — Phase 2
// Dashboard controller wired to backend controllers (Swing-free)

import com.example.MainApp;
import com.example.controller.AuthController;
import com.example.controller.HistoryController;
import com.example.controller.InventoryController;
import com.example.controller.MedicineController;
import com.example.controller.ReminderController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MainDashboardController: Navigates between child views and shares backend controllers.
 */
public class MainDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(MainDashboardController.class);

    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;

    // Shared backend controllers (single instances reused across views)
    private final MedicineController medicineController = new MedicineController();
    private final InventoryController inventoryController = new InventoryController();
    private final HistoryController historyController = new HistoryController();
    private final ReminderController reminderController = new ReminderController();

    @FXML
    private void initialize() {
        // Register with MainApp for shutdown cleanup (stops alarm timer on window close)
        MainApp.getInstance().setDashboardController(this);

        // Wire cross-dependencies
        reminderController.setInventoryController(inventoryController);
        reminderController.setHistoryController(historyController);

        // Load default view
        loadIntoContent("/fxml/HomeView.fxml");
        status("Home");
    }

    @FXML private void showHome()      { loadIntoContent("/fxml/HomeView.fxml"); status("Home"); }
    @FXML private void showMedicines() { loadIntoContent("/fxml/MedicineView.fxml"); status("Medicines"); }
    @FXML private void showReminders() { loadIntoContent("/fxml/ReminderView.fxml"); status("Reminders"); }
    @FXML private void showInventory() { loadIntoContent("/fxml/InventoryView.fxml"); status("Inventory"); }
    @FXML private void showHistory()   { loadIntoContent("/fxml/HistoryView.fxml"); status("History"); }

    @FXML private void showSettings() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Application Settings");
        alert.setContentText(
            "Version: 1.0\n" +
            "Database: SQLite\n" +
            "User: " + (AuthController.getCurrentUser() != null ? AuthController.getCurrentUser().getUsername() : "N/A") + "\n\n" +
            "Alarm Check Interval: Every 1 second\n" +
            "Low Stock Threshold: Configurable per medicine\n" +
            "History Export: CSV format"
        );
        alert.showAndWait();
        status("Settings viewed");
    }
    
    @FXML private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("DailyDose - Medicine Tracker");
        alert.setContentText(
            "HOW TO USE:\n\n" +
            "1. MEDICINE: Add your medicines with dosage and frequency\n" +
            "2. REMINDER: Set alarm times for each medicine\n" +
            "3. INVENTORY: Track stock and get low-stock alerts\n" +
            "4. HISTORY: View your dose history and export to CSV\n\n" +
            "ALARM ACTIONS:\n" +
            "• TAKEN ✓ - Marks dose taken, updates history\n" +
            "• MISS ✗ - Marks dose missed\n" +
            "• STOP - Dismisses alarm without recording\n\n" +
            "For support, contact: support@dailydose.app"
        );
        alert.showAndWait();
        status("Help viewed");
    }
    @FXML private void handleLogout()  { navigateToLogin(); }

    private void status(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    private void loadIntoContent(String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(resourcePath));
            Node view = loader.load();
            Object childController = loader.getController();

            // Inject shared controllers where applicable (Java 11 compatible instanceof)
            if (childController instanceof MedicineViewController) {
                MedicineViewController mvc = (MedicineViewController) childController;
                mvc.setControllers(medicineController);
                mvc.refreshTable();
            } else if (childController instanceof ReminderViewController) {
                ReminderViewController rvc = (ReminderViewController) childController;
                rvc.setControllers(reminderController, medicineController);
                rvc.refreshTable();
            } else if (childController instanceof InventoryViewController) {
                InventoryViewController ivc = (InventoryViewController) childController;
                ivc.setControllers(inventoryController, medicineController);
                ivc.refreshTable();
            } else if (childController instanceof HistoryViewController) {
                HistoryViewController hvc = (HistoryViewController) childController;
                hvc.setControllers(historyController);
                hvc.refreshTable();
            }

            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            logger.error("Failed to load view {}", resourcePath, e);
            Label error = new Label("Failed to load view: " + resourcePath);
            contentArea.getChildren().setAll(error);
        }
    }

    private void navigateToLogin() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
            contentArea.getScene().setRoot(loginRoot);
            status("Logged out");
        } catch (Exception e) {
            logger.error("Failed to navigate to login", e);
            new Alert(Alert.AlertType.ERROR, "Could not log out.").showAndWait();
        }
    }

    /** Called when app is shutting down to cleanup all resources (especially alarm timer). */
    public void shutdown() {
        logger.info("MainDashboardController shutting down...");
        reminderController.shutdown();
    }
}
