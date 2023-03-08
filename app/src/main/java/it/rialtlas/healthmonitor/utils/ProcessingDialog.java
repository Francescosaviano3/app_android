package it.rialtlas.healthmonitor.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.model.MeasurementsContextStrategy;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProcessingDialog extends AsyncTask<Void, Void, Void> {
    private int MAX_SECONDS = 300;
    private static final int SLEEP_TIME = 5000;
    private static final int SLEEP_INCREMENT = SLEEP_TIME / 1000;

    private final Lock processDialogLock = new ReentrantLock(true);

    private Context context;

    private ProgressDialog pd;
    private Handler handle;

    private boolean keepWorking;
    long elapsed = 0;

    private ProcessingDialog (Context context) {
        this.context = context;
        this.keepWorking = true;
    }

    private String toTimeString(int uptime) {
        long minutes = TimeUnit.SECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toSeconds(minutes);
        return Long.toString(minutes) + "min" + (uptime>0 ? " " + Long.toString(uptime) + "sec" : "");
    }

    @Override
    protected void onPreExecute() {
        processDialogLock.lock();
        try {
            pd = new ProgressDialog(context);
            pd.setTitle(MessagingUtils.translate(R.string.MSG_ACQUIRING));
            MAX_SECONDS = MeasurementsContextStrategy.getInstance().getMaxMeasurementsTime();
            pd.setMessage(toTimeString(MAX_SECONDS) + " " + MessagingUtils.translate(R.string.MSG_REMAINING_SECONDS));
            pd.setCancelable(true);
            pd.setIndeterminate(false);
            pd.setMax(MAX_SECONDS);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCanceledOnTouchOutside(false);
            pd.setButton(DialogInterface.BUTTON_NEGATIVE, MessagingUtils.translate(R.string.MSG_STOP_MEASURING), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MessagingUtils.oKCancelDialog(R.string.MSG_WARNING, "Confermi la richiesta di annullamento?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MeasurementsContextStrategy.getInstance().clearMeasurements();
                                    MeasurementsContextStrategy.getInstance().stopWaitingForMeasurement();
                                    MeasurementsContextStrategy.getInstance().state().unbind(MeasurementsContextStrategy.getInstance());
                                    MeasurementsContextStrategy.getInstance().state().scan(MeasurementsContextStrategy.getInstance());
                                }
                            } , null
                            );
                    dialog.dismiss();
                }
            });

            pd.show();
        }
        finally {
            processDialogLock.unlock();
        }
        this.handle = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                processDialogLock.lock();
                try {
                    if (pd!=null && pd.isShowing()) {
                        int timeToRecover = MeasurementsContextStrategy.getInstance().currentMeasurementElapsedTime(SLEEP_INCREMENT);
                        pd.incrementProgressBy(SLEEP_INCREMENT- timeToRecover);

                        pd.setTitle(
                                MessagingUtils.translate(
                                R.string.MSG_ACQUIRING,
                                MeasurementsContextStrategy.getInstance().currentMeasurement(),
                                R.string.MSG_IN_PROGRESS) + " " +
                                MessagingUtils.translate(R.string.MSG_REQUIRED_SECONDS) + " " +
                                Integer.toString(MeasurementsContextStrategy.getInstance().currentMeasurementForeseenSeconds())
                                        + " "
                                        + MessagingUtils.translate(R.string.MSG_REQUIRED_SECONDS_TRAILER));
                        pd.setMessage(toTimeString(MAX_SECONDS - pd.getProgress()) + " " + MessagingUtils.translate(R.string.MSG_REMAINING_SECONDS));
                    }
                }
                finally {
                    processDialogLock.unlock();
                }
            }
        };
    }
    boolean stopped = false;
    @Override
    protected Void doInBackground(Void... arg0) {
        while (this.keepWorking) {
            try {
                //Do something...
                Thread.sleep(SLEEP_TIME);
                handle.sendMessage(handle.obtainMessage());
                if (pd.getProgress() >= pd.getMax() && !stopped) {
                    stopped = true;
                    MessagingUtils.oKCancelDialog(R.string.MSG_ERROR, MessagingUtils.translate(R.string.ERR_WRONG_MEASUREMENT, R.string.MSG_RETRY_MISSING),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MeasurementsContextStrategy.getInstance().state().retryMissingMeasurements();
                                    pd.setProgress(0);
                                    stopped = false;
                                    handle.sendMessage(handle.obtainMessage());
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MeasurementsContextStrategy.getInstance().clearMeasurements();
                                    MeasurementsContextStrategy.getInstance().state().unbind(MeasurementsContextStrategy.getInstance());
                                    MeasurementsContextStrategy.getInstance().state().scan(MeasurementsContextStrategy.getInstance());
                                    ProcessingDialog.this.completed();
                                }
                            });
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.handle = null;
        processDialogLock.lock();
        try {
            this.pd.dismiss();
            this.pd = null;
        }
        finally {
            processDialogLock.unlock();
        }
        return null;
    }

    public void completed() {
        this.keepWorking = false;

    }

    @Override
    protected void onPostExecute(Void result) {
        if (pd!=null) {
            pd.dismiss();
        }
    }

    public static ProcessingDialog waitFor(Context context) {
        ProcessingDialog pd = new ProcessingDialog(context);
        pd.execute((Void[])null);
        return pd;
    }
}