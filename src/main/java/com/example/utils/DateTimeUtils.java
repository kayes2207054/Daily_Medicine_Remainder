package com.example.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * DateTimeUtils Class
 * Provides utility methods for date and time operations throughout the application.
 */
public class DateTimeUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get today's date as string
     */
    public static String getTodayDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * Get current time as string
     */
    public static String getCurrentTime() {
        return LocalTime.now().format(TIME_FORMATTER);
    }

    /**
     * Get current date and time as string
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * Parse date string to LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * Parse time string to LocalTime
     */
    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    /**
     * Format LocalDate to string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Format LocalTime to string
     */
    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    /**
     * Get days between two dates
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Check if time is between two times
     */
    public static boolean isTimeBetween(LocalTime time, LocalTime startTime, LocalTime endTime) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }

    /**
     * Get predefined reminder time for morning (08:00)
     */
    public static LocalTime getMorningTime() {
        return LocalTime.of(8, 0);
    }

    /**
     * Get predefined reminder time for noon (12:00)
     */
    public static LocalTime getNoonTime() {
        return LocalTime.of(12, 0);
    }

    /**
     * Get predefined reminder time for evening (18:00)
     */
    public static LocalTime getEveningTime() {
        return LocalTime.of(18, 0);
    }

    /**
     * Check if current time matches reminder time (within 1 minute)
     */
    public static boolean isReminderTime(LocalTime reminderTime) {
        LocalTime now = LocalTime.now();
        return Math.abs(ChronoUnit.MINUTES.between(now, reminderTime)) <= 1;
    }
}
