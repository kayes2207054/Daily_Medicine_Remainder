# DailyDose - Your Personal Medicine Companion

A comprehensive Java desktop application for managing daily medicines, setting reminders, tracking inventory, and monitoring medication adherence.

## Features

### 1. Medicine Management
- Add, edit, delete, and search medicines
- Track dosage, frequency, and special instructions
- Sort medicines by name or frequency
- Full CRUD operations with database persistence

### 2. Reminder System
- Set multiple reminders for each medicine
- Predefined reminder times (morning, noon, evening) or custom times
- Desktop notifications when reminder time matches
- Track which doses have been taken
- Visual reminder dashboard

### 3. Inventory Management
- Track pill quantities for each medicine
- Set low-stock warning thresholds
- Automatic refill date estimation based on usage patterns
- Low-stock alerts and notifications
- Inventory history and updates

### 4. Dose Tracking & History
- Record each dose as Taken, Missed, or Pending
- Detailed history with dates and times
- Adherence percentage calculations
- Daily, weekly, and monthly history views
- Notes for missed doses

### 5. User Interface
- Clean, intuitive Swing-based GUI
- Responsive navigation with CardLayout
- Statistics dashboard on home screen
- Dialog-based forms for data entry
- Professional color scheme

### 6. Data Storage
- SQLite database for persistent storage
- Automatic database initialization on first run
- Sample data included for testing
- Backup and export functionality

### 7. Additional Features
- Form validation
- Search and filter capabilities
- Automatic sorting options
- CSV export for history
- JSON serialization support
- Comprehensive logging

## Project Structure

```
Daily_Medicine_Remainder/
├── src/main/java/com/example/
│   ├── Main.java                    # Application entry point
│   ├── controller/
│   │   ├── MedicineController.java  # Medicine CRUD operations
│   │   ├── ReminderController.java  # Reminder management
│   │   ├── InventoryController.java # Inventory tracking
│   │   └── HistoryController.java   # History and statistics
│   ├── model/
│   │   ├── Medicine.java            # Medicine entity
│   │   ├── Reminder.java            # Reminder entity
│   │   ├── Inventory.java           # Inventory entity
│   │   └── DoseHistory.java         # History entity
│   ├── view/
│   │   ├── MainFrame.java           # Main application window
│   │   ├── MedicinePanel.java       # Medicine management UI
│   │   ├── ReminderPanel.java       # Reminder management UI
│   │   ├── InventoryPanel.java      # Inventory management UI
│   │   └── HistoryPanel.java        # History and reports UI
│   ├── database/
│   │   └── DatabaseManager.java     # SQLite database operations
│   └── utils/
│       ├── NotificationService.java # Notification and scheduling
│       ├── DateTimeUtils.java       # Date/time utilities
│       ├── FileUtils.java           # File and CSV operations
│       └── DataSeeder.java          # Sample data initialization
├── src/main/resources/
│   └── logback.xml                  # Logging configuration
└── pom.xml                          # Maven configuration
```

## Architecture

### MVC Pattern
- **Model**: Domain objects (Medicine, Reminder, Inventory, DoseHistory)
- **View**: Swing panels for UI (MainFrame, MedicinePanel, ReminderPanel, InventoryPanel, HistoryPanel)
- **Controller**: Business logic controllers managing data flow and operations

### Database Design
- **medicines**: Stores medicine information
- **reminders**: Stores reminder times and states
- **inventory**: Tracks pill quantities and refill dates
- **dose_history**: Records all dose events

## Dependencies

- **SQLite JDBC**: Database connectivity (`org.xerial:sqlite-jdbc:3.44.0.0`)
- **GSON**: JSON serialization (`com.google.code.gson:gson:2.10.1`)
- **JFreeChart**: Charts and graphs (`org.jfree:jfreechart:1.5.3`)
- **Apache Commons CSV**: CSV file handling (`org.apache.commons:commons-csv:1.10.0`)
- **SLF4J + Logback**: Logging framework
- **JUnit**: Unit testing

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Installation

1. Clone or download the project
2. Navigate to the project directory:
   ```bash
   cd Daily_Medicine_Remainder
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.Main"
   ```

Or directly run the JAR:
   ```bash
   java -jar target/Daily_Medicine_Remainder-1.0-SNAPSHOT.jar
   ```

## Usage

### Adding a Medicine
1. Go to "Medicines" tab
2. Click "Add Medicine"
3. Fill in name, dosage, frequency, and instructions
4. Click "Save"

### Setting Reminders
1. Go to "Reminders" tab
2. Click "Add Reminder"
3. Select medicine, set time, choose reminder type
4. Click "Save"

### Managing Inventory
1. Go to "Inventory" tab
2. Click "Add Item" to add new inventory
3. Enter medicine name, quantity, and threshold
4. Update quantity as needed

### Viewing History
1. Go to "History" tab
2. Filter by date range (Today, This Week, This Month)
3. View adherence statistics and dose records

## Configuration

### Logging
Edit `src/main/resources/logback.xml` to configure logging levels and output.

### Database
The application automatically creates `daily_dose.db` in the working directory on first run.

### Sample Data
Sample medicines and reminders are automatically loaded on first application run.

## Features Implementation Details

### Notification System
- Checks reminder times every minute
- Shows desktop notifications matching reminder times
- Supports immediate marking of doses as taken

### Adherence Calculation
- Calculates percentage of doses taken vs. expected
- Supports date range filtering
- Per-medicine adherence tracking

### Inventory Forecasting
- Estimates refill date based on daily usage
- Warns when stock falls below threshold
- Tracks refill history

## Future Enhancements

- Doctor integration APIs
- Wearable device support
- Multi-user/family accounts
- Cloud synchronization
- Mobile app companion
- Voice-based reminders
- Advanced analytics and reporting
- Medication interaction checker
- Prescription scanning

## Code Quality

- Clean Code principles followed
- Comprehensive documentation and comments
- Proper exception handling
- Logger integration for debugging
- Form validation
- Consistent naming conventions

## License

This project is provided as-is for educational and personal use.

## Support

For issues or questions, please refer to the code comments and documentation.

---

**Version**: 1.0-SNAPSHOT  
**Last Updated**: December 2025  
**Language**: Java 11+  
**GUI Framework**: Swing  
**Database**: SQLite
