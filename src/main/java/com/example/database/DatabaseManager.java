package com.example.database;

import com.example.model.DoseHistory;
import com.example.model.Medicine;
import com.example.model.Schedule;
import com.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager Class
 * Handles all SQLite database operations for DailyDose application.
 * Uses JDBC connection pooling with a singleton pattern.
 * Refactored for normalized schema and enhanced dosage tracking.
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_URL = "jdbc:sqlite:daily_dose_v2.db";
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
     * Initialize database tables
     */
    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Enable foreign keys
            stmt.execute("PRAGMA foreign_keys = ON;");

            // 1. Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password_hash TEXT NOT NULL," +
                    "role TEXT NOT NULL DEFAULT 'PATIENT'," + 
                    "full_name TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // 2. Medicines table (Normalized: Name and Stock only)
            stmt.execute("CREATE TABLE IF NOT EXISTS medicines (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT UNIQUE NOT NULL," +
                    "stock_quantity INTEGER DEFAULT 0," + // Current physical stock
                    "low_stock_threshold INTEGER DEFAULT 10," +
                    "dose_unit TEXT," + // e.g., tablet, ml, pill
                    "instructions TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // 3. Medicine Schedules (Dosage & Frequency)
            // One medicine can have multiple schedules (e.g. Morning-BeforeMeal AND Night-AfterMeal)
            stmt.execute("CREATE TABLE IF NOT EXISTS medicine_schedules (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medicine_id INTEGER NOT NULL," +
                    "time_of_day TEXT NOT NULL," + // MORNING, NOON, NIGHT, CUSTOM
                    "meal_timing TEXT NOT NULL," + // BEFORE_MEAL, AFTER_MEAL, NONE
                    "dose_amount REAL DEFAULT 1.0," + // How much to take at this time
                    "custom_time TEXT," + // Specific time if needed example "10:00"
                    "custom_note TEXT," + // For "Custom" option text
                    "FOREIGN KEY(medicine_id) REFERENCES medicines(id) ON DELETE CASCADE)");

            // 4. Dose History (Tracking actual intake)
            stmt.execute("CREATE TABLE IF NOT EXISTS dose_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medicine_id INTEGER NOT NULL," +
                    "scheduled_time TIMESTAMP," + // When it was supposed to be taken
                    "taken_time TIMESTAMP," + // When it was actually taken
                    "status TEXT NOT NULL," + // TAKEN, MISSED, SKIPPED
                    "notes TEXT," +
                    "FOREIGN KEY(medicine_id) REFERENCES medicines(id) ON DELETE SET NULL)");
            
            // 5. Inventory Transaction Logs (Audit trail)
            stmt.execute("CREATE TABLE IF NOT EXISTS inventory_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medicine_id INTEGER NOT NULL," +
                    "change_amount INTEGER NOT NULL," + // +50 or -1
                    "reason TEXT," + // "Refill", "Dose Taken", "Adjustment"
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(medicine_id) REFERENCES medicines(id) ON DELETE CASCADE)");

            logger.info("Database tables initialized successfully (V2 Schema)");
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
        }
    }

    // ============= MEDICINE OPERATIONS =============

    public int addMedicine(Medicine medicine) {
        String sql = "INSERT INTO medicines(name, stock_quantity, low_stock_threshold, dose_unit, instructions) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setInt(2, medicine.getStockQuantity());
            pstmt.setInt(3, medicine.getLowStockThreshold());
            pstmt.setString(4, medicine.getDoseUnit());
            pstmt.setString(5, medicine.getInstructions());
            pstmt.executeUpdate();
            
            try (Statement stmt = connection.createStatement();
                 ResultSet keyRs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (keyRs.next()) {
                    int medId = keyRs.getInt(1);
                
                // Add Schedules
                if (medicine.getSchedules() != null) {
                    addSchedules(medId, medicine.getSchedules());
                }
                // Log Initial Inventory
                if(medicine.getStockQuantity() > 0) {
                    logInventoryChange(medId, medicine.getStockQuantity(), "Initial Stock");
                }
                
                return medId;
            }
          }
        } catch (SQLException e) {
            logger.error("Error adding medicine", e);
        }
        return -1;
    }

    private void addSchedules(int medicineId, List<Schedule> schedules) {
        String sql = "INSERT INTO medicine_schedules(medicine_id, time_of_day, meal_timing, dose_amount, custom_note) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Schedule s : schedules) {
                pstmt.setInt(1, medicineId);
                pstmt.setString(2, s.getTimeOfDay());
                pstmt.setString(3, s.getMealTiming());
                pstmt.setDouble(4, s.getDoseAmount());
                pstmt.setString(5, s.getCustomNote());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Error adding schedules", e);
        }
    }

    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines ORDER BY name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Medicine m = mapResultSetToMedicine(rs);
                m.setSchedules(getSchedulesForMedicine(m.getId()));
                medicines.add(m);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving medicines", e);
        }
        return medicines;
    }

    public List<Medicine> searchMedicines(String query, String frequencyFilter, String mealFilter) {
        List<Medicine> medicines = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT m.* FROM medicines m ");
        sql.append("LEFT JOIN medicine_schedules s ON m.id = s.medicine_id WHERE 1=1 ");

        if (query != null && !query.isEmpty()) {
            sql.append("AND (m.name LIKE ? OR m.instructions LIKE ?) ");
        }
        if (frequencyFilter != null && !frequencyFilter.isEmpty()) {
             sql.append("AND s.time_of_day = ? ");
        }
        if (mealFilter != null && !mealFilter.isEmpty()) {
             sql.append("AND s.meal_timing = ? ");
        }
        
        sql.append("ORDER BY m.name");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (query != null && !query.isEmpty()) {
                pstmt.setString(index++, "%" + query + "%");
                pstmt.setString(index++, "%" + query + "%");
            }
            if (frequencyFilter != null && !frequencyFilter.isEmpty()) {
                pstmt.setString(index++, frequencyFilter);
            }
            if (mealFilter != null && !mealFilter.isEmpty()) {
                pstmt.setString(index++, mealFilter);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Medicine m = mapResultSetToMedicine(rs);
                m.setSchedules(getSchedulesForMedicine(m.getId()));
                medicines.add(m);
            }
        } catch (SQLException e) {
            logger.error("Error filtering medicines", e);
        }
        return medicines;
    }

    private Medicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("name"));
        m.setStockQuantity(rs.getInt("stock_quantity"));
        m.setLowStockThreshold(rs.getInt("low_stock_threshold"));
        m.setDoseUnit(rs.getString("dose_unit"));
        m.setInstructions(rs.getString("instructions"));
        return m;
    }

    private List<Schedule> getSchedulesForMedicine(int medicineId) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM medicine_schedules WHERE medicine_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medicineId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Schedule s = new Schedule();
                s.setId(rs.getInt("id"));
                s.setMedicineId(rs.getInt("medicine_id"));
                s.setTimeOfDay(rs.getString("time_of_day"));
                s.setMealTiming(rs.getString("meal_timing"));
                s.setDoseAmount(rs.getDouble("dose_amount"));
                s.setCustomNote(rs.getString("custom_note"));
                schedules.add(s);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving schedules", e);
        }
        return schedules;
    }

    public boolean updateMedicine(Medicine medicine) {
        String sql = "UPDATE medicines SET name = ?, stock_quantity = ?, low_stock_threshold = ?, dose_unit = ?, instructions = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setInt(2, medicine.getStockQuantity());
            pstmt.setInt(3, medicine.getLowStockThreshold());
            pstmt.setString(4, medicine.getDoseUnit());
            pstmt.setString(5, medicine.getInstructions());
            pstmt.setInt(6, medicine.getId());
            
            int affected = pstmt.executeUpdate();
            if(affected > 0) {
                // Update schedules: Delete all and re-insert (Simplest approach for 1:N update)
                deleteSchedules(medicine.getId());
                addSchedules(medicine.getId(), medicine.getSchedules());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating medicine", e);
        }
        return false;
    }
    
    public boolean updateStock(int medicineId, int newQuantity, String reason) {
         try {
             // Get current stock
             Medicine m = getMedicineById(medicineId);
             if(m == null) return false;
             
             int diff = newQuantity - m.getStockQuantity();
             
             String sql = "UPDATE medicines SET stock_quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
             try(PreparedStatement pstmt = connection.prepareStatement(sql)) {
                 pstmt.setInt(1, newQuantity);
                 pstmt.setInt(2, medicineId);
                 if(pstmt.executeUpdate() > 0) {
                     logInventoryChange(medicineId, diff, reason);
                     return true;
                 }
             }
         } catch(SQLException e) {
             logger.error("Error updating stock", e);
         }
         return false;
    }
    
    private void logInventoryChange(int medicineId, int changeAmount, String reason) {
        String sql = "INSERT INTO inventory_logs(medicine_id, change_amount, reason) VALUES(?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medicineId);
            pstmt.setInt(2, changeAmount);
            pstmt.setString(3, reason);
            pstmt.executeUpdate();
        } catch(SQLException e) {
             logger.error("Error logging inventory", e);
        }
    }

    private void deleteSchedules(int medicineId) {
        String sql = "DELETE FROM medicine_schedules WHERE medicine_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medicineId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting schedules", e);
        }
    }

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
    
    public Medicine getMedicineById(int id) {
        String sql = "SELECT * FROM medicines WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                 Medicine m = mapResultSetToMedicine(rs);
                 m.setSchedules(getSchedulesForMedicine(id));
                 return m;
            }
        } catch (SQLException e) {
             logger.error("Error retrieving medicine", e);
        }
        return null;
    }

    // ============= USER OPERATIONS =============
    
    public User authenticateUser(String username, String password) {
         String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
         try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
             pstmt.setString(1, username);
             pstmt.setString(2, password); // Ideally text hashing
             ResultSet rs = pstmt.executeQuery();
             if(rs.next()) {
                 User u = new User();
                 u.setId(rs.getInt("id"));
                 u.setUsername(rs.getString("username"));
                 u.setRole(rs.getString("role"));
                 u.setFullName(rs.getString("full_name"));
                 return u;
             }
         } catch(SQLException e) {
             logger.error("Error authenticating", e);
         }
         return null;
    }
    
    public boolean registerUser(User user) {
         String sql = "INSERT INTO users(username, password_hash, role, full_name) VALUES(?, ?, ?, ?)";
         try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
             pstmt.setString(1, user.getUsername());
             pstmt.setString(2, user.getPassword()); // Storing raw/simple hash for now
             pstmt.setString(3, user.getRole());
             pstmt.setString(4, user.getFullName());
             return pstmt.executeUpdate() > 0;
         } catch(SQLException e) {
             logger.error("Error registering user", e);
         }
         return false;
    }

    // ============= HISTORY OPERATIONS =============
    public int addDoseHistory(DoseHistory history) {
        String sql = "INSERT INTO dose_history(medicine_id, scheduled_time, taken_time, status, notes) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, history.getMedicineId());
            pstmt.setString(2, history.getScheduledTime() != null ? history.getScheduledTime().toString() : null);
            pstmt.setString(3, history.getTakenTime() != null ? history.getTakenTime().toString() : null);
            pstmt.setString(4, history.getStatus());
            pstmt.setString(5, history.getNotes());
            pstmt.executeUpdate();
            
            try (Statement stmt = connection.createStatement();
                 ResultSet keyRs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (keyRs.next()) return keyRs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error adding dose history", e);
        }
        return -1;
    }

    public List<DoseHistory> getAllDoseHistory() {
        List<DoseHistory> list = new ArrayList<>();
        // Join with medicines to get name
        String sql = "SELECT h.*, m.name as medicine_name FROM dose_history h LEFT JOIN medicines m ON h.medicine_id = m.id ORDER BY h.scheduled_time DESC";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DoseHistory h = new DoseHistory();
                h.setId(rs.getInt("id"));
                h.setMedicineId(rs.getInt("medicine_id"));
                h.setMedicineName(rs.getString("medicine_name"));
                String sched = rs.getString("scheduled_time");
                if (sched != null) h.setScheduledTime(LocalDateTime.parse(sched));
                String taken = rs.getString("taken_time");
                if (taken != null) h.setTakenTime(LocalDateTime.parse(taken));
                h.setStatus(rs.getString("status"));
                h.setNotes(rs.getString("notes"));
                list.add(h);
            }
        } catch (SQLException e) {
            logger.error("Error getting history", e);
        }
        return list;
    }
}

