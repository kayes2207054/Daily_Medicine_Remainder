package com.example.viewfx;

import com.example.DailyDoseApp;
import com.example.controller.HistoryController;
import com.example.controller.UserController;
import com.example.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Guardian Dashboard Controller
 * Interface for guardians to monitor patient medicine compliance
 * 
 * অভিভাবক ড্যাশবোর্ড কন্ট্রোলার - রোগীদের ঔষধ মেনে চলা নিরীক্ষণ করার ইন্টারফেস
 */
public class GuardianDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(GuardianDashboardController.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Header
    @FXML private Label userLabel;
    @FXML private Label notificationBadge;
    
    // Link Patient
    @FXML private TextField linkPatientField;
    
    // Patients Table
    @FXML private TableView<GuardianPatientLink> patientsTable;
    @FXML private TableColumn<GuardianPatientLink, String> patientIdColumn;
    @FXML private TableColumn<GuardianPatientLink, String> patientNameColumn;
    @FXML private TableColumn<GuardianPatientLink, String> patientUsernameColumn;
    @FXML private TableColumn<GuardianPatientLink, String> linkedSinceColumn;
    @FXML private TableColumn<GuardianPatientLink, String> adherenceColumn;
    @FXML private TableColumn<GuardianPatientLink, String> actionsColumn;
    
    // Patient History
    @FXML private ComboBox<String> patientComboBox;
    @FXML private DatePicker histStartDate;
    @FXML private DatePicker histEndDate;
    @FXML private VBox patientProfileCard;
    @FXML private Label profileFullNameLabel;
    @FXML private Label profileUsernameLabel;
    @FXML private Label profileEmailLabel;
    @FXML private Label profileRoleLabel;
    @FXML private Label profileCreatedLabel;
    @FXML private Label profileStatusLabel;
    @FXML private Label totalDosesLabel;
    @FXML private Label takenDosesLabel;
    @FXML private Label missedDosesLabel;
    @FXML private Label adherencePercentLabel;
    @FXML private TableView<DoseHistory> patientHistoryTable;
    @FXML private TableColumn<DoseHistory, String> histDateColumn;
    @FXML private TableColumn<DoseHistory, String> histTimeColumn;
    @FXML private TableColumn<DoseHistory, String> histMedicineColumn;
    @FXML private TableColumn<DoseHistory, String> histStatusColumn;
    @FXML private TableColumn<DoseHistory, String> histNotesColumn;
    
    // Notifications
    @FXML private Label unreadCountLabel;
    @FXML private ListView<VBox> notificationsList;
    
    // Controllers
    private final UserController userController;
    private final HistoryController historyController;
    
    // Current patient selection
    private int selectedPatientId = -1;
    
    public GuardianDashboardController() {
        this.userController = new UserController();
        this.historyController = new HistoryController();
    }
    
    @FXML
    private void initialize() {
        logger.info("Guardian Dashboard initialized");
        
        // Set user name
        User currentUser = userController.getCurrentUser();
        if (currentUser != null) {
            userLabel.setText("Welcome, " + currentUser.getFullName());
        }
        
        // Initialize tables
        initializePatientsTable();
        initializeHistoryTable();
        
        // Load data
        loadLinkedPatients();
        loadNotifications();
        
        logger.info("Guardian Dashboard initialization complete");
    }
    
    /**
     * Initialize Patients Table
     */
    private void initializePatientsTable() {
        patientIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPatientId())));
        patientNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientFullName()));
        patientUsernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientUsername()));
        linkedSinceColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getLinkedAt() != null ? data.getValue().getLinkedAt().format(DATE_FORMAT) : ""));
        
        // Calculate adherence for each patient
        adherenceColumn.setCellValueFactory(data -> {
            int patientId = data.getValue().getPatientId();
            double adherence = calculatePatientAdherence(patientId, 30); // Last 30 days
            return new SimpleStringProperty(String.format("%.1f%%", adherence));
        });
        
        // Add view button
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button viewButton = new Button("View Details");
            
            {
                viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                viewButton.setOnAction(event -> {
                    GuardianPatientLink link = getTableView().getItems().get(getIndex());
                    selectPatientForHistory(link);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewButton);
            }
        });
    }
    
    /**
     * Initialize History Table
     */
    private void initializeHistoryTable() {
        histDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().format(DATE_FORMAT)));
        histTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getTime() != null ? data.getValue().getTime().format(TIME_FORMAT) : ""));
        histMedicineColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicineName()));
        histStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        histNotesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNotes()));
        
        // Color code status
        histStatusColumn.setCellFactory(column -> new TableCell<>() {
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
     * Load linked patients
     * সংযুক্ত রোগীদের লোড করুন
     */
    private void loadLinkedPatients() {
        User currentUser = userController.getCurrentUser();
        if (currentUser == null || !currentUser.isGuardian()) {
            return;
        }
        
        List<GuardianPatientLink> links = userController.getPatientsForGuardian(currentUser.getId());
        ObservableList<GuardianPatientLink> data = FXCollections.observableArrayList(links);
        patientsTable.setItems(data);
        
        // Update patient combo box
        List<String> patientNames = links.stream()
            .map(link -> link.getPatientFullName() + " (" + link.getPatientUsername() + ")")
            .collect(Collectors.toList());
        patientComboBox.setItems(FXCollections.observableArrayList(patientNames));
        
        logger.info("Loaded {} linked patients", links.size());
    }
    
    /**
     * Calculate patient adherence percentage
     */
    private double calculatePatientAdherence(int patientId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<DoseHistory> history = historyController.getAllHistory().stream()
            .filter(h -> {
                // Filter by patient if we had patient-specific history
                // For now, approximate using all history
                return !h.getDate().isBefore(startDate) && !h.getDate().isAfter(endDate);
            })
            .collect(Collectors.toList());
        
        if (history.isEmpty()) return 0.0;
        
        long taken = history.stream().filter(h -> h.getStatus().equals("TAKEN")).count();
        return (taken * 100.0) / history.size();
    }
    
    /**
     * Handle Link Patient
     * রোগী সংযুক্ত করুন
     */
    @FXML
    private void handleLinkPatient() {
        String patientUsername = linkPatientField.getText().trim();
        
        if (patientUsername.isEmpty()) {
            showError("Please enter patient username!");
            return;
        }
        
        User currentUser = userController.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        // Find patient user
        User patient = userController.getPatientByUsername(patientUsername);
        if (patient == null) {
            showError("Patient not found or user is not a patient!");
            return;
        }
        
        // Link
        boolean success = userController.linkGuardianToPatient(currentUser.getId(), patient.getId());
        
        if (success) {
            showInfo("Patient linked successfully!");
            linkPatientField.clear();
            loadLinkedPatients();
        } else {
            showError("Failed to link patient. May already be linked.");
        }
    }
    
    /**
     * Select patient for history view
     */
    private void selectPatientForHistory(GuardianPatientLink link) {
        selectedPatientId = link.getPatientId();
        patientComboBox.setValue(link.getPatientFullName() + " (" + link.getPatientUsername() + ")");
        loadPatientHistory();
    }
    
    /**
     * Load patient history
     * রোগীর ইতিহাস লোড করুন
     */
    @FXML
    private void loadPatientHistory() {
        if (selectedPatientId == -1 && patientComboBox.getValue() == null) {
            showError("Please select a patient!");
            return;
        }
        
        // Load patient profile
        loadPatientProfile();
        
        // For now, load all history (in production, filter by patient)
        List<DoseHistory> history = historyController.getAllHistory();
        
        // Calculate statistics
        long total = history.size();
        long taken = history.stream().filter(h -> h.getStatus().equals("TAKEN")).count();
        long missed = history.stream().filter(h -> h.getStatus().equals("MISSED")).count();
        double adherence = total > 0 ? (taken * 100.0) / total : 0.0;
        
        totalDosesLabel.setText(String.valueOf(total));
        takenDosesLabel.setText(String.valueOf(taken));
        missedDosesLabel.setText(String.valueOf(missed));
        adherencePercentLabel.setText(String.format("%.1f%%", adherence));
        
        // Load table
        ObservableList<DoseHistory> data = FXCollections.observableArrayList(history);
        patientHistoryTable.setItems(data);
    }
    
    /**
     * Load patient profile information
     * রোগীর প্রোফাইল তথ্য লোড করুন
     */
    private void loadPatientProfile() {
        if (selectedPatientId == -1) {
            patientProfileCard.setVisible(false);
            patientProfileCard.setManaged(false);
            return;
        }
        
        try {
            // Get patient user object
            User patient = userController.getUserById(selectedPatientId);
            
            if (patient != null) {
                // Show profile card
                patientProfileCard.setVisible(true);
                patientProfileCard.setManaged(true);
                
                // Fill profile details
                profileFullNameLabel.setText(patient.getFullName() != null ? patient.getFullName() : "N/A");
                profileUsernameLabel.setText(patient.getUsername());
                profileEmailLabel.setText(patient.getEmail() != null ? patient.getEmail() : "Not provided");
                profileRoleLabel.setText(patient.getRole().toString());
                
                // Format created date
                if (patient.getCreatedAt() != null) {
                    profileCreatedLabel.setText(patient.getCreatedAt().format(DATETIME_FORMAT));
                } else {
                    profileCreatedLabel.setText("N/A");
                }
                
                // Set status
                if (patient.isActive()) {
                    profileStatusLabel.setText("✅ Active");
                    profileStatusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                } else {
                    profileStatusLabel.setText("❌ Inactive");
                    profileStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        } catch (Exception e) {
            logger.error("Error loading patient profile", e);
            showError("Error loading patient profile: " + e.getMessage());
        }
    }
    
    /**
     * Filter patient history by date range
     */
    @FXML
    private void filterPatientHistory() {
        LocalDate start = histStartDate.getValue();
        LocalDate end = histEndDate.getValue();
        
        if (start != null && end != null) {
            List<DoseHistory> filtered = historyController.getHistoryByDateRange(start, end);
            
            // Update statistics
            long total = filtered.size();
            long taken = filtered.stream().filter(h -> h.getStatus().equals("TAKEN")).count();
            long missed = filtered.stream().filter(h -> h.getStatus().equals("MISSED")).count();
            double adherence = total > 0 ? (taken * 100.0) / total : 0.0;
            
            totalDosesLabel.setText(String.valueOf(total));
            takenDosesLabel.setText(String.valueOf(taken));
            missedDosesLabel.setText(String.valueOf(missed));
            adherencePercentLabel.setText(String.format("%.1f%%", adherence));
            
            ObservableList<DoseHistory> data = FXCollections.observableArrayList(filtered);
            patientHistoryTable.setItems(data);
        } else {
            showError("Please select both start and end dates!");
        }
    }
    
    /**
     * Load notifications
     * বিজ্ঞপ্তি লোড করুন
     */
    private void loadNotifications() {
        User currentUser = userController.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        List<Notification> notifications = userController.getNotificationsForGuardian(currentUser.getId());
        
        // Count unread
        long unread = notifications.stream().filter(n -> !n.isRead()).count();
        unreadCountLabel.setText(unread + " unread notifications");
        
        if (unread > 0) {
            notificationBadge.setText(String.valueOf(unread));
            notificationBadge.setVisible(true);
        } else {
            notificationBadge.setVisible(false);
        }
        
        // Create notification cards
        ObservableList<VBox> notifCards = FXCollections.observableArrayList();
        for (Notification notif : notifications) {
            VBox card = createNotificationCard(notif);
            notifCards.add(card);
        }
        
        notificationsList.setItems(notifCards);
    }
    
    /**
     * Create notification card
     */
    private VBox createNotificationCard(Notification notification) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: " + (notification.isRead() ? "#ecf0f1" : "#fff3cd") + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + (notification.isRead() ? "#bdc3c7" : "#ffc107") + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 2;"
        );
        
        // Type icon
        String icon = notification.getType() == Notification.Type.DOSE_TAKEN ? "✅" : "❌";
        
        Label titleLabel = new Label(icon + " " + notification.getMessage());
        titleLabel.setFont(Font.font("System Bold", 14));
        
        Label detailsLabel = new Label(notification.getDetails());
        detailsLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        Label timeLabel = new Label(notification.getCreatedAt().format(DATETIME_FORMAT));
        timeLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11;");
        
        Button markReadButton = new Button("✓ Mark Read");
        markReadButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11;");
        markReadButton.setVisible(!notification.isRead());
        markReadButton.setOnAction(e -> {
            userController.markNotificationAsRead(notification.getId());
            refreshNotifications();
        });
        
        card.getChildren().addAll(titleLabel, detailsLabel, timeLabel, markReadButton);
        
        return card;
    }
    
    /**
     * Mark all notifications as read
     */
    @FXML
    private void markAllRead() {
        User currentUser = userController.getCurrentUser();
        if (currentUser != null) {
            userController.markAllNotificationsAsRead(currentUser.getId());
            refreshNotifications();
            showInfo("All notifications marked as read!");
        }
    }
    
    /**
     * Refresh notifications
     */
    @FXML
    private void refreshNotifications() {
        loadNotifications();
    }
    
    /**
     * Handle Logout
     */
    @FXML
    private void handleLogout() {
        userController.logout();
        
        try {
            DailyDoseApp.showLoginScreen();
        } catch (Exception e) {
            logger.error("Error navigating to login", e);
        }
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
}
