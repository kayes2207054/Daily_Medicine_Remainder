package com.example.controller.fx;

import com.example.controller.HistoryController;
import com.example.model.DoseHistory;
import com.example.utils.FileUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryViewController {
    @FXML private TableView<DoseHistory> historyTable;
    @FXML private TableColumn<DoseHistory, String> medicineCol;
    @FXML private TableColumn<DoseHistory, String> dateCol;
    @FXML private TableColumn<DoseHistory, String> statusCol;
    @FXML private Label status;

    private HistoryController historyController;
    private final ObservableList<DoseHistory> items = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setControllers(HistoryController historyController) {
        this.historyController = historyController;
        initTable();
    }

    @FXML
    public void initialize() {
        // defer until controller set
    }

    public void refreshTable() {
        if (historyController == null) return;
        List<DoseHistory> all = historyController.getAllHistory();
        items.setAll(all);
        historyTable.setItems(items);
        setStatus("হিস্ট্রি লোড হয়েছে");
    }

    @FXML
    private void handleExportCSV() {
        if (historyController == null) return;
        String path = FileUtils.exportHistoryToCSV(historyController.getAllHistory(), "history_export_fx");
        if (path != null) {
            new Alert(Alert.AlertType.INFORMATION, "CSV সংরক্ষিত: " + path, ButtonType.OK).showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "এক্সপোর্ট ব্যর্থ", ButtonType.OK).showAndWait();
        }
    }

    private void initTable() {
        medicineCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMedicineName()));
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDate() != null ? c.getValue().getDate().format(DATE_FMT) : ""));
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
    }

    private void setStatus(String text) { if (status != null) status.setText(text); }
}
