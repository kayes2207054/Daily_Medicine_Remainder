package com.example.utils;

import com.example.model.DoseHistory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

/**
 * FileUtils Class
 * Provides utilities for file operations including CSV export and JSON serialization.
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String EXPORT_DIR = "exports/";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        try {
            Files.createDirectories(Paths.get(EXPORT_DIR));
        } catch (IOException e) {
            logger.error("Error creating export directory", e);
        }
    }

    /**
     * Export dose history to CSV file
     */
    public static String exportHistoryToCSV(List<DoseHistory> histories, String fileName) {
        String filePath = EXPORT_DIR + fileName + "_" + System.currentTimeMillis() + ".csv";
        try (Writer out = new FileWriter(filePath);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                     .withHeader("ID", "Medicine Name", "Scheduled Time", "Taken Time", "Status", "Notes"))) {

            for (DoseHistory history : histories) {
                printer.printRecord(
                        history.getId(),
                        history.getMedicineName(),
                        history.getScheduledTime(),
                        history.getTakenTime(),
                        history.getStatus(),
                        history.getNotes() != null ? history.getNotes() : ""
                );
            }
            printer.flush();
            logger.info("History exported to CSV: " + filePath);
            return filePath;
        } catch (IOException e) {
            logger.error("Error exporting history to CSV", e);
        }
        return null;
    }

    /**
     * Export daily report to CSV
     */
    public static String exportDailyReportToCSV(List<DoseHistory> histories, LocalDate date) {
        String fileName = "daily_report_" + date;
        return exportHistoryToCSV(histories, fileName);
    }

    /**
     * Export weekly report to CSV
     */
    public static String exportWeeklyReportToCSV(List<DoseHistory> histories, LocalDate startDate, LocalDate endDate) {
        String fileName = "weekly_report_" + startDate + "_to_" + endDate;
        return exportHistoryToCSV(histories, fileName);
    }

    /**
     * Export monthly report to CSV
     */
    public static String exportMonthlyReportToCSV(List<DoseHistory> histories, int month, int year) {
        String fileName = "monthly_report_" + month + "_" + year;
        return exportHistoryToCSV(histories, fileName);
    }

    /**
     * Save object to JSON file
     */
    public static <T> boolean saveToJSON(T object, String fileName) {
        String filePath = EXPORT_DIR + fileName + ".json";
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(object, writer);
            logger.info("Object saved to JSON: " + filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error saving object to JSON", e);
        }
        return false;
    }

    /**
     * Load object from JSON file
     */
    public static <T> T loadFromJSON(String fileName, Class<T> classOfT) {
        String filePath = EXPORT_DIR + fileName + ".json";
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, classOfT);
        } catch (IOException e) {
            logger.error("Error loading object from JSON", e);
        }
        return null;
    }

    /**
     * Create backup of database
     */
    public static boolean backupDatabase() {
        try {
            String sourceFile = "daily_dose.db";
            String backupFile = EXPORT_DIR + "backup_daily_dose_" + System.currentTimeMillis() + ".db";
            Files.copy(Paths.get(sourceFile), Paths.get(backupFile));
            logger.info("Database backed up to: " + backupFile);
            return true;
        } catch (IOException e) {
            logger.error("Error backing up database", e);
        }
        return false;
    }

    /**
     * Check if file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Delete file
     */
    public static boolean deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
            logger.info("File deleted: " + filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error deleting file", e);
        }
        return false;
    }

    /**
     * Get export directory path
     */
    public static String getExportDirectory() {
        return EXPORT_DIR;
    }
}
