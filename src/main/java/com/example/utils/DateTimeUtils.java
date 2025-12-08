package com.example.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    
    public static String getTodayDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }
}
