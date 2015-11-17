package com.dsiddiqui.android.tunein.sdk;

import android.util.Log;

import com.dsiddiqui.android.tunein.BuildConfig;

/**
 * Created by dsiddiqui on 15-11-07.
 */
public class TuneLogger {

    // Constants

    private static final String TAG = "Button";

    // Logging Methods

    public static void logMessage(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void logMessage(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void logError(String errorMessage) {
        logError(TAG, errorMessage);
    }

    public static void logError(String tag, String errorMessage) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, (errorMessage == null || errorMessage.isEmpty()) ? "Unspecified error occurred" : errorMessage);
        }
    }

    public static void logException(Exception e) {
        logException(TAG, e);
    }

    public static void logException(String tag, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, e.getLocalizedMessage());
        }
    }

}