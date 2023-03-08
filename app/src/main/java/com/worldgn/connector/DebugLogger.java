package com.worldgn.connector;

import android.util.Log;
import it.rialtlas.healthmonitor.BuildConfig;
/**
 * Created by Krishna Rao on 18/09/2017.
 */

class DebugLogger {

    public static final Boolean DEBUG_MODE = true;
    
    public static void v(final String tag, final String text) {
        if (BuildConfig.DEBUG)
            Log.v(tag, text);
    }

    public static void d(final String tag, final String text) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, text);
        }
    }

    public static void i(final String tag, final String text) {
        if (BuildConfig.DEBUG)
            Log.i(tag, text);
    }

    public static void w(final String tag, final String text) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, text);
        }
    }

    public static void e(final String tag, final String text) {
        if (BuildConfig.DEBUG)
            Log.e(tag, text);
    }

    public static void e(final String tag, final String text, final Throwable e) {
        if (BuildConfig.DEBUG)
            Log.e(tag, text, e);
    }

    public static void wtf(final String tag, final String text) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, text);
        }
    }

    public static void wtf(final String tag, final String text, final Throwable e) {
        if (DEBUG_MODE) {
            Log.wtf(tag, text, e);
        }
    }
}
