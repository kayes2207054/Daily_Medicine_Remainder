# 🧪 DailyDose - Testing Checklist

**Date**: _________________  
**Tester**: _________________  
**Java Version**: _________________  
**IntelliJ Version**: _________________

---

## ✅ COMPILATION & STARTUP

- [ ] Project opens in IntelliJ without errors
- [ ] Maven dependencies download successfully
- [ ] Application compiles without errors
- [ ] Application launches successfully
- [ ] Login screen appears with gradient background
- [ ] No console errors visible

---

## 🔐 AUTHENTICATION SYSTEM

### Patient Login
- [ ] Login with patient credentials (admin/admin123)
- [ ] Role selection "Patient" works
- [ ] Successful login redirects to Patient Dashboard
- [ ] User label shows "Welcome, admin" or username
- [ ] Logout button visible and functional

### Guardian Login
- [ ] Login with guardian credentials (guardian/guard123)
- [ ] Role selection "Guardian" works
- [ ] Successful login redirects to Guardian Dashboard
- [ ] User label shows "Welcome, guardian" or username
- [ ] Logout button functional

### Registration
- [ ] Click "Register Here" link on login screen
- [ ] Registration form appears
- [ ] All fields (username, fullName, email, password, confirmPassword) present
- [ ] Role selection (Patient/Guardian) works
- [ ] Password matching validation works
- [ ] Successful registration auto-redirects to login
- [ ] Can login with newly registered account

### Validation
- [ ] Empty fields show error message
- [ ] Short username (<3 chars) rejected
- [ ] Weak password (<6 chars) rejected
- [ ] Mismatched passwords rejected
- [ ] Wrong role selection shows error

---

## 👤 PATIENT FEATURES

### Dashboard Tab
- [ ] 4 statistics cards visible:
  - Total Medicines
  - Pending Reminders
  - Taken Today
  - Missed Today
- [ ] Recent activity table shows data
- [ ] Statistics update automatically

### Medicines Tab
**Add Medicine**
- [ ] "Add Medicine" button clickable
- [ ] Dialog with fields appears: Name, Dosage, Frequency, Instructions
- [ ] Can enter medicine details
- [ ] Click OK saves medicine
- [ ] New medicine appears in table
- [ ] Medicine ID auto-generated

**View Medicines**
- [ ] Medicines table has 5 columns (ID, Name, Dosage, Frequency, Instructions)
- [ ] All medicines load correctly
- [ ] Table scrollable if many medicines

**Search Medicine**
- [ ] Search box present
- [ ] Typing filters medicines in real-time
- [ ] Search works for medicine names
- [ ] Clear search shows all medicines

**Edit Medicine**
- [ ] Select medicine from table
- [ ] Click "Edit Medicine" button
- [ ] (Currently shows "Edit functionality coming soon")

**Delete Medicine**
- [ ] Select medicine from table
- [ ] Click "Delete Medicine" button
- [ ] Confirmation dialog appears
- [ ] Confirm deletes medicine from table
- [ ] Medicine removed from database

### Reminders Tab
**Add Reminder**
- [ ] "Add Reminder" button clickable
- [ ] Dialog appears with fields: Medicine, Date, Time
- [ ] Can select medicine from dropdown/field
- [ ] Date picker allows date selection
- [ ] Time field accepts HH:mm format
- [ ] Click OK saves reminder
- [ ] Reminder appears in table with status "PENDING"

**View Reminders**
- [ ] Reminders table has 5 columns (ID, Medicine, Date, Time, Status)
- [ ] Status color-coded:
  - Green for TAKEN
  - Red for MISSED
  - Orange for PENDING
- [ ] All reminders load correctly

**Refresh Reminders**
- [ ] "Refresh" button updates table
- [ ] New reminders appear after refresh

**Edit Reminder**
- [ ] Select reminder
- [ ] Click "Edit Reminder"
- [ ] (Currently shows info message)

**Delete Reminder**
- [ ] Select reminder
- [ ] Click "Delete Reminder"
- [ ] Confirmation dialog
- [ ] Confirm deletes reminder

### History Tab
**View History**
- [ ] History table shows all dose records
- [ ] 6 columns visible (ID, Medicine, Date, Time, Status, Notes)
- [ ] Status color-coded (Green/Red/Orange)

**Filter by Date Range**
- [ ] Start Date picker works
- [ ] End Date picker works
- [ ] "Filter" button filters history
- [ ] Filtered results show correct date range
- [ ] "Show All" button removes filter

---

## ⏰ ALARM SYSTEM (CRITICAL)

### Setup
- [ ] Login as Patient
- [ ] Add medicine (e.g., "Test Medicine")
- [ ] Set reminder:
  - Date: Today
  - Time: Current time + 1 minute
  - Status: PENDING
- [ ] Reminder saved successfully

### Alarm Trigger
- [ ] Wait for scheduled time
- [ ] **ALARM POPUP APPEARS** exactly at scheduled time
- [ ] **SYSTEM BEEP SOUND** plays (high volume)
- [ ] Popup has red gradient background
- [ ] Popup shows:
  - "⏰ TIME TO TAKE YOUR MEDICINE!"
  - Medicine name
  - Scheduled time
- [ ] Popup is modal (blocks other interactions)

### Alarm Actions
**TAKEN Button**
- [ ] "✓ TAKEN" button visible (green)
- [ ] Click TAKEN closes popup
- [ ] Beep sound stops
- [ ] Reminder status updates to TAKEN
- [ ] Record saved in History with current timestamp
- [ ] Guardian receives notification (if linked)

**MISSED Button**
- [ ] "✗ MISSED" button visible (red)
- [ ] Click MISSED closes popup
- [ ] Beep sound stops
- [ ] Reminder status updates to MISSED
- [ ] Record saved in History with current timestamp
- [ ] Guardian receives notification (if linked)

**STOP ALARM Button**
- [ ] "⏹ STOP ALARM" button visible (dark)
- [ ] Click STOP closes popup
- [ ] Beep sound stops
- [ ] Reminder status remains PENDING

### Multiple Alarms
- [ ] Set 2 reminders with different times
- [ ] Both alarms trigger at correct times
- [ ] Each alarm shows correct medicine name
- [ ] No duplicate popups for same reminder

---

## 👨‍👧 GUARDIAN FEATURES

### My Patients Tab
**Link Patient**
- [ ] Link patient input field visible
- [ ] Enter patient username (e.g., "admin")
- [ ] Click "Link Patient" button
- [ ] Success message appears
- [ ] Patient appears in patients table

**View Patients**
- [ ] Patients table shows linked patients
- [ ] 6 columns: ID, Name, Username, Linked Since, Adherence%, Actions
- [ ] Adherence % calculates correctly
- [ ] "View Details" button in Actions column

**Patient Details**
- [ ] Click "View Details" button
- [ ] Patient selected in Patient History tab
- [ ] History loads automatically

### Patient History Tab
**Select Patient**
- [ ] Patient dropdown shows linked patients
- [ ] Selecting patient loads their history
- [ ] 4 statistics cards update:
  - Total Doses
  - Taken
  - Missed
  - Adherence %
- [ ] History table shows all patient's dose records

**Filter by Date**
- [ ] Start date picker works
- [ ] End date picker works
- [ ] "Filter" button applies date range
- [ ] Statistics recalculate for filtered range
- [ ] "Show All" removes filter

### Notifications Tab
**View Notifications**
- [ ] Unread count badge visible (red circle)
- [ ] Unread count shows correct number
- [ ] Notifications list loads
- [ ] Each notification card shows:
  - Icon (✅ for TAKEN, ❌ for MISSED)
  - Patient name
  - Medicine name
  - Action (Taken/Missed)
  - Timestamp
  - "Mark Read" button (if unread)

**Notification Colors**
- [ ] Unread notifications: yellow background, orange border
- [ ] Read notifications: gray background, gray border

**Mark as Read**
- [ ] Click "Mark Read" on individual notification
- [ ] Notification color changes to gray
- [ ] Unread count decreases
- [ ] "Mark Read" button disappears

**Mark All Read**
- [ ] "Mark All Read" button visible
- [ ] Click marks all notifications as read
- [ ] All notifications turn gray
- [ ] Unread count becomes 0

**Refresh**
- [ ] "Refresh" button updates notification list
- [ ] New notifications appear after refresh

---

## 🔔 GUARDIAN NOTIFICATION FLOW (END-TO-END)

### Setup
1. [ ] Patient "admin" and Guardian "guardian" are linked
2. [ ] Login as Patient (admin/admin123)
3. [ ] Set reminder (current time + 1 minute)
4. [ ] Wait for alarm

### Patient Action
5. [ ] Alarm rings at scheduled time
6. [ ] Click "TAKEN" button
7. [ ] Logout

### Guardian Receives
8. [ ] Login as Guardian (guardian/guard123)
9. [ ] Notification badge shows "1" (or increased count)
10. [ ] Go to Notifications tab
11. [ ] New notification appears:
    - Yellow background (unread)
    - Icon: ✅
    - Message: "admin took their medicine"
    - Medicine: [Medicine name]
    - Timestamp: [Current time]
12. [ ] Click "Mark Read"
13. [ ] Notification turns gray
14. [ ] Badge count decreases

### Test MISSED Flow
15. [ ] Logout guardian
16. [ ] Login as Patient
17. [ ] Set new reminder (current time + 1 minute)
18. [ ] Wait for alarm
19. [ ] Click "MISSED" button
20. [ ] Logout
21. [ ] Login as Guardian
22. [ ] New notification shows:
    - Icon: ❌
    - Message: "admin MISSED their medicine"
    - Timestamp: [Current time]

---

## 💾 DATABASE PERSISTENCE

- [ ] Close application completely
- [ ] Reopen application
- [ ] Login with existing account
- [ ] All medicines still present
- [ ] All reminders still present
- [ ] All history records still present
- [ ] Guardian-patient links still present
- [ ] Notifications still present

---

## 🎨 UI/UX CHECK

### Design
- [ ] Gradient backgrounds visible
- [ ] Colors professional and consistent
- [ ] Buttons have hover effects
- [ ] Tables have proper spacing
- [ ] Text readable and clear

### Responsiveness
- [ ] Window resizable
- [ ] Tables adjust to window size
- [ ] Scrollbars appear when needed
- [ ] No overlapping elements

### Navigation
- [ ] Tabs switch smoothly
- [ ] Back/forward navigation works
- [ ] Logout returns to login screen
- [ ] Login redirects to correct dashboard

---

## 📊 EDGE CASES & VALIDATION

### Empty Data
- [ ] Empty medicines list shows empty table (no crash)
- [ ] Empty reminders list shows empty table
- [ ] Empty history shows empty table
- [ ] No patients linked shows empty table

### Invalid Inputs
- [ ] Adding medicine with empty name rejected
- [ ] Adding reminder with past time rejected/allowed
- [ ] Invalid time format (25:99) rejected
- [ ] Special characters in username handled

### Duplicate Data
- [ ] Same medicine name allowed (different IDs)
- [ ] Same reminder time allowed (different medicines)
- [ ] Duplicate username rejected in registration

---

## 🐛 ERROR HANDLING

- [ ] No crashes on invalid input
- [ ] Error messages displayed clearly
- [ ] Console shows no stack traces (normal operation)
- [ ] Application recovers from errors gracefully

---

## ✅ FINAL VERIFICATION

- [ ] **JavaFX + FXML only** (NO Swing components)
- [ ] **Real-time alarm system** working
- [ ] **System beep sound** plays on alarm
- [ ] **Guardian notifications** working
- [ ] **All CRUD operations** functional
- [ ] **Date filtering** works
- [ ] **Search functionality** works
- [ ] **Statistics calculation** accurate
- [ ] **Database persistence** confirmed
- [ ] **No compile errors**
- [ ] **No runtime errors**
- [ ] **Professional UI** with modern design

---

## 📝 NOTES & ISSUES

**Issues Found:**
1. _____________________________________________________________________
2. _____________________________________________________________________
3. _____________________________________________________________________

**Suggestions:**
1. _____________________________________________________________________
2. _____________________________________________________________________
3. _____________________________________________________________________

**Overall Rating:** _____ / 10

**Ready for Demonstration:** [ ] YES  [ ] NO

**Tester Signature:** _____________________  
**Date:** _____________________

---

**END OF TESTING CHECKLIST**

*Save this file after completing all tests!*
