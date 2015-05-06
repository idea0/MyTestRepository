package com.haomee.util;

import android.util.Log;

/**
 * log info
 * @author noTrace
 *
 */
public class LogUtil {
    private static final String TAG = "myLog";
    public static final boolean isDebug = true;
    public static final String NULL = "NULL";

    public static void trace(Exception e) {
        if (!isDebug)
            return;
        final String msg = e.getMessage();
        Log.e(TAG, msg == null ? NULL : msg);
    }

    public static void trace(String msg) {
        if (!isDebug)
            return;
        Log.d(TAG, msg == null ? NULL : msg);
    }


    public static void trace(String tag, String msg) {
        if (!isDebug)
            return;
        Log.d(TAG + tag, msg);
    }

    public static void trace(String tag, Exception e) {
        if (!isDebug)
            return;
        final String msg = e.getMessage();
        Log.e(TAG + tag, msg == null ? NULL : msg);
    }
}
