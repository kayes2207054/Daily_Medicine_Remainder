package com.example.utils;

// Incremental Progress: 60% → 70%

import java.util.prefs.Preferences;

/**
 * AppSettings (basic): stores small user preferences.
 * NOTE: Student-level, non-final; minimal keys only.
 * TODO: Expand with more settings and error handling later.
 */
public final class AppSettings {
    private static final String NODE = "DailyDose";
    private static final String KEY_USE_24H = "use24Hour";

    private AppSettings() {}

    public static boolean getUse24Hour() {
        try {
            Preferences prefs = Preferences.userRoot().node(NODE);
            return prefs.getBoolean(KEY_USE_24H, true); // default 24h
        } catch (Exception ignored) {
            return true; // fallback
        }
    }

    public static void setUse24Hour(boolean value) {
        try {
            Preferences prefs = Preferences.userRoot().node(NODE);
            prefs.putBoolean(KEY_USE_24H, value);
        } catch (Exception ignored) {
            // TODO: Consider logging failures
        }
    }
}
