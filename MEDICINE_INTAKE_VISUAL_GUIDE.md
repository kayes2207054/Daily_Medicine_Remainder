# Medicine Intake Recording - Visual Guide

## Reminder Panel Interface

```
┌─────────────────────────────────────────────────────────────────┐
│                   Reminder Management                            │
├─────────────────────────────────────────────────────────────────┤
│  ID │ Medicine    │ Time   │ Type    │ Taken                    │
├────┼─────────────┼────────┼─────────┼─────────────────────────┤
│ 1  │ Aspirin     │ 08:00  │ morning │ Yes                     │
│ 2  │ Lisinopril  │ 12:00  │ noon    │ No                      │
│ 3  │ Metformin   │ 18:00  │ evening │ No                      │
├─────────────────────────────────────────────────────────────────┤
│ [Add Reminder] [✓ Mark as Taken] [✗ Mark as Not Taken]         │
└─────────────────────────────────────────────────────────────────┘
```

### How to Use:
1. **Select a reminder** from the table
2. Click **"✓ Mark as Taken"** (Green) if you took the medicine
3. Click **"✗ Mark as Not Taken"** (Red) if you didn't take it
4. Table updates with new status

---

## History Panel Interface

```
┌─────────────────────────────────────────────────────────────────┐
│                  Dose History & Reports                          │
├─────────────────────────────────────────────────────────────────┤
│  ID │ Medicine    │ Date       │ Time   │ Status                 │
├────┼─────────────┼────────────┼────────┼──────────────────────┤
│ 1  │ Aspirin     │ 2025-12-09 │ 08:00  │ Taken                │
│ 2  │ Lisinopril  │ 2025-12-09 │ 12:00  │ Missed               │
│ 3  │ Metformin   │ 2025-12-09 │ 18:00  │ Pending              │
│ 4  │ Omeprazole  │ 2025-12-08 │ 08:00  │ Taken                │
├─────────────────────────────────────────────────────────────────┤
│ [Today] [This Week] [This Month] [All]                          │
│ [✓ Mark as Taken] [✗ Mark as Missed] [? Mark as Pending]       │
└─────────────────────────────────────────────────────────────────┘
```

### How to Use:
1. Use **filter buttons** to find specific doses (Today, This Week, etc.)
2. **Select a dose** from the table
3. Click one of the status buttons:
   - **"✓ Mark as Taken"** (Green) - Dose was successfully taken
   - **"✗ Mark as Missed"** (Red) - Dose was not taken
   - **"? Mark as Pending"** (Yellow) - Dose status is uncertain
4. Table updates with new status immediately

---

## Status Meanings

| Status  | Color  | Meaning |
|---------|--------|---------|
| Taken   | Green  | ✓ Medicine was successfully taken |
| Missed  | Red    | ✗ Medicine was not taken |
| Pending | Yellow | ? Status not yet determined |

---

## Workflow Examples

### Morning Routine
```
8:00 AM → Alarm/Notification
    ↓
Open DailyDose → Go to Reminders
    ↓
Select "Aspirin" at 8:00 AM
    ↓
Click "✓ Mark as Taken"
    ↓
Status updates to "Yes"
    ↓
Dashboard shows +1 dose taken today
```

### Tracking Missed Doses
```
Realize you forgot noon medication
    ↓
Go to History Panel
    ↓
Filter "Today"
    ↓
Select Lisinopril 12:00 PM
    ↓
Click "✗ Mark as Missed"
    ↓
Status updates to "Missed"
    ↓
Adherence report shows: 2/3 doses (66%)
```

### Updating Pending Status
```
Uncertain about afternoon dose
    ↓
Click "? Mark as Pending"
    ↓
Later when confirmed, go back
    ↓
Select same dose again
    ↓
Click "✓ Mark as Taken" (or "✗ Mark as Missed")
    ↓
Status updated and statistics recalculate
```

---

## Dashboard Integration

### Today's Overview
```
┌────────────────────────────────────────────────────────────────┐
│                      DailyDose Dashboard                         │
├────────────────────┬────────────────────┬────────────────────┤
│ Total Medicines    │ Pending Reminders  │ Low Stock Items    │
│        5           │        2           │        1           │
├────────────────────┴────────────────────┴────────────────────┤
│ Today's Doses Taken: 4/6 (66%)                              │
├────────────────────────────────────────────────────────────────┤
│ [Add Medicine] [Set Reminder] [View History]                  │
└────────────────────────────────────────────────────────────────┘
```

---

## Database Structure

### Reminder Table (Relevant Columns)
```
reminders
├── id (PK)
├── medicine_name
├── time
├── taken (0 = false, 1 = true)
├── last_taken_at (timestamp)
└── created_at
```

### DoseHistory Table
```
dose_history
├── id (PK)
├── medicine_id (FK)
├── medicine_name
├── date (YYYY-MM-DD)
├── time (HH:MM:SS)
├── status ('Taken', 'Missed', 'Pending')
├── recorded_at (timestamp)
└── notes (optional)
```

---

## Keyboard Shortcuts (Potential)

In future versions, you could use:
- **Ctrl+T** - Mark selected as Taken
- **Ctrl+M** - Mark selected as Missed
- **Ctrl+P** - Mark selected as Pending
- **Ctrl+R** - Refresh current view

---

## Tips & Tricks

1. **Batch Update**: Select multiple doses and update them together (if implemented)
2. **Quick Add**: Use the "Add Reminder" button when you need to add a new dose entry
3. **Export Data**: Export history to CSV for doctor appointments
4. **Weekly Review**: Use "This Week" filter to review your adherence
5. **Pattern Recognition**: Look for days/times when you typically miss doses

---

## Error Handling

### Common Messages

**"Please select a reminder!"**
- Solution: Click on a row in the table before clicking the button

**"Please select a dose history record!"**
- Solution: Click on a row in the history table before clicking the button

**"Failed to update dose status!"**
- Solution: Database connection issue; try again or restart the application

---

## Privacy & Data

- All data is stored locally in `daily_dose.db`
- No data is sent to external servers
- You can backup your database anytime
- Data can be exported to CSV for sharing with healthcare providers

---

## Next Steps

1. Try marking a reminder as taken
2. Check the dashboard to see updated statistics
3. Navigate to History to verify the record
4. Export your data when visiting your doctor
5. Review weekly patterns to improve adherence

---

## Support

For issues or questions:
1. Check that a row is selected before clicking buttons
2. Ensure the application has write permissions to the database file
3. Restart the application if something seems incorrect
4. Contact support with the error message shown

