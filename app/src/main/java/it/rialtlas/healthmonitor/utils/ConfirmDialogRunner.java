package it.rialtlas.healthmonitor.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import it.rialtlas.healthmonitor.R;

public class ConfirmDialogRunner implements Runnable {
    private final Activity uiActivity;
    private final String message;
    private final DialogInterface.OnClickListener confirmListener;

    private ConfirmDialogRunner(Activity uiActivity, String message, DialogInterface.OnClickListener confirmListener) {
        this.uiActivity = uiActivity;
        this.message = message;
        this.confirmListener = confirmListener;
    }
    @Override
    public void run() {
        AlertDialog alertDialog = new AlertDialog.Builder(uiActivity).create();
        alertDialog.setTitle(uiActivity.getApplication().getResources().getString(R.string.MSG_WARNING));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, uiActivity.getApplication().getResources().getString(R.string.MSG_NO), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                }
            });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, uiActivity.getApplication().getResources().getString(R.string.MSG_YES), this.confirmListener);
        alertDialog.show();
    }

    public static ConfirmDialogRunner of(Activity uiActivity, String message, DialogInterface.OnClickListener confirmListener) {
        return new ConfirmDialogRunner(uiActivity, message, confirmListener);
    }
}