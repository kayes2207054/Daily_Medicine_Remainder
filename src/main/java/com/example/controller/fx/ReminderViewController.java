package com.example.controller.fx;

import com.example.controller.MedicineController;
import com.example.controller.ReminderController;
import com.example.model.Reminder;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ReminderViewController {
    @FXML private TableView<Reminder> reminderTable;
    @FXML private TableColumn<Reminder, String> medicineCol;
    @FXML private TableColumn<Reminder, String> timeCol;
    @FXML private TableColumn<Reminder, String> statusCol;
    @FXML private Label status;

    private ReminderController reminderController;
    private MedicineController medicineController;
    private final ObservableList<Reminder> items = FXCollections.observableArrayList();
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setControllers(ReminderController reminderController, MedicineController medicineController) {
        this.reminderController = reminderController;
        this.medicineController = medicineController;
        initTable();
    }

    @FXML
    public void initialize() {
        // defer until controllers injected
    }

    public void refreshTable() {
        if (reminderController == null) return;
        items.setAll(reminderController.getRemindersSortedByTime());
        reminderTable.setItems(items);
        setStatus("রিমাইন্ডার লোড হয়েছে");
    }

    @FXML private void handleAdd() {
        Reminder r = showAddDialog();
        if (r != null) {
            reminderController.addReminder(r);
            refreshTable();
            setStatus("রিমাইন্ডার যোগ হয়েছে");
        }
    }

    @FXML private void handleDelete() {
        Reminder sel = reminderTable.getSelectionModel().getSelectedItem();
        if (sel == null) { info("একটি সারি নির্বাচন করুন"); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "রিমাইন্ডার মুছবেন?", ButtonType.YES, ButtonType.NO);
        c.setHeaderText(null);
        Optional<ButtonType> res = c.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            reminderController.deleteReminder(sel.getId());
            refreshTable();
            setStatus("রিমাইন্ডার ডিলিট হয়েছে");
        }
    }

    @FXML private void handleMarkTaken() {
        Reminder sel = reminderTable.getSelectionModel().getSelectedItem();
        if (sel == null) { info("একটি সারি নির্বাচন করুন"); return; }
        reminderController.markTaken(sel.getId());
        refreshTable();
        setStatus("Taken মার্ক করা হয়েছে");
    }

    @FXML private void handleMarkMissed() {
        Reminder sel = reminderTable.getSelectionModel().getSelectedItem();
        if (sel == null) { info("একটি সারি নির্বাচন করুন"); return; }
        reminderController.markMissed(sel.getId());
        refreshTable();
        setStatus("Missed মার্ক করা হয়েছে");
    }

    private void initTable() {
        medicineCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMedicineName()));
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getReminderTime() != null ? c.getValue().getReminderTime().format(DISPLAY_FMT) : ""));
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatus() != null ? c.getValue().getStatus().name() : ""));
    }

    private Reminder showAddDialog() {
        Dialog<Reminder> dialog = new Dialog<>();
        dialog.setTitle("রিমাইন্ডার যোগ");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField med = new TextField();
        if (medicineController != null && !medicineController.getAllMedicines().isEmpty()) {
            med.setPromptText("ওষুধের নাম (উদা: " + medicineController.getAllMedicines().get(0).getName() + ")");
        }
        TextField time = new TextField("08:00");

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("ওষুধের নাম"), med);
        grid.addRow(1, new Label("সময় (HH:mm)"), time);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    LocalTime lt = LocalTime.parse(time.getText().trim());
                    LocalDateTime dt = LocalDate.now().atTime(lt);
                    return new Reminder(med.getText().trim(), dt);
                } catch (Exception ex) {
                    alertError("সময় HH:mm ফর্ম্যাটে দিন");
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private void setStatus(String text) {
        if (status != null) status.setText(text);
    }

    private void alertError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
