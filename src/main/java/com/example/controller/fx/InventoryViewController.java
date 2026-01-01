package com.example.controller.fx;

import com.example.controller.InventoryController;
import com.example.controller.MedicineController;
import com.example.model.Inventory;
import com.example.model.Medicine;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class InventoryViewController {
    @FXML private TableView<Inventory> inventoryTable;
    @FXML private TableColumn<Inventory, String> medicineCol;
    @FXML private TableColumn<Inventory, Number> quantityCol;
    @FXML private TableColumn<Inventory, String> expiryCol;
    @FXML private Label status;

    private InventoryController inventoryController;
    private MedicineController medicineController;
    private final ObservableList<Inventory> items = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setControllers(InventoryController inventoryController, MedicineController medicineController) {
        this.inventoryController = inventoryController;
        this.medicineController = medicineController;
        initTable();
    }

    @FXML
    public void initialize() {
        // wait for controller injection
    }

    public void refreshTable() {
        if (inventoryController == null) return;
        items.setAll(inventoryController.getAllInventory());
        inventoryTable.setItems(items);
        setStatus("ইনভেন্টরি লোড হয়েছে");
        showAlerts();
    }

    @FXML private void handleAdd() {
        Inventory inv = showDialog(null);
        if (inv != null) {
            int id = inventoryController.addInventory(inv);
            if (id > 0) {
                refreshTable();
                setStatus("ইনভেন্টরি যোগ হয়েছে");
            } else {
                error("যোগ করতে ব্যর্থ");
            }
        }
    }

    @FXML private void handleUpdate() {
        Inventory selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("একটি সারি নির্বাচন করুন"); return; }
        Inventory updated = showDialog(selected);
        if (updated != null) {
            updated.setId(selected.getId());
            if (inventoryController.updateInventory(updated)) {
                refreshTable();
                setStatus("আপডেট হয়েছে");
            } else {
                error("আপডেট ব্যর্থ");
            }
        }
    }

    @FXML private void handleDelete() {
        Inventory selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("একটি সারি নির্বাচন করুন"); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "মুছবেন?", ButtonType.YES, ButtonType.NO);
        c.setHeaderText(null);
        if (c.showAndWait().filter(btn -> btn == ButtonType.YES).isPresent()) {
            if (inventoryController.deleteInventory(selected.getId())) {
                refreshTable();
                setStatus("ডিলিট হয়েছে");
            } else {
                error("ডিলিট ব্যর্থ");
            }
        }
    }

    private void initTable() {
        medicineCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMedicineName()));
        quantityCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()));
        expiryCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstimatedRefillDate() != null ? c.getValue().getEstimatedRefillDate().format(DATE_FMT) : ""));
    }

    private Inventory showDialog(Inventory existing) {
        Dialog<Inventory> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "ইনভেন্টরি যোগ" : "ইনভেন্টরি আপডেট");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        List<Medicine> meds = medicineController != null ? medicineController.getAllMedicines() : List.of();
        ComboBox<Medicine> medBox = new ComboBox<>(FXCollections.observableArrayList(meds));
        medBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Medicine m) { return m == null ? "" : m.getName(); }
            @Override public Medicine fromString(String s) { return null; }
        });
        if (existing != null) {
            medBox.getSelectionModel().select(meds.stream()
                    .filter(m -> m.getId() == existing.getMedicineId())
                    .findFirst().orElse(null));
        }

        TextField qty = new TextField(existing != null ? String.valueOf(existing.getQuantity()) : "30");
        TextField threshold = new TextField(existing != null ? String.valueOf(existing.getThreshold()) : "10");
        TextField daily = new TextField(existing != null ? String.valueOf(existing.getDailyUsage()) : "1");

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("ওষুধ"), medBox);
        grid.addRow(1, new Label("পরিমাণ"), qty);
        grid.addRow(2, new Label("থ্রেশহোল্ড"), threshold);
        grid.addRow(3, new Label("দৈনিক খরচ"), daily);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Medicine m = medBox.getValue();
                if (m == null) { error("একটি ওষুধ নির্বাচন করুন"); return null; }
                try {
                    int q = Integer.parseInt(qty.getText().trim());
                    int th = Integer.parseInt(threshold.getText().trim());
                    int du = Integer.parseInt(daily.getText().trim());
                    Inventory inv = existing != null ? existing : new Inventory();
                    inv.setMedicineId(m.getId());
                    inv.setMedicineName(m.getName());
                    inv.setQuantity(q);
                    inv.setThreshold(th);
                    inv.setDailyUsage(du);
                    return inv;
                } catch (NumberFormatException ex) {
                    error("সংখ্যা সঠিক নয়");
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private void setStatus(String text) { if (status != null) status.setText(text); }
    private void error(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
    private void info(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }

    /**
     * Show low-stock and refill-soon alerts so user can act quickly.
     */
    private void showAlerts() {
        if (inventoryController == null) return;

        List<Inventory> low = inventoryController.getLowStockItems();
        List<Inventory> soon = inventoryController.getItemsNeedingRefillSoon();

        StringBuilder sb = new StringBuilder();
        if (!low.isEmpty()) {
            sb.append("লো স্টক: \n");
            low.forEach(inv -> sb.append("• ")
                    .append(inv.getMedicineName())
                    .append(" (পরিমাণ ")
                    .append(inv.getQuantity())
                    .append("/থ্রেশহোল্ড ")
                    .append(inv.getThreshold())
                    .append(")\n"));
            sb.append("\n");
        }

        if (!soon.isEmpty()) {
            sb.append("রিফিল শীঘ্র: \n");
            soon.forEach(inv -> sb.append("• ")
                    .append(inv.getMedicineName())
                    .append(" (রিফিল তারিখ ")
                    .append(inv.getEstimatedRefillDate() != null ? inv.getEstimatedRefillDate().format(DATE_FMT) : "—")
                    .append(")\n"));
        }

        if (sb.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ইনভেন্টরি সতর্কতা");
            alert.setHeaderText("দ্রুত রিফিল প্রয়োজন");
            alert.setContentText(sb.toString());
            alert.show();
        }
    }
}
