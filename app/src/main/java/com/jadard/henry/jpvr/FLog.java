package com.jadard.henry.jpvr;

import android.util.Log;

public class FLog {
    public static final boolean DEBUG_MODE = false;
    public static final String LOG_TAG = "[JPVR]";

    public static void v(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.v(LOG_TAG, tag + " - " + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG_MODE) {
//			Log.i(LOG_TAG, tag + " - " + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.d(LOG_TAG, tag + " - " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.w(LOG_TAG, tag + " - " + msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG_MODE) {
            Log.w(LOG_TAG, tag + " - " + msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.e(LOG_TAG, tag + " - " + msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG_MODE) {
            Log.e(LOG_TAG, tag + " - " + msg, tr);
        }
    }
}
