# 💊 DailyDose - Medicine Tracker & Reminder System

![Version](https://img.shields.io/badge/version-2.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)

**A comprehensive desktop application for managing medicines, setting reminders, tracking inventory, and monitoring adherence.**

---

## ✨ Features

### 🏠 Dashboard
- **Live Statistics**: Total medicines, pending reminders, doses taken/missed, adherence rate
- **Recent Activity**: Last 7 days dose history with color-coded status
- **Auto-refresh**: Updates every 30 seconds

### 💊 Medicine Management
- **Advanced Search**: Real-time search by name, dosage, or instructions
- **Smart Filtering**: Filter by frequency
- **Full CRUD**: Add, Edit, Delete medicines
- **Modern UI**: Color-coded table with emoji icons

### ⏰ Reminder System
- **Real-time Alarms**: Background service monitors reminders 24/7
- **Alarm Features**: Continuous beep, popup dialog, TAKEN/SNOOZE/MISS actions
- **Snooze Function**: 5-minute delay option
- **Auto-miss**: After 10 minutes if ignored

### 📦 Inventory & 📊 History
- Stock tracking with low stock alerts
- Complete dose history
- Adherence analytics

### ⚙️ Settings
- Database backup/restore
- Theme and language options

---

## 💻 System Requirements

- **Java**: JDK 8 or higher
- **OS**: Windows/macOS/Linux
- **RAM**: 512 MB recommended
- **Disk**: 50 MB

---

## 🚀 Quick Start

### Run Application
```bash
# Windows
START.bat

# Or using Java
java -cp target/classes com.example.Main
```

### First Time Login
```
Username: admin
Password: admin123
```

---

## 📖 User Guide

### Adding a Reminder
1. Go to **Reminders** tab
2. Click **➕ Add Reminder**
3. Enter medicine name, date (yyyy-MM-dd), time (HH:mm)
4. Click **💾 Save**

### When Alarm Rings
- Popup appears with beep sound
- Click **✓ TAKEN** (mark as taken)
- Click **⏰ SNOOZE 5m** (delay 5 minutes)
- Click **✗ MISS** (mark as missed)

### Backup Database
1. Go to **Settings** tab
2. Click **💾 Backup Database**
3. Choose save location

---

## 🏗️ Architecture

**MVC Pattern**:
- **Models**: Medicine, Reminder, Inventory, DoseHistory
- **Views**: MainFrame, DashboardPanel, EnhancedMedicinePanel, ReminderPanel, etc.
- **Controllers**: MedicineController, ReminderController, InventoryController, HistoryController
- **Database**: SQLite with DatabaseManager singleton

**Background Services**:
- Alarm monitoring (30-second intervals)
- Auto-refresh timers
- Real-time clock

---

## 🛠️ Technologies

- **Java 8+** - Core language
- **Swing/AWT** - Desktop UI
- **SQLite** - Database
- **Maven** - Build tool
- **SLF4J** - Logging

---

## 🔧 Troubleshooting

**App won't start?**
```bash
# Check Java version
java -version

# Run from command line to see errors
java -cp target/classes com.example.Main
```

**Alarm not ringing?**
- Check reminder status is PENDING
- Verify system time is correct
- Ensure app is running (not closed)

---

## 📁 Project Structure

```
Daily_Medicine_Remainder/
├── src/main/java/com/example/
│   ├── Main.java
│   ├── controller/
│   ├── model/
│   ├── view/
│   ├── database/
│   └── utils/
├── data/
│   └── dailydose.db
├── pom.xml
└── README.md
```

---

## 🚀 Future Enhancements

- [ ] PDF/CSV export
- [ ] Adherence charts
- [ ] Recurring reminders
- [ ] Multi-user support
- [ ] Mobile app
- [ ] Cloud sync

---

## 👨‍💻 Author

**Kayes Ahmed**
- GitHub: [@kayes2207054](https://github.com/kayes2207054)
- Project: Advanced Java Lab

---

## 📞 Documentation

- **Full Documentation**: See [ENHANCEMENTS.md](ENHANCEMENTS.md)
- **Reminder Guide**: See [REMINDER_FEATURES_GUIDE.md](REMINDER_FEATURES_GUIDE.md)

---

**Made with ❤️ for Better Health Management** 💊⏰
