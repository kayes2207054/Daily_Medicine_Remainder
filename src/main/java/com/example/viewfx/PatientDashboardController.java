package com.example.viewfx;

import com.example.DailyDoseApp;
import com.example.controller.*;
import com.example.model.*;
import com.example.utils.DataChangeListener;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Patient Dashboard Controller
 * Main interface for patient to manage medicines, reminders, and view history
 * 
 * রোগীর ড্যাশবোর্ড কন্ট্রোলার - ঔষধ, রিমাইন্ডার এবং ইতিহাস পরিচালনার প্রধান ইন্টারফেস
 */
public class PatientDashboardController implements DataChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(PatientDashboardController.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    
    // Dashboard
    @FXML private Label userLabel;
    @FXML private Label totalMedicinesLabel;
    @FXML private Label pendingRemindersLabel;
    @FXML private Label takenTodayLabel;
    @FXML private Label missedTodayLabel;
    @FXML private TableView<DoseHistory> recentActivityTable;
    @FXML private TableColumn<DoseHistory, String> dateColumn;
    @FXML private TableColumn<DoseHistory, String> timeColumn;
    @FXML private TableColumn<DoseHistory, String> medicineColumn;
    @FXML private TableColumn<DoseHistory, String> statusColumn;
    @FXML private TableColumn<DoseHistory, String> notesColumn;
    
    // Medicines Tab
    @FXML private TextField medicineSearchField;
    @FXML private TableView<Medicine> medicinesTable;
    @FXML private TableColumn<Medicine, String> medIdColumn;
    @FXML private TableColumn<Medicine, String> medNameColumn;
    @FXML private TableColumn<Medicine, String> medDosageColumn;
    @FXML private TableColumn<Medicine, String> medFrequencyColumn;
    @FXML private TableColumn<Medicine, String> medInstructionsColumn;
    
    // Reminders Tab
    @FXML private TableView<Reminder> remindersTable;
    @FXML private TableColumn<Reminder, String> remIdColumn;
    @FXML private TableColumn<Reminder, String> remMedicineColumn;
    @FXML private TableColumn<Reminder, String> remDateColumn;
    @FXML private TableColumn<Reminder, String> remTimeColumn;
    @FXML private TableColumn<Reminder, String> remStatusColumn;
    
    // History Tab
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<DoseHistory> historyTable;
    @FXML private TableColumn<DoseHistory, String> histIdColumn;
    @FXML private TableColumn<DoseHistory, String> histMedicineColumn;
    @FXML private TableColumn<DoseHistory, String> histDateColumn;
    @FXML private TableColumn<DoseHistory, String> histTimeColumn;
    @FXML private TableColumn<DoseHistory, String> histStatusColumn;
    @FXML private TableColumn<DoseHistory, String> histNotesColumn;
    
    // Controllers
    private final MedicineController medicineController;
    private final ReminderController reminderController;
    private final HistoryController historyController;
    private final UserController userController;
    
    // Alarm System
    private Timeline alarmChecker;
    private final AlarmService alarmService;
    
    public PatientDashboardController() {
        this.medicineController = new MedicineController();
        this.reminderController = new ReminderController();
        this.historyController = new HistoryController();
        this.userController = new UserController();
        this.alarmService = new AlarmService(reminderController, userController);
    }
    
    @FXML
    private void initialize() {
        logger.info("Patient Dashboard initialized");
        
        // Set user name
        User currentUser = userController.getCurrentUser();
        if (currentUser != null) {
            userLabel.setText("Welcome, " + currentUser.getFullName());
        }
        
        // Register as listener for data changes
        medicineController.addDataChangeListener(this);
        reminderController.addDataChangeListener(this);
        historyController.addDataChangeListener(this);
        
        // Initialize tables
        initializeDashboardTable();
        initializeMedicinesTable();
        initializeRemindersTable();
        initializeHistoryTable();
        
        // Load data
        refreshDashboard();
        loadMedicines();
        loadReminders();
        loadHistory();
        
        // Setup search
        setupMedicineSearch();
        
        // Start alarm system
        startAlarmSystem();
        
        logger.info("Dashboard initialization complete");
    }
    
    /**
     * Initialize Dashboard Table
     */
    private void initializeDashboardTable() {
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().format(DATE_FORMAT)));
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getTime() != null ? data.getValue().getTime().format(TIME_FORMAT) : ""));
        medicineColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicineName()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        notesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNotes()));
        
        // Color code status
        statusColumn.setCellFactory(column -> new TableCell<DoseHistory, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("TAKEN")) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else if (item.equals("MISSED")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }
    
    /**
     * Initialize Medicines Table
     */
    private void initializeMedicinesTable() {
        medIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        medNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        medDosageColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDosage()));
        medFrequencyColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFrequency()));
        medInstructionsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getInstructions()));
    }
    
    /**
     * Initialize Reminders Table
     */
    private void initializeRemindersTable() {
        remIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        remMedicineColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicineName()));
        remDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getReminderTime() != null ? data.getValue().getReminderTime().toLocalDate().format(DATE_FORMAT) : ""));
        remTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getReminderTime() != null ? data.getValue().getReminderTime().toLocalTime().format(TIME_FORMAT) : ""));
        remStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));
        
        // Color code status
        remStatusColumn.setCellFactory(column -> new TableCell<Reminder, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("PENDING")) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else if (item.equals("TAKEN")) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else if (item.equals("MISSED")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }
    
    /**
     * Initialize History Table
     */
    private void initializeHistoryTable() {
        histIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        histMedicineColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicineName()));
        histDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().format(DATE_FORMAT)));
        histTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getTime() != null ? data.getValue().getTime().format(TIME_FORMAT) : ""));
        histStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        histNotesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNotes()));
        
        // Color code status
        histStatusColumn.setCellFactory(column -> new TableCell<DoseHistory, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("TAKEN")) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else if (item.equals("MISSED")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }
    
    /**
     * Refresh Dashboard Statistics
     */
    private void refreshDashboard() {
        // Total medicines
        int totalMedicines = medicineController.getAllMedicines().size();
        totalMedicinesLabel.setText(String.valueOf(totalMedicines));
        
        // Pending reminders
        int pendingReminders = reminderController.getPendingReminders().size();
        pendingRemindersLabel.setText(String.valueOf(pendingReminders));
        
        // Today's doses
        LocalDate today = LocalDate.now();
        List<DoseHistory> todayHistory = historyController.getHistoryByDate(today);
        
        long takenToday = todayHistory.stream()
            .filter(h -> h.getStatus().equals(DoseHistory.STATUS_TAKEN))
            .count();
        takenTodayLabel.setText(String.valueOf(takenToday));
        
        long missedToday = todayHistory.stream()
            .filter(h -> h.getStatus().equals(DoseHistory.STATUS_MISSED))
            .count();
        missedTodayLabel.setText(String.valueOf(missedToday));
        
        // Recent activity (last 7 days)
        LocalDate weekAgo = today.minusDays(7);
        List<DoseHistory> recentHistory = historyController.getAllHistory().stream()
            .filter(h -> !h.getDate().isBefore(weekAgo))
            .sorted((a, b) -> b.getRecordedAt().compareTo(a.getRecordedAt()))
            .limit(10)
            .collect(Collectors.toList());
        
        ObservableList<DoseHistory> recentData = FXCollections.observableArrayList(recentHistory);
        recentActivityTable.setItems(recentData);
    }
    
    /**
     * Load medicines
     */
    private void loadMedicines() {
        List<Medicine> medicines = medicineController.getAllMedicines();
        ObservableList<Medicine> data = FXCollections.observableArrayList(medicines);
        medicinesTable.setItems(data);
    }
    
    /**
     * Load reminders
     */
    private void loadReminders() {
        List<Reminder> reminders = reminderController.getAllReminders();
        ObservableList<Reminder> data = FXCollections.observableArrayList(reminders);
        remindersTable.setItems(data);
    }
    
    /**
     * Load history
     */
    private void loadHistory() {
        List<DoseHistory> history = historyController.getAllHistory();
        ObservableList<DoseHistory> data = FXCollections.observableArrayList(history);
        historyTable.setItems(data);
    }
    
    /**
     * Setup medicine search
     */
    private void setupMedicineSearch() {
        medicineSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                loadMedicines();
            } else {
                List<Medicine> filtered = medicineController.searchMedicines(newValue.trim());
                ObservableList<Medicine> data = FXCollections.observableArrayList(filtered);
                medicinesTable.setItems(data);
            }
        });
    }
    
    /**
     * Handle Add Medicine
     */
    @FXML
    private void handleAddMedicine() {
        Dialog<Medicine> dialog = new Dialog<>();
        dialog.setTitle("Add New Medicine");
        dialog.setHeaderText("Enter medicine details");
        
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Medicine Name");
        TextField dosageField = new TextField();
        dosageField.setPromptText("Dosage (e.g., 10mg)");
        TextField frequencyField = new TextField();
        frequencyField.setPromptText("Frequency (e.g., Twice Daily)");
        TextArea instructionsArea = new TextArea();
        instructionsArea.setPromptText("Instructions");
        instructionsArea.setPrefRowCount(3);
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Dosage:"), 0, 1);
        grid.add(dosageField, 1, 1);
        grid.add(new Label("Frequency:"), 0, 2);
        grid.add(frequencyField, 1, 2);
        grid.add(new Label("Instructions:"), 0, 3);
        grid.add(instructionsArea, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        Platform.runLater(nameField::requestFocus);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Medicine medicine = new Medicine();
                medicine.setName(nameField.getText());
                medicine.setDosage(dosageField.getText());
                medicine.setFrequency(frequencyField.getText());
                medicine.setInstructions(instructionsArea.getText());
                return medicine;
            }
            return null;
        });
        
        Optional<Medicine> result = dialog.showAndWait();
        result.ifPresent(medicine -> {
            if (!medicine.getName().isEmpty() && !medicine.getDosage().isEmpty()) {
                int id = medicineController.addMedicine(medicine);
                if (id > 0) {
                    showInfo("Medicine added successfully!");
                    // Listener will auto-refresh the view - no need to manually call loadMedicines()
                } else {
                    showError("Failed to add medicine. It may already exist.");
                }
            } else {
                showError("Name and Dosage are required!");
            }
        });
    }
    
    /**
     * Handle Edit Medicine
     */
    @FXML
    private void handleEditMedicine() {
        Medicine selected = medicinesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a medicine to edit!");
            return;
        }
        
        // Similar dialog for edit
        showInfo("Edit medicine feature - implementation similar to Add");
    }
    
    /**
     * Handle Delete Medicine
     */
    @FXML
    private void handleDeleteMedicine() {
        Medicine selected = medicinesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a medicine to delete!");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Medicine");
        alert.setHeaderText("Delete " + selected.getName() + "?");
        alert.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            medicineController.deleteMedicine(selected.getId());
            loadMedicines();
            refreshDashboard();
            showInfo("Medicine deleted successfully!");
        }
    }
    
    /**
     * Handle Add Reminder
     */
    @FXML
    private void handleAddReminder() {
        Dialog<Reminder> dialog = new Dialog<>();
        dialog.setTitle("Add Reminder");
        dialog.setHeaderText("Set a new reminder");
        
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField medicineField = new TextField();
        medicineField.setPromptText("Medicine Name");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField timeField = new TextField();
        timeField.setPromptText("HH:mm (24-hour format)");
        
        grid.add(new Label("Medicine:"), 0, 0);
        grid.add(medicineField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Time:"), 0, 2);
        grid.add(timeField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    LocalTime time = LocalTime.parse(timeField.getText(), TIME_FORMAT);
                    LocalDateTime reminderTime = LocalDateTime.of(datePicker.getValue(), time);
                    
                    Reminder reminder = new Reminder();
                    reminder.setMedicineName(medicineField.getText());
                    reminder.setReminderTime(reminderTime);
                    reminder.setStatus(Reminder.Status.PENDING);
                    return reminder;
                } catch (Exception e) {
                    showError("Invalid time format! Use HH:mm");
                    return null;
                }
            }
            return null;
        });
        
        Optional<Reminder> result = dialog.showAndWait();
        result.ifPresent(reminder -> {
            if (reminder != null && !reminder.getMedicineName().isEmpty()) {
                reminderController.addReminder(reminder);
                loadReminders();
                refreshDashboard();
                showInfo("Reminder added successfully!");
            }
        });
    }
    
    /**
     * Handle Edit/Delete Reminder
     */
    @FXML
    private void handleEditReminder() {
        showInfo("Edit reminder feature");
    }
    
    @FXML
    private void handleDeleteReminder() {
        Reminder selected = remindersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a reminder to delete!");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Reminder");
        alert.setHeaderText("Delete reminder for " + selected.getMedicineName() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            reminderController.deleteReminder(selected.getId());
            loadReminders();
            refreshDashboard();
            showInfo("Reminder deleted!");
        }
    }
    
    @FXML
    private void refreshReminders() {
        loadReminders();
        refreshDashboard();
    }
    
    /**
     * Filter history by date range
     */
    @FXML
    private void filterHistory() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        
        if (start != null && end != null) {
            List<DoseHistory> filtered = historyController.getHistoryByDateRange(start, end);
            ObservableList<DoseHistory> data = FXCollections.observableArrayList(filtered);
            historyTable.setItems(data);
        } else {
            showError("Please select both start and end dates!");
        }
    }
    
    @FXML
    private void showAllHistory() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        loadHistory();
    }
    
    /**
     * Handle Logout
     */
    @FXML
    private void handleLogout() {
        // Stop alarm system
        if (alarmChecker != null) {
            alarmChecker.stop();
        }
        alarmService.stop();
        
        // Logout
        userController.logout();
        
        try {
            DailyDoseApp.showLoginScreen();
        } catch (Exception e) {
            logger.error("Error navigating to login", e);
        }
    }
    
    /**
     * Start Alarm System
     * রিমাইন্ডার চেক করার জন্য টাইমলাইন শুরু করুন
     */
    private void startAlarmSystem() {
        alarmService.start();
        logger.info("Alarm system started");
    }
    
    /**
     * Show error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show info alert
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // DataChangeListener implementation - auto-refresh UI when data changes
    @Override
    public void onMedicineDataChanged() {
        Platform.runLater(() -> {
            loadMedicines();
            refreshDashboard();
        });
    }
    
    @Override
    public void onReminderDataChanged() {
        Platform.runLater(() -> {
            loadReminders();
            refreshDashboard();
        });
    }
    
    @Override
    public void onInventoryDataChanged() {
        Platform.runLater(() -> {
            refreshDashboard();
        });
    }
    
    @Override
    public void onHistoryDataChanged() {
        Platform.runLater(() -> {
            loadHistory();
            refreshDashboard();
        });
    }
}
