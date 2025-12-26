package com.example.controller.fx;

import com.example.controller.MedicineController;
import com.example.model.Medicine;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class MedicineViewController {

    @FXML private TableView<Medicine> medicineTable;
    @FXML private TableColumn<Medicine, String> nameCol;
    @FXML private TableColumn<Medicine, String> dosageCol;
    @FXML private TableColumn<Medicine, String> scheduleCol;
    @FXML private Label status;

    private MedicineController medicineController;
    private ObservableList<Medicine> items = FXCollections.observableArrayList();

    public void setControllers(MedicineController medicineController) {
        this.medicineController = medicineController;
        initTable();
    }

    @FXML
    public void initialize() {
        // Table init deferred until controller injected
    }

    public void refreshTable() {
        if (medicineController == null) return;
        items.setAll(medicineController.getAllMedicines());
        medicineTable.setItems(items);
        setStatus("Medicines লোড হয়েছে");
    }

    @FXML
    private void handleAdd() {
        Medicine m = showMedicineDialog(null);
        if (m != null) {
            int id = medicineController.addMedicine(m);
            if (id > 0) {
                refreshTable();
                setStatus("নতুন ওষুধ যোগ হয়েছে");
            } else {
                alertError("ওষুধ যোগ করতে ব্যর্থ। নাম ডুপ্লিকেট হতে পারে।");
            }
        }
    }

    @FXML
    private void handleEdit() {
        Medicine selected = medicineTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertInfo("একটি সারি নির্বাচন করুন");
            return;
        }
        Medicine updated = showMedicineDialog(selected);
        if (updated != null) {
            updated.setId(selected.getId());
            if (medicineController.updateMedicine(updated)) {
                refreshTable();
                setStatus("ওষুধ আপডেট হয়েছে");
            } else {
                alertError("আপডেট ব্যর্থ।");
            }
        }
    }

    @FXML
    private void handleDelete() {
        Medicine selected = medicineTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertInfo("একটি সারি নির্বাচন করুন");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "মুছতে চান?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            if (medicineController.deleteMedicine(selected.getId())) {
                refreshTable();
                setStatus("ওষুধ ডিলিট হয়েছে");
            } else {
                alertError("ডিলিট ব্যর্থ।");
            }
        }
    }

    private void initTable() {
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        dosageCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDosage()));
        scheduleCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFrequency()));
    }

    private Medicine showMedicineDialog(Medicine existing) {
        Dialog<Medicine> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "নতুন ওষুধ" : "ওষুধ সম্পাদনা");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField name = new TextField(existing != null ? existing.getName() : "");
        TextField dosage = new TextField(existing != null ? existing.getDosage() : "");
        TextField freq = new TextField(existing != null ? existing.getFrequency() : "");
        TextArea instr = new TextArea(existing != null ? existing.getInstructions() : "");
        instr.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("নাম"), name);
        grid.addRow(1, new Label("ডোজ"), dosage);
        grid.addRow(2, new Label("ফ্রিকোয়েন্সি"), freq);
        grid.addRow(3, new Label("নির্দেশনা"), instr);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Medicine m = (existing != null) ? existing : new Medicine();
                m.setName(name.getText().trim());
                m.setDosage(dosage.getText().trim());
                m.setFrequency(freq.getText().trim());
                m.setInstructions(instr.getText().trim());
                return m;
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

    private void alertInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
