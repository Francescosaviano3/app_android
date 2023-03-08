package it.rialtlas.healthmonitor.utils;

import android.app.Activity;
import android.widget.Toast;

class ToastMessageRunner implements Runnable {
    private final Activity uiActivity;
    private final String message;
    private final int duration;

    private ToastMessageRunner(Activity uiActivity, String message, int duration) {
        this.uiActivity = uiActivity;
        this.message = message;
        this.duration = duration;
    }

    @Override
    public void run() {
        Toast.makeText(uiActivity, message, duration).show();
    }

    public static ToastMessageRunner of(Activity uiActivity, String message, int duration) {
        return new ToastMessageRunner(uiActivity, message, duration);
    }
}
