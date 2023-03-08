package com.worldgn.connector;

import android.content.Context;
import android.os.SystemClock;

import com.github.hussainderry.securepreferences.SecurePreferences;
import com.github.hussainderry.securepreferences.model.DigestType;
import com.github.hussainderry.securepreferences.model.SecurityConfig;
import com.github.hussainderry.securepreferences.util.AsyncDataLoader;


import java.util.concurrent.Future;

/**
 * Created by Krishna Rao on 13/10/2017.
 */

class SafePreferences {

    private static final String PASSWORD = "Qwerty&*()";
    private static final String FILENAME = "securefile";
    private SecurePreferences mPreferences;
    private SecurePreferences.Editor mEditor;
    private AsyncDataLoader mAsyncLoader;
    private static SafePreferences instance;
    private static String LOCK = "lock";

    private SafePreferences() {

    }

    public static SafePreferences getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new SafePreferences();
                }
            }
        }
        return instance;
    }

    public void intilizeSecure(Context context) {
        // Minimum Configurations
        SecurityConfig minimumConfig = new SecurityConfig.Builder(PASSWORD)
                .build();

        // Full Configurations
        SecurityConfig fullConfig = new SecurityConfig.Builder(PASSWORD)
                .setAesKeySize(256)
                .setPbkdf2SaltSize(32)
                .setPbkdf2Iterations(24000)
                .setDigestType(DigestType.SHA256)
                .build();

        mPreferences = SecurePreferences.getInstance(context, FILENAME, minimumConfig);
        mEditor = mPreferences.edit();
        mAsyncLoader = mPreferences.getAsyncDataLoader();
    }

    public void saveStringToSecurePref(String key, String data) {
        mEditor.putString(key, data);
        mEditor.apply();
    }

    public void saveBooleanToSecurePref(String key, boolean data) {
        mEditor.putBoolean(key, data);
        mEditor.apply();
    }

    public void saveIntToSecurePref(String key, int data) {
        mEditor.putInt(key, data);
        mEditor.apply();
    }


    public void saveLongToSecurePref(String key, long data) {
        mEditor.putLong(key, data);
        mEditor.apply();
    }

    public String getStringFromSecurePref(String key, String defvalue) throws Exception {
        Future<String> mFuture = mAsyncLoader.getString(key, defvalue);
        while (!mFuture.isDone()) {
//            DebugLogger.i("SafePreferences", "Loading from pref!");
            SystemClock.sleep(50);
        }

        return mFuture.get();
    }

    public Boolean getBooleanFromSecurePref(String key, Boolean defValue) throws Exception {
        Future<Boolean> mFuture = mAsyncLoader.getBoolean(key, defValue);
        while (!mFuture.isDone()) {
//            DebugLogger.i("SafePreferences", "Loading from pref!");
            SystemClock.sleep(50);
        }

        return mFuture.get();
    }

    public Integer getIntFromSecurePref(String key, Integer defValue) throws Exception {
        Future<Integer> mFuture = mAsyncLoader.getInt(key, defValue);
        while (!mFuture.isDone()) {
//            DebugLogger.i("SafePreferences", "Loading from pref!");
            SystemClock.sleep(50);
        }

        return mFuture.get();
    }


    public Long getLongFromSecurePref(String key, Long defValue) throws Exception {

        Future<Long> mFuture = mAsyncLoader.getLong(key, defValue);
        while (!mFuture.isDone()) {
//            DebugLogger.i("SafePreferences", "Loading from pref!");
            SystemClock.sleep(50);
        }

        return mFuture.get();
    }




}
