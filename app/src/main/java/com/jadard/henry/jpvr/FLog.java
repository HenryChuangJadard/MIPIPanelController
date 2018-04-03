/*
 *  /*************************************************************************
 *  *
 *  * Jadard Technology Inc. CONFIDENTIAL
 *  * __________________
 *  *  All Rights Reserved.
 *  * 2018 MIPIPanelController
 *  * NOTICE:  All information contained herein is, and remains  the property of Jadard Technology Inc..
 *  * The intellectual and technical concepts contained herein are proprietary to Jadard Technology Inc.
 *  * patents in process, and are protected by trade secret or copyright law.
 *  * Dissemination of this information or reproduction of this material is strictly forbidden unless prior
 *  * written permission is obtained from Jadard Technology Inc..
 *
 */

package com.jadard.henry.jpvr;

import android.util.Log;

public class FLog {
    public static final boolean DEBUG_MODE = true;
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
