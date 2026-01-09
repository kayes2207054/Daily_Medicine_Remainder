package com.example.utils;

/**
 * Interface for listening to data changes in the application.
 * This enables real-time UI updates when data is modified.
 * 
 * ডেটা পরিবর্তন শ্রবণকারী ইন্টারফেস - রিয়েল টাইম UI আপডেট সক্ষম করে
 */
public interface DataChangeListener {
    
    /**
     * Called when medicine data changes (add, update, delete)
     */
    void onMedicineDataChanged();
    
    /**
     * Called when reminder data changes (add, update, delete)
     */
    void onReminderDataChanged();
    
    /**
     * Called when inventory data changes (add, update, delete)
     */
    void onInventoryDataChanged();
    
    /**
     * Called when history data changes (add, update, delete)
     */
    void onHistoryDataChanged();
}
