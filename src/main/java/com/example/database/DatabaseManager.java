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
     * Close database connection properly
     */
    public void closeConnection() {
        disconnect();
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
                    "quantity INTEGER DEFAULT 0," +
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

            // Users table (Patient/Guardian)
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password_hash TEXT NOT NULL," +
                    "role TEXT NOT NULL," +
                    "full_name TEXT," +
                    "email TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "active BOOLEAN DEFAULT 1)");

            // Guardian-Patient Links table
            stmt.execute("CREATE TABLE IF NOT EXISTS guardian_patient_links (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guardian_id INTEGER NOT NULL," +
                    "patient_id INTEGER NOT NULL," +
                    "linked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "active BOOLEAN DEFAULT 1," +
                    "FOREIGN KEY(guardian_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(patient_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "UNIQUE(guardian_id, patient_id))");

            // Notifications table
            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guardian_id INTEGER NOT NULL," +
                    "patient_id INTEGER NOT NULL," +
                    "type TEXT NOT NULL," +
                    "message TEXT NOT NULL," +
                    "details TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "read BOOLEAN DEFAULT 0," +
                    "FOREIGN KEY(guardian_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(patient_id) REFERENCES users(id) ON DELETE CASCADE)");

            // Migration: Add quantity column if it doesn't exist
            try {
                stmt.execute("ALTER TABLE medicines ADD COLUMN quantity INTEGER DEFAULT 0");
                logger.info("Added quantity column to medicines table");
            } catch (SQLException e) {
                // Column already exists, ignore
            }

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
        String sql = "INSERT INTO medicines(name, dosage, frequency, instructions, quantity) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setString(2, medicine.getDosage());
            pstmt.setString(3, medicine.getFrequency());
            pstmt.setString(4, medicine.getInstructions());
            pstmt.setInt(5, medicine.getQuantity());
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
                m.setQuantity(rs.getInt("quantity"));
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
                m.setQuantity(rs.getInt("quantity"));
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
        String sql = "UPDATE medicines SET name = ?, dosage = ?, frequency = ?, instructions = ?, quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setString(2, medicine.getDosage());
            pstmt.setString(3, medicine.getFrequency());
            pstmt.setString(4, medicine.getInstructions());
            pstmt.setInt(5, medicine.getQuantity());
            pstmt.setInt(6, medicine.getId());
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

    // ============= USER OPERATIONS =============

    /**
     * Add new user to database
     */
    public int addUser(User user) {
        String sql = "INSERT INTO users(username, password_hash, role, full_name, email) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole().toString());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getEmail());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error adding user", e);
        }
        return -1;
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setActive(rs.getBoolean("active"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    user.setCreatedAt(createdAt.toLocalDateTime());
                }
                return user;
            }
        } catch (SQLException e) {
            logger.error("Error getting user by username", e);
        }
        return null;
    }

    /**
     * Get user by ID
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setActive(rs.getBoolean("active"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    user.setCreatedAt(createdAt.toLocalDateTime());
                }
                return user;
            }
        } catch (SQLException e) {
            logger.error("Error getting user by id", e);
        }
        return null;
    }

    /**
     * Update user information
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET password_hash = ?, role = ?, full_name = ?, email = ?, active = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getPasswordHash());
            pstmt.setString(2, user.getRole().toString());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setBoolean(5, user.isActive());
            pstmt.setInt(6, user.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user", e);
        }
        return false;
    }

    // ============= GUARDIAN-PATIENT LINK OPERATIONS =============

    /**
     * Link a guardian to a patient
     */
    public int linkGuardianToPatient(int guardianId, int patientId) {
        String sql = "INSERT INTO guardian_patient_links(guardian_id, patient_id) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, guardianId);
            pstmt.setInt(2, patientId);
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error linking guardian to patient", e);
        }
        return -1;
    }

    /**
     * Get all patients linked to a guardian
     */
    public List<GuardianPatientLink> getPatientsByGuardianId(int guardianId) {
        List<GuardianPatientLink> links = new ArrayList<>();
        String sql = "SELECT gpl.*, u.username as patient_username, u.full_name as patient_full_name " +
                "FROM guardian_patient_links gpl " +
                "JOIN users u ON gpl.patient_id = u.id " +
                "WHERE gpl.guardian_id = ? AND gpl.active = 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guardianId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                GuardianPatientLink link = new GuardianPatientLink();
                link.setId(rs.getInt("id"));
                link.setGuardianId(rs.getInt("guardian_id"));
                link.setPatientId(rs.getInt("patient_id"));
                link.setPatientUsername(rs.getString("patient_username"));
                link.setPatientFullName(rs.getString("patient_full_name"));
                link.setActive(rs.getBoolean("active"));
                Timestamp linkedAt = rs.getTimestamp("linked_at");
                if (linkedAt != null) {
                    link.setLinkedAt(linkedAt.toLocalDateTime());
                }
                links.add(link);
            }
        } catch (SQLException e) {
            logger.error("Error getting patients by guardian id", e);
        }
        return links;
    }

    /**
     * Get all guardians linked to a patient
     */
    public List<GuardianPatientLink> getGuardiansByPatientId(int patientId) {
        List<GuardianPatientLink> links = new ArrayList<>();
        String sql = "SELECT gpl.*, u.username as guardian_username " +
                "FROM guardian_patient_links gpl " +
                "JOIN users u ON gpl.guardian_id = u.id " +
                "WHERE gpl.patient_id = ? AND gpl.active = 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                GuardianPatientLink link = new GuardianPatientLink();
                link.setId(rs.getInt("id"));
                link.setGuardianId(rs.getInt("guardian_id"));
                link.setPatientId(rs.getInt("patient_id"));
                link.setGuardianUsername(rs.getString("guardian_username"));
                link.setActive(rs.getBoolean("active"));
                Timestamp linkedAt = rs.getTimestamp("linked_at");
                if (linkedAt != null) {
                    link.setLinkedAt(linkedAt.toLocalDateTime());
                }
                links.add(link);
            }
        } catch (SQLException e) {
            logger.error("Error getting guardians by patient id", e);
        }
        return links;
    }

    /**
     * Unlink a guardian from a patient
     */
    public boolean unlinkGuardianFromPatient(int guardianId, int patientId) {
        String sql = "UPDATE guardian_patient_links SET active = 0 WHERE guardian_id = ? AND patient_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guardianId);
            pstmt.setInt(2, patientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error unlinking guardian from patient", e);
        }
        return false;
    }

    // ============= NOTIFICATION OPERATIONS =============

    /**
     * Add new notification
     */
    public int addNotification(Notification notification) {
        String sql = "INSERT INTO notifications(guardian_id, patient_id, type, message, details) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, notification.getGuardianId());
            pstmt.setInt(2, notification.getPatientId());
            pstmt.setString(3, notification.getType().toString());
            pstmt.setString(4, notification.getMessage());
            pstmt.setString(5, notification.getDetails());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error adding notification", e);
        }
        return -1;
    }

    /**
     * Get all notifications for a guardian
     */
    public List<Notification> getNotificationsByGuardianId(int guardianId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT n.*, u.full_name as patient_name " +
                "FROM notifications n " +
                "JOIN users u ON n.patient_id = u.id " +
                "WHERE n.guardian_id = ? ORDER BY n.created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guardianId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setGuardianId(rs.getInt("guardian_id"));
                notification.setPatientId(rs.getInt("patient_id"));
                notification.setPatientName(rs.getString("patient_name"));
                notification.setType(Notification.Type.valueOf(rs.getString("type")));
                notification.setMessage(rs.getString("message"));
                notification.setDetails(rs.getString("details"));
                notification.setRead(rs.getBoolean("read"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    notification.setCreatedAt(createdAt.toLocalDateTime());
                }
                notifications.add(notification);
            }
        } catch (SQLException e) {
            logger.error("Error getting notifications by guardian id", e);
        }
        return notifications;
    }

    /**
     * Get unread notification count for a guardian
     */
    public int getUnreadNotificationCount(int guardianId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE guardian_id = ? AND read = 0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guardianId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting unread notification count", e);
        }
        return 0;
    }

    /**
     * Mark notification as read
     */
    public boolean markNotificationAsRead(int notificationId) {
        String sql = "UPDATE notifications SET read = 1 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, notificationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error marking notification as read", e);
        }
        return false;
    }

    /**
     * Mark all notifications as read for a guardian
     */
    public boolean markAllNotificationsAsRead(int guardianId) {
        String sql = "UPDATE notifications SET read = 1 WHERE guardian_id = ? AND read = 0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, guardianId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error marking all notifications as read", e);
        }
        return false;
    }
}

