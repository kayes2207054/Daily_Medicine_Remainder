# DailyDose - Personal Medicine Companion
## Complete JavaFX Implementation

### ✅ TECHNOLOGY STACK
- **JavaFX 17** with FXML (NO Swing)
- **Maven** build system
- **SQLite** database
- **MVC** architecture
- **SLF4J + Logback** logging

---

## 🎯 FEATURES IMPLEMENTED

### 1. USER ROLES
- ✅ **Patient Role**: Manage medicines, set reminders, track doses
- ✅ **Guardian Role**: Monitor patients, view compliance, receive notifications

### 2. LOGIN SYSTEM
- ✅ Role-based authentication (Patient/Guardian)
- ✅ Password hashing (SHA-256)
- ✅ Session management
- ✅ Registration for new users

### 3. PATIENT FEATURES
✅ **Dashboard**
  - Statistics: Total medicines, pending reminders, today's taken/missed
  - Recent activity (last 7 days)

✅ **Medicines Management**
  - Add/Edit/Delete medicines
  - Search functionality
  - Track dosage, frequency, instructions

✅ **Reminders System**
  - Add/Edit/Delete reminders
  - Real-time alarm checking (every second)
  - Status: PENDING, TAKEN, MISSED

✅ **Alarm Popup**
  - **Triggers at exact time**
  - **System beep sound** (high volume)
  - **3 action buttons**:
    - ✓ TAKEN
    - ✗ MISSED
    - ⏹ STOP ALARM
  - Auto-saves with date, time, medicine name, status
  - **Notifies guardians** automatically

✅ **History**
  - View all dose history
  - Filter by date range
  - Color-coded status

### 4. GUARDIAN FEATURES
✅ **Patient Management**
  - Link patients by username
  - View linked patients
  - Patient adherence percentage

✅ **Patient Monitoring**
  - Full dose history per patient
  - Statistics: Total/Taken/Missed doses
  - Date-wise filtering
  - Compliance status

✅ **Notifications**
  - Real-time notifications when patient takes/misses dose
  - Unread count badge
  - Mark as read functionality
  - Notification details with timestamps

---

## 📁 PROJECT STRUCTURE

```
src/main/
├── java/com/example/
│   ├── DailyDoseApp.java              # Main JavaFX Application
│   ├── controller/
│   │   ├── MedicineController.java
│   │   ├── ReminderController.java
│   │   ├── HistoryController.java
│   │   └── UserController.java        # User & Guardian management
│   ├── model/
│   │   ├── Medicine.java
│   │   ├── Reminder.java
│   │   ├── DoseHistory.java
│   │   ├── User.java                  # Patient/Guardian roles
│   │   ├── GuardianPatientLink.java
│   │   └── Notification.java
│   ├── database/
│   │   └── DatabaseManager.java       # SQLite operations
│   └── viewfx/                        # JavaFX Controllers
│       ├── LoginController.java
│       ├── RegistrationController.java
│       ├── PatientDashboardController.java
│       ├── GuardianDashboardController.java
│       └── AlarmService.java          # Real-time alarm system
├── resources/
│   ├── fxml/
│   │   ├── Login.fxml
│   │   ├── Registration.fxml
│   │   ├── PatientDashboard.fxml
│   │   └── GuardianDashboard.fxml
│   ├── css/
│   │   └── style.css                  # Modern styling
│   └── sounds/
│       └── (place alarm.mp3 here - optional)
```

---

## 🚀 HOW TO RUN

### Method 1: Using Batch File (Easiest)
```bash
Double-click: RUN_APP.bat
```

### Method 2: Using Maven Command
```bash
mvn clean javafx:run
```

### Method 3: IntelliJ IDEA
1. Open project in IntelliJ
2. Right-click on `DailyDoseApp.java`
3. Run 'DailyDoseApp.main()'

---

## 👤 DEFAULT LOGIN CREDENTIALS

### Patient Account
```
Username: admin
Password: admin123
Role: Select "Patient"
```

### Guardian Account
```
Username: guardian
Password: guard123
Role: Select "Guardian"
```

### Create New Account
Click "Register Here" on login screen

---

## 📊 DATABASE TABLES

1. **users** - Patient & Guardian accounts
2. **medicines** - Medicine records
3. **reminders** - Scheduled reminders
4. **dose_history** - Patient intake records
5. **guardian_patient_links** - Guardian-Patient relationships
6. **notifications** - Guardian notifications
7. **inventory** - Stock tracking

---

## ⏰ ALARM SYSTEM DETAILS

### How It Works (জাভাএফএক্স টাইমলাইন)
1. **Timeline checks every 1 second**
2. Compares current time with reminder time
3. When match found:
   - **System beep plays** (high volume)
   - **Popup appears** (always on top)
   - Patient chooses: TAKEN / MISSED / STOP
4. Action saved to database with timestamp
5. **Guardian notified immediately**

### Alarm Features
- ✅ Real system time checking
- ✅ High volume beep sound
- ✅ Modal dialog (must respond)
- ✅ Three clear action buttons
- ✅ Auto-saves to history
- ✅ Guardian notification

---

## 🛡️ GUARDIAN NOTIFICATION FLOW

1. **Patient Action**: Patient takes or misses medicine
2. **Automatic Trigger**: AlarmService detects action
3. **Notification Created**: Stored in database
4. **Guardian View**: Notification appears in Guardian Dashboard
5. **Details Shown**:
   - Patient name
   - Medicine name
   - Action (Taken/Missed)
   - Date & Time
   - Status (Read/Unread)

---

## 🎨 UI HIGHLIGHTS

- **Modern gradient backgrounds**
- **Color-coded status** (Green=Taken, Red=Missed, Yellow=Pending)
- **Responsive tables** with sorting
- **Clean tab-based navigation**
- **Real-time statistics** updates
- **Professional styling** (CSS-based)
- **Bengali comments** for important logic

---

## 🔧 MAVEN COMMANDS

```bash
# Build project
mvn clean install

# Run application
mvn javafx:run

# Create executable JAR
mvn package

# Run tests
mvn test
```

---

## 📝 CODE COMMENTS

All critical logic includes:
- **English explanations**
- **Bengali (বাংলা) comments** for complex parts
- Student-friendly explanations

Example:
```java
/**
 * Start alarm checking service
 * অ্যালার্ম চেকিং সার্ভিস শুরু করুন
 */
public void start() {
    // Timeline checks every second
    timeline = new Timeline(new KeyFrame(Duration.seconds(1), 
        event -> checkReminders()));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
}
```

---

## ✅ VERIFICATION CHECKLIST

- [x] JavaFX + FXML only (NO Swing)
- [x] Patient & Guardian roles working
- [x] Login with role selection
- [x] Registration system
- [x] Medicine CRUD operations
- [x] Reminder CRUD operations
- [x] **Real-time alarm system** (Timeline-based)
- [x] **System beep sound**
- [x] **Alarm popup with 3 buttons**
- [x] **Action saves to history**
- [x] **Guardian notifications**
- [x] Patient compliance tracking
- [x] Date-wise history view
- [x] Guardian dashboard with patient list
- [x] Notification badge system
- [x] Database persistence
- [x] Professional UI styling
- [x] Bengali + English comments

---

## 🎓 FOR TEACHER DEMONSTRATION

### Show These Features:

1. **Login System**
   - Login as Patient (admin/admin123)
   - Login as Guardian (guardian/guard123)

2. **Patient Flow**
   - Add a medicine
   - Set a reminder (current time + 1 minute)
   - **Wait for alarm to ring**
   - **Show popup with beep**
   - Click "TAKEN" button
   - Verify saved in History tab

3. **Guardian Flow**
   - Login as Guardian
   - Link patient "admin" (if not linked)
   - View patient's dose history
   - **Check Notifications tab**
   - See notification about patient action
   - View adherence percentage

4. **Database Persistence**
   - Close and reopen app
   - All data still present

---

## 🐛 TROUBLESHOOTING

### Error: "Module not found"
```bash
Solution: Run `mvn clean install` first
```

### Alarm not ringing?
- Check reminder time is in future
- Verify status is "PENDING"
- System time must match

### Notifications not showing?
- Patient and Guardian must be linked
- Check Notifications tab
- Refresh using 🔄 button

---

## 📚 ACADEMIC NOTES

This project demonstrates:
- **JavaFX Application** architecture
- **FXML** for UI design
- **MVC pattern** (Model-View-Controller)
- **Database operations** with JDBC
- **Real-time scheduling** with Timeline
- **Event-driven programming**
- **User authentication** & authorization
- **Relational data modeling**
- **CSS styling** for modern UX

---

**Developed by: Kayes Ahmed**  
**Course: Advanced Java Lab**  
**Date: January 2026**  
**Technology: JavaFX + FXML + SQLite + Maven**

---

## 🎯 READY FOR VIVA

**Key Points to Explain:**
1. Why JavaFX over Swing? (Modern, FXML separation, better styling)
2. How alarm system works? (Timeline checks every second, triggers popup)
3. How guardian notifications work? (Patient action → Database → Guardian view)
4. Database schema? (7 tables with relationships)
5. MVC architecture? (Models, ViewFX controllers, Business controllers)
6. Authentication flow? (SHA-256 hashing, role-based access)

**Ready to run and demonstrate! 🚀**
