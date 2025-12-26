package com.example.controller.fx;

// JavaFX + FXML Migration — Phase 2
// Dashboard controller wired to backend controllers (Swing-free)

import com.example.controller.HistoryController;
import com.example.controller.InventoryController;
import com.example.controller.MedicineController;
import com.example.controller.ReminderController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

    @FXML private void showSettings()  { status("Settings (TODO)"); }
    @FXML private void showHelp()      { status("Help (TODO)"); }
    @FXML private void handleLogout()  { navigateToLogin(); }

    private void status(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    private void loadIntoContent(String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(resourcePath));
            Node view = loader.load();
            Object childController = loader.getController();

            // Inject shared controllers where applicable
            if (childController instanceof MedicineViewController mvc) {
                mvc.setControllers(medicineController);
                mvc.refreshTable();
            } else if (childController instanceof ReminderViewController rvc) {
                rvc.setControllers(reminderController, medicineController);
                rvc.refreshTable();
            } else if (childController instanceof InventoryViewController ivc) {
                ivc.setControllers(inventoryController, medicineController);
                ivc.refreshTable();
            } else if (childController instanceof HistoryViewController hvc) {
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
            Node loginRoot = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
            contentArea.getScene().setRoot(loginRoot);
            status("Logged out");
        } catch (Exception e) {
            logger.error("Failed to navigate to login", e);
            new Alert(Alert.AlertType.ERROR, "Could not log out.").showAndWait();
        }
    }
}
