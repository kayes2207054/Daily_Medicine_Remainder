package com.example.database;

import com.example.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager Class
 * Handles all SQLite database operations for DailyDose application.
 * Uses JDBC connection pooling with a singleton pattern.
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_URL = "jdbc:sqlite:daily_dose.db";
    private static DatabaseManager instance;
    private Connection connection;

    /**
     * Singleton pattern - get instance of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
            instance.connect();
            instance.initializeDatabase();
        }
        return instance;
    }

    /**
     * Connect to SQLite database
     */
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            logger.info("Database connection established successfully");
        } catch (Exception e) {
            logger.error("Error connecting to database", e);
        }
    }

    /**
     * Disconnect from database
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Initialize database tables if they don't exist
     */
    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Medicines table
            stmt.execute("CREATE TABLE IF NOT EXISTS medicines (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT UNIQUE NOT NULL," +
                    "dosage TEXT NOT NULL," +
                    "frequency TEXT NOT NULL," +
                    "instructions TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Reminders table
            stmt.execute("CREATE TABLE IF NOT EXISTS reminders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medicine_id INTEGER NOT NULL," +
                    "medicine_name TEXT NOT NULL," +
                    "time TEXT NOT NULL," +
                    "reminder_type TEXT NOT NULL," +
                    "taken BOOLEAN DEFAULT 0," +
                    "last_taken_at TIMESTAMP," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(medicine_id) REFERENCES medicines(id) ON DELETE CASCADE)");

            // Inventory table
            stmt.execute("CREATE TABLE IF NOT EXISTS inventory (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medicine_id INTEGER NOT NULL," +
                    "medicine_name TEXT NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "threshold INTEGER DEFAULT 10," +
                    "daily_usage INTEGER DEFAULT 1," +
                    "last_refill_date DATE," +
                    "estimated_refill_date DATE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(medicine_id) REFERENCES medicines(id) ON DELETE CASCADE)");

            // Dose History table
            stmt.execute("CREATE TABLE IF NOT EXISTS dose_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medicine_id INTEGER NOT NULL," +
                    "reminder_id INTEGER," +
                    "medicine_name TEXT NOT NULL," +
                    "date DATE NOT NULL," +
                    "time TEXT," +
                    "status TEXT NOT NULL," +
                    "recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "notes TEXT," +
                    "FOREIGN KEY(medicine_id) REFERENCES medicines(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(reminder_id) REFERENCES reminders(id) ON DELETE SET NULL)");

            logger.info("Database tables initialized successfully");
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
        }
    }

    // ============= MEDICINE OPERATIONS =============

    /**
     * Add new medicine to database
     */
    public int addMedicine(Medicine medicine) {
        String sql = "INSERT INTO medicines(name, dosage, frequency, instructions) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setString(2, medicine.getDosage());
            pstmt.setString(3, medicine.getFrequency());
            pstmt.setString(4, medicine.getInstructions());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error adding medicine", e);
        }
        return -1;
    }

    /**
     * Get all medicines from database
     */
    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines ORDER BY name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Medicine m = new Medicine();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("name"));
                m.setDosage(rs.getString("dosage"));
                m.setFrequency(rs.getString("frequency"));
                m.setInstructions(rs.getString("instructions"));
                medicines.add(m);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving medicines", e);
        }
        return medicines;
    }

    /**
     * Get medicine by ID
     */
    public Medicine getMedicineById(int id) {
        String sql = "SELECT * FROM medicines WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Medicine m = new Medicine();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("name"));
                m.setDosage(rs.getString("dosage"));
                m.setFrequency(rs.getString("frequency"));
                m.setInstructions(rs.getString("instructions"));
                return m;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving medicine by id", e);
        }
        return null;
    }

    /**
     * Update medicine
     */
    public boolean updateMedicine(Medicine medicine) {
        String sql = "UPDATE medicines SET name = ?, dosage = ?, frequency = ?, instructions = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setString(2, medicine.getDosage());
            pstmt.setString(3, medicine.getFrequency());
            pstmt.setString(4, medicine.getInstructions());
            pstmt.setInt(5, medicine.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating medicine", e);
        }
        return false;
    }

    /**
     * Delete medicine and related reminders/inventory/history
     */
    public boolean deleteMedicine(int id) {
        String sql = "DELETE FROM medicines WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting medicine", e);
        }
        return false;
    }

    // ============= REMINDER OPERATIONS =============

    /**
     * Add new reminder
     */
    public int addReminder(Reminder reminder) {
        String sql = "INSERT INTO reminders(medicine_id, medicine_name, time, reminder_type) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Legacy DB compatibility: we no longer store medicineId/type; write minimal info
            pstmt.setInt(1, 0);
            pstmt.setString(2, reminder.getMedicineName());
            pstmt.setString(3, reminder.getReminderTime() != null ? reminder.getReminderTime().toString() : "");
            pstmt.setString(4, reminder.getStatus() != null ? reminder.getStatus().name() : "PENDING");
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error adding reminder", e);
        }
        return -1;
    }

    /**
     * Get all reminders
     */
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        String sql = "SELECT * FROM reminders ORDER BY time";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Reminder r = new Reminder();
                r.setId(rs.getInt("id"));
                r.setMedicineName(rs.getString("medicine_name"));
                r.setReminderTime(parseReminderDateTime(rs.getString("time")));
                String status = rs.getString("reminder_type");
                try {
                    r.setStatus(Reminder.Status.valueOf(status));
                } catch (Exception ex) {
                    r.setStatus(Reminder.Status.PENDING);
                }
                reminders.add(r);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving reminders", e);
        }
        return reminders;
    }

    /**
     * Get reminders by medicine ID
     */
    public List<Reminder> getRemindersByMedicineId(int medicineId) {
        List<Reminder> reminders = new ArrayList<>();
        String sql = "SELECT * FROM reminders WHERE medicine_id = ? ORDER BY time";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medicineId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reminder r = new Reminder();
                r.setId(rs.getInt("id"));
                r.setMedicineName(rs.getString("medicine_name"));
                r.setReminderTime(parseReminderDateTime(rs.getString("time")));
                String status = rs.getString("reminder_type");
                try {
                    r.setStatus(Reminder.Status.valueOf(status));
                } catch (Exception ex) {
                    r.setStatus(Reminder.Status.PENDING);
                }
                reminders.add(r);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving reminders by medicine id", e);
        }
        return reminders;
    }

    /**
     * Parse reminder time stored as either full LocalDateTime ISO string or HH:mm legacy time.
     */
    private LocalDateTime parseReminderDateTime(String value) {
        if (value == null || value.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            if (value.length() <= 5 && !value.contains("T")) {
                // Legacy format like "07:00" -> assume today at that time
                return LocalDate.now().atTime(java.time.LocalTime.parse(value));
            }
            return LocalDateTime.parse(value);
        } catch (Exception ex) {
            logger.warn("Failed to parse reminder time '{}', defaulting to now", value, ex);
            return LocalDateTime.now();
        }
    }

    /**
     * Update reminder
     */
    public boolean updateReminder(Reminder reminder) {
        String sql = "UPDATE reminders SET medicine_id = ?, medicine_name = ?, time = ?, reminder_type = ?, taken = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, 0);
            pstmt.setString(2, reminder.getMedicineName());
            pstmt.setString(3, reminder.getReminderTime() != null ? reminder.getReminderTime().toString() : "");
            pstmt.setString(4, reminder.getStatus() != null ? reminder.getStatus().name() : "PENDING");
            pstmt.setBoolean(5, reminder.getStatus() == Reminder.Status.TAKEN);
            pstmt.setInt(6, reminder.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating reminder", e);
        }
        return false;
    }

    /**
     * Delete reminder
     */
    public boolean deleteReminder(int id) {
        String sql = "DELETE FROM reminders WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting reminder", e);
        }
        return false;
    }

    // ============= INVENTORY OPERATIONS =============

    /**
     * Add new inventory item
     */
    public int addInventory(Inventory inventory) {
        String sql = "INSERT INTO inventory(medicine_id, medicine_name, quantity, threshold, daily_usage, last_refill_date) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, inventory.getMedicineId());
            pstmt.setString(2, inventory.getMedicineName());
            pstmt.setInt(3, inventory.getQuantity());
            pstmt.setInt(4, inventory.getThreshold());
            pstmt.setInt(5, inventory.getDailyUsage());
            pstmt.setDate(6, Date.valueOf(inventory.getLastRefillDate()));
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error adding inventory", e);
        }
        return -1;
    }

    /**
     * Get all inventory
     */
    public List<Inventory> getAllInventory() {
        List<Inventory> inventories = new ArrayList<>();
        String sql = "SELECT * FROM inventory ORDER BY medicine_name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Inventory inv = new Inventory();
                inv.setId(rs.getInt("id"));
                inv.setMedicineId(rs.getInt("medicine_id"));
                inv.setMedicineName(rs.getString("medicine_name"));
                inv.setQuantity(rs.getInt("quantity"));
                inv.setThreshold(rs.getInt("threshold"));
                inv.setDailyUsage(rs.getInt("daily_usage"));
                inv.setLastRefillDate(rs.getDate("last_refill_date").toLocalDate());
                inventories.add(inv);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving inventory", e);
        }
        return inventories;
    }

    /**
     * Get inventory by medicine ID
     */
    public Inventory getInventoryByMedicineId(int medicineId) {
        String sql = "SELECT * FROM inventory WHERE medicine_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medicineId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Inventory inv = new Inventory();
                inv.setId(rs.getInt("id"));
                inv.setMedicineId(rs.getInt("medicine_id"));
                inv.setMedicineName(rs.getString("medicine_name"));
                inv.setQuantity(rs.getInt("quantity"));
                inv.setThreshold(rs.getInt("threshold"));
                inv.setDailyUsage(rs.getInt("daily_usage"));
                inv.setLastRefillDate(rs.getDate("last_refill_date").toLocalDate());
                return inv;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving inventory by medicine id", e);
        }
        return null;
    }

    /**
     * Update inventory
     */
    public boolean updateInventory(Inventory inventory) {
        String sql = "UPDATE inventory SET medicine_id = ?, medicine_name = ?, quantity = ?, threshold = ?, daily_usage = ?, last_refill_date = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, inventory.getMedicineId());
            pstmt.setString(2, inventory.getMedicineName());
            pstmt.setInt(3, inventory.getQuantity());
            pstmt.setInt(4, inventory.getThreshold());
            pstmt.setInt(5, inventory.getDailyUsage());
            pstmt.setDate(6, Date.valueOf(inventory.getLastRefillDate()));
            pstmt.setInt(7, inventory.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating inventory", e);
        }
        return false;
    }

    /**
     * Delete inventory
     */
    public boolean deleteInventory(int id) {
        String sql = "DELETE FROM inventory WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting inventory", e);
        }
        return false;
    }

    // ============= DOSE HISTORY OPERATIONS =============

    /**
     * Add dose history record
     */
    public int addDoseHistory(DoseHistory history) {
        String sql = "INSERT INTO dose_history(medicine_id, reminder_id, medicine_name, date, time, status, notes) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, history.getMedicineId());
            pstmt.setInt(2, history.getReminderId());
            pstmt.setString(3, history.getMedicineName());
            pstmt.setDate(4, Date.valueOf(history.getDate()));
            pstmt.setString(5, history.getTime() != null ? history.getTime().toString() : null);
            pstmt.setString(6, history.getStatus());
            pstmt.setString(7, history.getNotes());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error adding dose history", e);
        }
        return -1;
    }

    /**
     * Get all dose history
     */
    public List<DoseHistory> getAllDoseHistory() {
        List<DoseHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM dose_history ORDER BY date DESC, time DESC";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DoseHistory h = new DoseHistory();
                h.setId(rs.getInt("id"));
                h.setMedicineId(rs.getInt("medicine_id"));
                h.setReminderId(rs.getInt("reminder_id"));
                h.setMedicineName(rs.getString("medicine_name"));
                h.setDate(rs.getDate("date").toLocalDate());
                String timeStr = rs.getString("time");
                if (timeStr != null) {
                    h.setTime(LocalTime.parse(timeStr));
                }
                h.setStatus(rs.getString("status"));
                h.setNotes(rs.getString("notes"));
                histories.add(h);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving dose history", e);
        }
        return histories;
    }

    /**
     * Get history by date range
     */
    public List<DoseHistory> getHistoryByDateRange(LocalDate startDate, LocalDate endDate) {
        List<DoseHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM dose_history WHERE date BETWEEN ? AND ? ORDER BY date DESC, time DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DoseHistory h = new DoseHistory();
                h.setId(rs.getInt("id"));
                h.setMedicineId(rs.getInt("medicine_id"));
                h.setReminderId(rs.getInt("reminder_id"));
                h.setMedicineName(rs.getString("medicine_name"));
                h.setDate(rs.getDate("date").toLocalDate());
                String timeStr = rs.getString("time");
                if (timeStr != null) {
                    h.setTime(LocalTime.parse(timeStr));
                }
                h.setStatus(rs.getString("status"));
                h.setNotes(rs.getString("notes"));
                histories.add(h);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving history by date range", e);
        }
        return histories;
    }

    /**
     * Get history by medicine ID
     */
    public List<DoseHistory> getHistoryByMedicineId(int medicineId) {
        List<DoseHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM dose_history WHERE medicine_id = ? ORDER BY date DESC, time DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medicineId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DoseHistory h = new DoseHistory();
                h.setId(rs.getInt("id"));
                h.setMedicineId(rs.getInt("medicine_id"));
                h.setReminderId(rs.getInt("reminder_id"));
                h.setMedicineName(rs.getString("medicine_name"));
                h.setDate(rs.getDate("date").toLocalDate());
                String timeStr = rs.getString("time");
                if (timeStr != null) {
                    h.setTime(LocalTime.parse(timeStr));
                }
                h.setStatus(rs.getString("status"));
                h.setNotes(rs.getString("notes"));
                histories.add(h);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving history by medicine id", e);
        }
        return histories;
    }

    /**
     * Update dose history
     */
    public boolean updateDoseHistory(DoseHistory history) {
        String sql = "UPDATE dose_history SET medicine_id = ?, reminder_id = ?, medicine_name = ?, date = ?, time = ?, status = ?, notes = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, history.getMedicineId());
            pstmt.setInt(2, history.getReminderId());
            pstmt.setString(3, history.getMedicineName());
            pstmt.setDate(4, Date.valueOf(history.getDate()));
            pstmt.setString(5, history.getTime() != null ? history.getTime().toString() : null);
            pstmt.setString(6, history.getStatus());
            pstmt.setString(7, history.getNotes());
            pstmt.setInt(8, history.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating dose history", e);
        }
        return false;
    }

    /**
     * Delete dose history
     */
    public boolean deleteDoseHistory(int id) {
        String sql = "DELETE FROM dose_history WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting dose history", e);
        }
        return false;
    }

    /**
     * Get adherence percentage for a medicine in a given date range
     */
    public double getAdherencePercentage(int medicineId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(CASE WHEN status = ? THEN 1 END) as taken, COUNT(*) as total " +
                "FROM dose_history WHERE medicine_id = ? AND date BETWEEN ? AND ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, DoseHistory.STATUS_TAKEN);
            pstmt.setInt(2, medicineId);
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int taken = rs.getInt("taken");
                int total = rs.getInt("total");
                return total > 0 ? (taken * 100.0) / total : 0;
            }
        } catch (SQLException e) {
            logger.error("Error calculating adherence percentage", e);
        }
        return 0;
    }
}
