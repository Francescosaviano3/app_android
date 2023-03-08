package it.rialtlas.healthmonitor.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

class AlertDialogRunner implements Runnable {
    private final Activity uiActivity;
    private final String title;
    private final String message;
    private final ButtonsManager[] buttonsListener;

    private AlertDialogRunner(Activity uiActivity, String title, String message, ButtonsManager[] buttonsListener) {
        this.uiActivity = uiActivity;
        this.title = title;
        this.message = message;
        this.buttonsListener = buttonsListener;
    }

    @Override
    public void run() {
        AlertDialog dialog = new AlertDialog.Builder(this.uiActivity).create();
        dialog.setTitle(this.title);
        dialog.setMessage(this.message);
        for (ButtonsManager buttonManager : buttonsListener) {
            dialog.setButton(buttonManager.button(), buttonManager.buttonText(),buttonManager.listener());
        }
        dialog.show();
    }

    public static AlertDialogRunner of(Activity uiActivity, String title, String message, ButtonsManager[] buttonsListener) {
        return new AlertDialogRunner(uiActivity, title, message, buttonsListener);
    }

    static class AlertDialogOkManager implements DialogInterface.OnClickListener {
        private final boolean exitOnOK;

        AlertDialogOkManager(boolean exitOnOK) {
            this.exitOnOK = exitOnOK;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (exitOnOK) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
            else {
                dialog.dismiss();
            }
        }
    }

    static class ButtonsManager {
        private final int button;
        private final String buttonText;
        private final DialogInterface.OnClickListener listener;

        private ButtonsManager(int button, String buttonText, DialogInterface.OnClickListener listener) {
            this.button = button;
            this.listener = listener;
            this.buttonText = buttonText;
        }

        int button() {
            return this.button;
        }

        String buttonText() {
            return this.buttonText;
        }

        DialogInterface.OnClickListener listener() {
            return listener;
        }

        public static ButtonsManager of(int button, String buttonText, DialogInterface.OnClickListener listener) {
            return new ButtonsManager(button, buttonText, listener);
        }
    }
}
