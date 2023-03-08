package it.rialtlas.healthmonitor.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import it.rialtlas.healthmonitor.R;

/**
 * @author Veniero Mario - 11/09/2018
 */

public class MessagingUtils {

    private static Activity mainActivity;

    public static void criticalError(final int message, boolean exitOnOK) {
        if (mainActivity!=null) {
           mainActivity.runOnUiThread(AlertDialogRunner.of(
                    mainActivity, mainActivity.getResources().getString(R.string.MSG_ERROR),
                    mainActivity.getResources().getString(message),
                    new AlertDialogRunner.ButtonsManager[ ] {
                            AlertDialogRunner.ButtonsManager.of(AlertDialog.BUTTON_NEUTRAL, translate(R.string.MSG_CLOSE), new AlertDialogRunner.AlertDialogOkManager(exitOnOK))
                    }));
            return;
        }
        throw new IllegalStateException("EINT0001");
    }
    public static void criticalError(final String message, boolean exitOnOK) {
        if (mainActivity!=null) {
            mainActivity.runOnUiThread(AlertDialogRunner.of(
                    mainActivity, mainActivity.getResources().getString(R.string.MSG_ERROR),
                    message,
                    new AlertDialogRunner.ButtonsManager[ ] {
                            AlertDialogRunner.ButtonsManager.of(AlertDialog.BUTTON_NEUTRAL, translate(R.string.MSG_CLOSE), new AlertDialogRunner.AlertDialogOkManager(exitOnOK))
                    }));
            return;
        }
        throw new IllegalStateException("EINT0001");
    }
    public static void oKCancelDialog(final int title, final String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        if (mainActivity!=null) {
            mainActivity.runOnUiThread(AlertDialogRunner.of(
                    mainActivity, mainActivity.getResources().getString(title),
                    message,
                    new AlertDialogRunner.ButtonsManager[ ] {
                            AlertDialogRunner.ButtonsManager.of(AlertDialog.BUTTON_POSITIVE, translate(R.string.MSG_OK), okListener),
                            AlertDialogRunner.ButtonsManager.of(AlertDialog.BUTTON_NEGATIVE, translate(R.string.MSG_CANCEL), cancelListener)
                    }));
            return;
        }
        throw new IllegalStateException("EINT0001");
    }
    public static void oKDialog(final int title, final String message, DialogInterface.OnClickListener okListener) {
        if (mainActivity!=null) {
            mainActivity.runOnUiThread(AlertDialogRunner.of(
                    mainActivity, mainActivity.getResources().getString(title),
                    message,
                    new AlertDialogRunner.ButtonsManager[ ] {
                            AlertDialogRunner.ButtonsManager.of(AlertDialog.BUTTON_POSITIVE, translate(R.string.MSG_OK), okListener)
                    }));
            return;
        }
        throw new IllegalStateException("EINT0001");
    }

    public static void shortToast(final int message) {
        if (mainActivity!=null) {
            mainActivity.runOnUiThread(ToastMessageRunner.of(mainActivity, mainActivity.getResources().getString(message), Toast.LENGTH_SHORT));
            return;
        }
        throw new IllegalStateException("EINT0001");
    }

    public static void longToast(final int message) {
        if (mainActivity!=null) {
            mainActivity.runOnUiThread(ToastMessageRunner.of(mainActivity, mainActivity.getResources().getString(message), Toast.LENGTH_LONG));
            return;
        }
        throw new IllegalStateException("EINT0001");
    }

    public static String translate(int message) {
        if (mainActivity!=null) {
            return mainActivity.getResources().getString(message);
        }
        return Integer.toString(message);
    }

    public static String translate(int... messages) {
        if (mainActivity!=null && messages!=null && messages.length>0) {
            StringBuilder sb = new StringBuilder();
            sb.append(mainActivity.getResources().getString(messages[0]));
            for (int i=1; i<messages.length; i++) {
                sb.append(" ").append(mainActivity.getResources().getString(messages[i]));
            }
            return sb.toString();
        }
        return "";
    }

    public static void initialize(Activity mainActivity) {
        MessagingUtils.mainActivity = mainActivity;
    }
}
